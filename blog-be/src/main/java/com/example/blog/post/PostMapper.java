package com.example.blog.post;

import com.example.blog.post.dto.PostResponse;
import com.example.blog.common.config.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PostMapper {
    PostResponse toDto(Post post);
    List<PostResponse> toDto(List<Post> posts);
}
