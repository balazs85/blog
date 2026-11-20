package com.example.blog.user.dto;

import com.example.blog.post.dto.PostSummary;

import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String email,
        List<PostSummary> posts
) { }
