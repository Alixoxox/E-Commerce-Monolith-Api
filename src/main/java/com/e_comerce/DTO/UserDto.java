package com.e_comerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDto{
        private Long id;
        private String name;
        private String email;
    }
}
