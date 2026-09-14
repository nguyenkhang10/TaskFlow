package com.example.taskflow.mapper;

import com.example.taskflow.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserStatus;


import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    int insert(User user);

    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(@Param("email") String email);

    List<User> search(@Param("keyword") String keyword,
                      @Param("role") UserRole role);

    List<User> searchAdmin(@Param("username") String username,
                           @Param("email") String email,
                           @Param("role") UserRole role,
                           @Param("status") UserStatus status);


    int update(User user);

    int deleteById(@Param("id") Long id);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    int softDeleteById(@Param("id") Long id);
}
