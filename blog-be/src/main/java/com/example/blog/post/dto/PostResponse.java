package com.example.blog.post.dto;

import com.example.blog.user.dto.UserSummary;

public record PostResponse(
        Long id,
        String title,
        String text,
        UserSummary creator
) {
}
