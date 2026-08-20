package com.e_comerce.service;
import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_DEL_QUEUE;
import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_QUEUE;

import java.util.List;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.DTO.productDTOs;
import com.e_comerce.model.Product;
import com.e_comerce.model.enums.Category;
import com.e_comerce.repository.ProductRepo;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProdService {

    @Autowired
    private ProductRepo PR;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private S3Service s3Service;
    public Page<productDTOs.ProductSummary> getProducts(int page, int size, String search, Category category, String sort) {
        Sort s = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null) {
            switch (sort) {
                case "price-asc" -> s = Sort.by(Sort.Direction.ASC, "price");
                case "price-desc" -> s = Sort.by(Sort.Direction.DESC, "price");
                case "name-asc" -> s = Sort.by(Sort.Direction.ASC, "title");
                case "name-desc" -> s = Sort.by(Sort.Direction.DESC, "title");
                default -> { }
            }
        }
        Pageable pageable = PageRequest.of(page, size, s);
        Page<productDTOs.ProductSummary> products= PR.getSummary(pageable, search == null ? "" : search, category);
         return products.map(p -> convertToViewableImage(p));
    }

    @SneakyThrows
    @Transactional
    @CacheEvict(value = "ProdsByCategory", allEntries = true)
    public void createProduct(productDTOs.ProductDto dto, MultipartFile image) {
        Boolean key=false;
        Product product = new Product();
        if(!image.isEmpty()){
            UserDto.rateImg rateImg=new UserDto.rateImg(null, product.getId(), image,"products/");
            rabbitTemplate.convertAndSend(IMAGE_QUEUE,rateImg); //todo
            key=true;
        }
        Product newProd= apply(product, dto,key);
        PR.save(newProd);
    }
    @SneakyThrows
    @Transactional
    @CacheEvict(value = "ProdsByCategory", allEntries = true)
    public void updateProduct(productDTOs.ProductDto dto, MultipartFile image) {
        Product product = PR.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Product Not Found"));
        Boolean key = false;
        if(image != null && !image.isEmpty()){
            UserDto.rateImg rateImg=new UserDto.rateImg(null, product.getId(), image,"products/");
            rabbitTemplate.convertAndSend(IMAGE_QUEUE,rateImg);
            key = true;
        }
        Product newProd= apply(product, dto ,key);
        PR.save(newProd);
    }
    @Transactional
    @CacheEvict(value = "ProdsByCategory", allEntries = true)
    public void deleteProduct(Long id) {
        Product prod= PR.findById(id).orElseThrow(()-> new RuntimeException("Product Not Found"));
        rabbitTemplate.convertAndSend(IMAGE_DEL_QUEUE,prod.getImage());
        PR.deleteById(id);
    }
    @Cacheable(value="ProdsByCategory",key="#cat")
    public List<Product> ProdsByCategory(Category cat){
        try{
           return convertToViewAbleImage(PR.GetProdCategories(cat));

        }catch(Exception e){
            throw new RuntimeException("No products found.");
        }
    }
    private Product apply(Product product, productDTOs.ProductDto dto, Boolean Img) {
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        if(Img==true){
            product.setImage(null);
        } else {
            //image link
            product.setImage(dto.getImage());
        }
        product.setStock(dto.getStock());
        product.setPrice(dto.getPrice());
        return product;
    }

    public Product getOneProd(Long id){
        Product prod =PR.findById(id).orElseThrow(() -> new RuntimeException("Product Not Found"));
        return convertToViewAbleImage(prod);
    }

    // method Overloading -> Polymorphism
    private List<Product> convertToViewAbleImage(List<Product> products) {
    return products.stream().map(product -> convertToViewAbleImage(product)).toList();
    }
    private Product convertToViewAbleImage(Product p) {
        if(p==null){
            return null;
        }
        return new Product(
                   p.getId(),
                   p.getTitle(),
                   p.getDescription(),
                   p.getCategory(),
                   convertImg(p.getImage()),
                   p.getStock(),
                   p.getPrice(),
                   p.getWishlistedBy(),
                   p.getRatings(),
                   p.getOrderItems());
    }
    private productDTOs.ProductSummary convertToViewableImage(productDTOs.ProductSummary product) {
        return new productDTOs.ProductSummary(
        product.getId(),
        product.getTitle(),
        product.getPrice(),
        product.getDescription(),
        product.getCategory(),
        convertImg(product.getImage()),
        product.getStock(),
        product.getRate(),
        product.getCount()
    );
    }
    String convertImg(String key){
        if(key.startsWith("http")){ // some may be http or https
            return key;
        }
        // this also allows external Links to be viewable
        return key !=null ? s3Service.getPresignedUrl(key) : key;
    }
}