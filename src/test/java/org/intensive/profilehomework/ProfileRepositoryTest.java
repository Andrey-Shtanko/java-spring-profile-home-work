package org.intensive.profilehomework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProfileRepository.class)
@Sql("classpath:repository-test-data.sql")
class ProfileRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void shouldFindAllProfiles() {
        List<Profile> profiles = profileRepository.getAllProfiles();

        assertThat(profiles).singleElement().satisfies(profile -> {
            assertThat(profile.getFirstName()).isEqualTo("Ada");
            assertThat(profile.getLastName()).isEqualTo("Lovelace");
            assertThat(profile.getEmail()).isEqualTo("ada@example.com");
        });
    }

    @Test
    void shouldFindProfileById() {
        int id = profileRepository.getAllProfiles().get(0).getId();

        Profile profile = profileRepository.getProfileById(id);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail()).isEqualTo("ada@example.com");
    }

    @Test
    void shouldSaveNewProfile() {
        Profile saved = profileRepository.saveNewProfile("Grace", "Hopper", "grace@example.com", null, "grace.png");

        assertThat(saved.getId()).isPositive();

        Profile fetched = profileRepository.getProfileById(saved.getId());
        assertThat(fetched.getFirstName()).isEqualTo("Grace");
        assertThat(fetched.getEmail()).isEqualTo("grace@example.com");
    }

    @Test
    void shouldUpdateProfile() {
        int id = profileRepository.getAllProfiles().get(0).getId();

        profileRepository.updateProfileById(id, "Augusta", "King", "augusta@example.com", "+380501234567", "augusta.png");

        Profile profile = profileRepository.getProfileById(id);
        assertThat(profile.getFirstName()).isEqualTo("Augusta");
        assertThat(profile.getEmail()).isEqualTo("augusta@example.com");
    }

    @Test
    void shouldDeleteProfile() {
        int id = profileRepository.getAllProfiles().get(0).getId();

        profileRepository.deleteProfileById(id);

        assertThat(profileRepository.getAllProfiles()).isEmpty();
    }
}
