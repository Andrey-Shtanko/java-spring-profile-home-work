package org.intensive.profilehomework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ProfileIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndReadProfileThroughApplicationLayers() {
        Profile newProfile = new Profile();
        newProfile.setId(12345);
        newProfile.setFirstName("Grace");
        newProfile.setLastName("Hopper");
        newProfile.setEmail("grace.integration@example.com");
        newProfile.setPhone("+380501234567");
        newProfile.setAvatar("grace.png");

        ResponseEntity<String> createResponse = restTemplate.postForEntity(url("/profiles"), newProfile, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).satisfies(body -> {
            JsonNode json = readJson(body);
            assertThat(json.path("id").asInt()).isPositive().isNotEqualTo(12345);
            assertThat(json.path("firstName").asText()).isEqualTo("Grace");
            assertThat(json.path("email").asText()).isEqualTo("grace.integration@example.com");
        });

        ResponseEntity<String> getResponse = restTemplate.getForEntity(url("/profiles"), String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode profiles = readJson(getResponse.getBody());
        assertThat(profiles.isArray()).isTrue();
        assertThat(profiles)
                .extracting(json -> json.path("email").asText())
                .contains("grace.integration@example.com");
        assertThat(profiles)
                .extracting(json -> json.path("id").asInt())
                .doesNotContain(12345);
    }

    private JsonNode readJson(String body) {
        try {
            return new ObjectMapper().readTree(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid JSON response: " + body, e);
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
