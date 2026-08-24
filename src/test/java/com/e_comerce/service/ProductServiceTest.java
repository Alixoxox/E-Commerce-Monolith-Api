package com.e_comerce.service;

import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_DEL_QUEUE;
import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_QUEUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import java.util.Optional;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.DTO.productDTOs;
import com.e_comerce.model.Product;
import com.e_comerce.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    ProductRepo productRepo;
    @Mock
    RabbitTemplate rabbitTemplate;
    @InjectMocks
    ProdService productService;

    @Test
    void createProductWithImage(){
        productDTOs.ProductDto prod =new productDTOs.ProductDto();
        UserDto.Attachment image = new UserDto.Attachment();

        Product savedProd=new Product();
        savedProd.setId(1L);

        when(productRepo.save(any(Product.class))).thenReturn(savedProd);

        productService.createProduct(prod,image);

        verify(productRepo).save(any(Product.class));
        verify(rabbitTemplate).convertAndSend(eq(IMAGE_QUEUE), any(UserDto.rateImg.class));
    }
     @Test
    void createProductWithoutImage(){
        productDTOs.ProductDto prod =new productDTOs.ProductDto();
        prod.setImage("dummy-Image");

        Product savedProd=new Product();
        savedProd.setId(1L);

        when(productRepo.save(any(Product.class))).thenReturn(savedProd);

        productService.createProduct(prod,null);

        verify(productRepo).save(any(Product.class));
        // when no img verify this is not called
        verify(rabbitTemplate, never()).convertAndSend(eq(IMAGE_QUEUE), any(UserDto.rateImg.class));
//        verify(productRepo,times(1)).deleteById(1L);

    }
@Test
void getProductsWithPriceAsc() {
    Pageable expectedPageable = PageRequest.of(0, 10, Sort.Direction.ASC,"price");
    Page<productDTOs.ProductSummary> page = Page.empty(); // get summary output
    when(productRepo.getSummary(any(Pageable.class),eq(""),isNull())).thenReturn(page);
    productService.getProducts(0, 10, null, null, "price-asc");
    verify(productRepo).getSummary(eq(expectedPageable), eq(""), isNull());
    }
    @Test
    void getProductsNegativePage() {
        assertThrows(IllegalArgumentException.class,
            () -> productService.getProducts(-1,-1,null,null,"price-asc"));
    }
    @Test
    void getZeroProd(){
        assertThrows(RuntimeException.class,
                ()-> productService.getOneProd(0L) );
    }
    @Test
    void getOneProduct(){
        Product testProduct =new Product();
        when(productRepo.findById(1L)).thenReturn(Optional.of(testProduct));
        Product result = productService.getOneProd(1L);
        verify(productRepo).findById(1L);
        assertEquals(testProduct,result);
    }
    @Test
    void UpdateProductWithImage(){
        productDTOs.ProductDto productDto = new productDTOs.ProductDto();
        productDto.setId(1L);
        Product product = new Product();
        product.setId(1L);
        product.setImage("products/old-image.jpg");
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        String oldImage = product.getImage();
        productService.updateProduct(productDto,new UserDto.Attachment());
        verify(productRepo).save(product);
        verify(rabbitTemplate).convertAndSend(eq(IMAGE_DEL_QUEUE),eq(oldImage)); //delete older image from s3 bucket -> donot use mutable object as expected val after method
        verify(rabbitTemplate).convertAndSend(eq(IMAGE_QUEUE), any(UserDto.rateImg.class));
    }
    @Test // when image link given
    void UpdateProductWithoutImage(){
        productDTOs.ProductDto productDto = new productDTOs.ProductDto();
        productDto.setId(1L);
        when(productRepo.findById(1L)).thenReturn(Optional.of(new Product()));
        productService.updateProduct(productDto,null);
        verify(productRepo).save(any(Product.class));
        // found an edge case if the image was passed null then got nullPointerException on update->apply
    }

    @Test
    void DeleteProductWithoutImage(){
        Product oldProduct = new Product();
        oldProduct.setImage("products/stuff.jpg");
        when(productRepo.findById(1L)).thenReturn(Optional.of(oldProduct));
        String url=oldProduct.getImage();
        productService.deleteProduct(1L);
        verify(productRepo).deleteById(1L);
        verify(rabbitTemplate).convertAndSend(eq(IMAGE_DEL_QUEUE), eq(url));
    }
    @Test
    void DeleteProductWithImage(){
        Product oldProduct = new Product();
        when(productRepo.findById(1L)).thenReturn(Optional.of(oldProduct));
        productService.deleteProduct(1L);
        verify(productRepo).deleteById(1L);
        verify(rabbitTemplate, never()).convertAndSend(eq(IMAGE_DEL_QUEUE), any(String.class));
    }

    @Test
    void applyPessimisticImage() throws Exception {
        // java reflections for private methods
       Method x = productService.getClass().getDeclaredMethod("apply",Product.class,productDTOs.ProductDto.class,Boolean.class);
       x.setAccessible(true);

       Product product = new Product();
       productDTOs.ProductDto dto = new productDTOs.ProductDto();
       Product result = (Product) x.invoke(productService,product,dto,true);
        assertNull(result.getImage());
    }

     @Test
    void applyViaImageLink() throws Exception {
        // java reflections for private methods
       Method x = productService.getClass().getDeclaredMethod("apply",Product.class,productDTOs.ProductDto.class,Boolean.class);
       x.setAccessible(true);

       Product product=new Product();
       productDTOs.ProductDto dto =new productDTOs.ProductDto();
       dto.setImage("some-image.jpg");
       Product result =(Product) x.invoke(productService,product,dto,false);
       assertEquals(product, result);
       assertEquals(dto.getImage(), result.getImage());
    }
}