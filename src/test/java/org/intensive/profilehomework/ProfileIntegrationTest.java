package org.intensive.profilehomework;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
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
        newProfile.setFirstName("Grace");
        newProfile.setLastName("Hopper");
        newProfile.setEmail("grace.integration@example.com");
        newProfile.setPhone("+380501234567");
        newProfile.setAvatar("grace.png");

        ResponseEntity<Profile> createResponse = restTemplate.postForEntity(url("/profiles"), newProfile, Profile.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).satisfies(profile -> {
            assertThat(profile.getFirstName()).isEqualTo("Grace");
            assertThat(profile.getEmail()).isEqualTo("grace.integration@example.com");
        });

        ResponseEntity<Profile[]> getResponse = restTemplate.getForEntity(url("/profiles"), Profile[].class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody())
                .extracting(Profile::getEmail)
                .contains("grace.integration@example.com");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
