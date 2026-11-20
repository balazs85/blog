package com.example.blog.user;

import com.example.blog.post.Post;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    void findAll_loadsPostsInSameQuery() {
        User user = new User();
        user.setName("TestName");
        Post post = new Post();
        post.setTitle("TestTitle");
        post.setText("TestText");
        post.setCreator(user);
        user.getPosts().add(post);
        userRepository.save(user);

        em.flush();   // write to the DB
        em.clear();   // clear the cache. Otherwise, test would not prove anything

        List<User> users = userRepository.findAll();

        assertThat(users)
                .isNotEmpty()
                .allSatisfy(u ->
                    assertThat(Hibernate.isInitialized(u.getPosts())).isTrue()
                );
    }
}
