package com.e_comerce.service;

import java.util.List;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.User;
import com.e_comerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo UR;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<User> FetchUserData() {
        return UR.findAll();
    }

    public User CreateUser(UserDto.Request UDR) {
        //instantiate new User
        try {
            String HashedPass= passwordEncoder.encode(UDR.getPassword());
            User userM = new User(null, UDR.getName(), UDR.getEmail(), HashedPass, null, null, null, null);
            return UR.save(userM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Create User\nTry Again Later.");
        }
    }
    public User LoginUser(UserDto.Login UDR){
        try{
            String pass=passwordEncoder.encode(UDR.getPassword());
            User userM= UR.findByEmailPassCustom(UDR.getEmail());
            if(userM==null){
                throw new RuntimeException("Email Not Found");
            }
            boolean x = passwordEncoder.matches(pass, userM.getPassword());
            if(!x){
                throw new RuntimeException("Password does not match\nTry Again");
            }
            return userM;
        } catch (Exception e) {
            throw new RuntimeException("Incorrect Credentials.\nTry Again");
        }
    }
}
