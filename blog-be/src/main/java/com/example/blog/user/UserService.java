package com.example.blog.user;

import com.example.blog.common.exception.ResourceNotFoundException;
import com.example.blog.user.dto.UserRequest;
import com.example.blog.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsers() {
        return userMapper.toDto(userRepository.findAll());
    }

    public UserResponse getUserById(long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    @Transactional
    public UserResponse createUser(UserRequest input) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(input)));
    }
}
