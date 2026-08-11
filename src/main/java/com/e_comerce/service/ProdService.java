package com.e_comerce.service;
import java.util.List;
import java.util.Optional;

import com.e_comerce.DTO.ProductSummary;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.Product;
import com.e_comerce.model.enums.OrderStatus;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdService {

    @Autowired
    private ProductRepo PR;

    public Page<ProductSummary> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PR.getSummary(pageable);
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
    public List<Product> ProdsByCategory(String cat){
        try{
           return PR.GetProdCategories(cat);
        }catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("No products found.");
        }
    }

    @Autowired
    private PastOrderRepo por;
    public Boolean updateItemStatus(Long OrderId, OrderStatus status){
        return por.updateOrderStatus(status, OrderId);
    }

    }
