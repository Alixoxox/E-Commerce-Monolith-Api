package com.e_comerce.service;

import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_QUEUE;
import static org.mockito.Mockito.*;

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

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    ProductRepo productRepo;
    @Mock
    RabbitTemplate rabbitTemplate;
    @InjectMocks
    ProdService productService;

    @Test
    void createProductWithImageTest(){
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
    void createProductWithoutImageTest(){
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
    
}
