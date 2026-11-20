package com.example.blog.user;

import com.example.blog.user.dto.UserRequest;
import com.example.blog.user.dto.UserResponse;
import com.example.blog.common.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    UserResponse toDto(User user);
    List<UserResponse> toDto(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User toEntity(UserRequest user);
}
