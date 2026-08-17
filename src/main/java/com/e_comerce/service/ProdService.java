package com.e_comerce.service;
import java.util.List;
import java.util.Optional;

import com.e_comerce.DTO.ProductSummary;
import com.e_comerce.model.Product;
import com.e_comerce.model.enums.OrderStatus;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProdService {

    @Autowired
    private ProductRepo PR;

    @Autowired
    private RedisTemplate redisTemplate; // fasten the queries i.e remove find / entityManger with redis

    @Cacheable(value="getProducts",key="#page + ':' + #size")
    public Page<ProductSummary> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PR.getSummary(pageable);
    }
    @Cacheable(value = "Product",key="#id")
    public Optional<Product> getOneProd(Long id){
        Optional<Product> prod =PR.findById(id);
        if(prod.isEmpty()){
            throw new RuntimeException("Product Not Found");
        }
        return prod;
    }
    @Cacheable(value = "Categories")
    public List<String> getCategories(){
        try {
            return PR.GetCategory();
        } catch (RuntimeException e) {
            throw new RuntimeException("No Categories Found");
        }
    }
    @Cacheable(value="ProdsByCategory",key="#cat")
    public List<Product> ProdsByCategory(String cat){
        try{
           return PR.GetProdCategories(cat);
        }catch(Exception e){
            throw new RuntimeException("No products found.");
        }
    }

    @Autowired
    private PastOrderRepo por;

    }
