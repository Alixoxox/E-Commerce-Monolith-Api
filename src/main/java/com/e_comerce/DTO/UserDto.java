package com.e_comerce.DTO;

import lombok.Data;

public class UserDto {
    @Data
    public static class Request{
        private String name;
        private String password;
        private String email;
    }
    @Data
    public static class Login{
        private String password;
        private String email;
    }
}
