package com.e_comerce.service;

import javax.naming.InsufficientResourcesException;
import java.util.List;
import java.util.Optional;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.model.Product;
import com.e_comerce.model.enums.category;
import com.e_comerce.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdService {

    @Autowired
    private ProductRepo PR;

    public Page<Product> getProducts(int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PR.findAll(pageable);
    }
    public Optional<Product> getOneProd(Long id){
        Optional<Product> prod =PR.findById(id);
        if(prod.isEmpty()){
            throw new RuntimeException("Product Not Found");
        }
        return prod;
    }
    public List<String> getCategories(){
        try {
            return PR.GetCategory();
        } catch (RuntimeException e) {
            throw new RuntimeException("No Categories Found");
        }
    }
    public List<Product> ProdsByCategory(category cat){
        try{
           return PR.GetProdCategories(cat);
        }catch(Exception e){
            throw new RuntimeException("No products found.");
        }
    }
}
