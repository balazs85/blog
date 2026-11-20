package com.example.blog.user;

import com.example.blog.common.exception.ResourceNotFoundException;
import com.example.blog.user.dto.UserRequest;
import com.example.blog.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    public static final String TEST_NAME = "TestName";
    public static final String TEST_EMAIL = "test@email.com";
    public static final long TEST_PRESENT_ID = 1L;
    public static final String TEST_WRONG_ID = "abc";
    public static final long TEST_NOT_FOUND_ID = 999L;

    // fake HTTP request without server
    @Autowired
    MockMvc mockMvc;

    // User service is mocked
    @MockitoBean
    UserService userService;

    @Test
    void getUser_returns200Success() throws Exception {
        UserResponse mockResponse = new UserResponse(TEST_PRESENT_ID, TEST_NAME, TEST_EMAIL, List.of());
        when(userService.getUserById(TEST_PRESENT_ID))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/users/" + TEST_PRESENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(TEST_NAME));
    }

    @Test
    void getUser_returns400AsProblemDetail() throws Exception {
        mockMvc.perform(get("/api/users/" + TEST_WRONG_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: '" + TEST_WRONG_ID + "'"));
    }

    @Test
    void getUser_returns404AsProblemDetail() throws Exception {
        when(userService.getUserById(TEST_NOT_FOUND_ID))
                .thenThrow(new ResourceNotFoundException(User.class, TEST_NOT_FOUND_ID));

        mockMvc.perform(get("/api/users/" + TEST_NOT_FOUND_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("User not found: " + TEST_NOT_FOUND_ID));
    }

    @Test
    void getUser_returns500AsProblemDetail() throws Exception {
        when(userService.getUserById(TEST_PRESENT_ID))
                .thenThrow(new RuntimeException("Simulated technical error"));

        mockMvc.perform(get("/api/users/" + TEST_PRESENT_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"));
    }

    // --- POST /api/users ---

    @Test
    void createUser_returns201WithLocationAndBody() throws Exception {
        UserRequest request = new UserRequest(TEST_NAME, TEST_EMAIL);
        when(userService.createUser(request))
                .thenReturn(new UserResponse(TEST_PRESENT_ID, TEST_NAME, TEST_EMAIL, List.of()));

        postUser("""
                {"name": "%s", "email": "%s"}
                """.formatted(TEST_NAME, TEST_EMAIL))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/api/users/" + TEST_PRESENT_ID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_PRESENT_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void createUser_returns400_whenNameBlank() throws Exception {
        postUser("""
                {"name": "", "email": "%s"}
                """.formatted(TEST_EMAIL))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        // invalid input must not reach the service layer
        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_returns400_whenEmailInvalid() throws Exception {
        postUser("""
                {"name": "%s", "email": "not-an-email"}
                """.formatted(TEST_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_returns400_whenFieldsMissing() throws Exception {
        postUser("{}")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_returns400_whenBodyMissing() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verify(userService, never()).createUser(any());
    }

    private ResultActions postUser(String json) throws Exception {
        return mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
