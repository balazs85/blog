package com.example.blog.user;

import com.example.blog.user.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_mapsRequestAndKeepsGeneratedFieldsUnset() {
        User user = userMapper.toEntity(new UserRequest("Test", "test@example.com"));

        assertThat(user.getName()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isNull();
        assertThat(user.getPosts()).isEmpty();
    }
}
