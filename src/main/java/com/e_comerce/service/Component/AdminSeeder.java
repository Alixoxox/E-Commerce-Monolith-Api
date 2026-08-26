package com.e_comerce.service.Component;

import java.time.LocalDateTime;

import com.e_comerce.model.User;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

//    @Value("${spring.mail.adminMail}")
//    private String adminMail;
//
//    @Value("${spring.mail.adminPass}")
//    private String adminPas;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//    if(!userRepo.existsByEmail(adminMail)){
//        String hashedPass=passwordEncoder.encode(adminPas);
//        User user= new User(null,null,adminMail,hashedPass, LocalDateTime.now(), UserRole.ADMIN,null,null,null);
//        userRepo.save(user);
//    }
    }
}