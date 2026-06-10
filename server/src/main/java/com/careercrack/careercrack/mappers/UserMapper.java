package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.UserResponse;
import com.careercrack.careercrack.models.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
    List<UserResponse> toDto(List<User> users);
}
