package com.e_comerce.repository;
import java.util.Optional;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT new com.e_comerce.DTO.UserDto$UserSummaryDto(u.id, u.name, u.email) FROM User u WHERE u.email = :email")
    Optional<UserDto.UserSummaryDto> findByEmailPassCustom(@Param("email") String email);

    Optional<User> findByEmail(@Param("email") String email);
}
