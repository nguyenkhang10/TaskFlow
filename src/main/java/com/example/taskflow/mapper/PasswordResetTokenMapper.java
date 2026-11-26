package com.example.taskflow.mapper;

import com.example.taskflow.domain.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PasswordResetTokenMapper {
    int insert(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    int markUsed(@Param("id") Long id);

    int invalidateAllByUserId(@Param("userId") Long userId);
}