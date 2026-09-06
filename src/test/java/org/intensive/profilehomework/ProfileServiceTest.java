package org.intensive.profilehomework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=never"
})
@ActiveProfiles("prod")
class ProfileServiceTest {

    @TestConfiguration
    static class MockRepositoryConfig {
        @Bean
        ProfileRepository profileRepository() {
            return mock(ProfileRepository.class);
        }
    }

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void shouldSaveCleanFirstNameButReturnWithSuffix() {
        org.mockito.Mockito.when(profileRepository.saveNewProfile(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull()))
                .thenAnswer(invocation -> {
                    Profile p = new Profile();
                    p.setId(7);
                    p.setFirstName(invocation.getArgument(0));
                    p.setLastName(invocation.getArgument(1));
                    p.setEmail(invocation.getArgument(2));
                    return p;
                });

        Profile saved = profileService.saveNewProfile(profile("Grace", "Hopper", "grace@example.com"));

        // у БД записується чисте ім'я
        org.mockito.Mockito.verify(profileRepository).saveNewProfile(
                org.mockito.ArgumentMatchers.eq("Grace"),
                org.mockito.ArgumentMatchers.eq("Hopper"),
                org.mockito.ArgumentMatchers.eq("grace@example.com"),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull());

        // у відповіді API — ім'я з суфіксом
        assertThat(saved.getFirstName()).isEqualTo("Grace(Prod)");
    }

    @Test
    void shouldSaveCleanFirstNameOnUpdateButReturnWithSuffix() {
        org.mockito.Mockito.when(profileRepository.getProfileById(1)).thenReturn(profile("Grace", "Hopper", "grace@example.com"));

        Profile updated = profileService.updateProfileById(1, profile("Grace", "Hopper", "grace@example.com"));

        // у БД записується чисте ім'я
        org.mockito.Mockito.verify(profileRepository).updateProfileById(
                org.mockito.ArgumentMatchers.eq(1),
                org.mockito.ArgumentMatchers.eq("Grace"),
                org.mockito.ArgumentMatchers.eq("Hopper"),
                org.mockito.ArgumentMatchers.eq("grace@example.com"),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull());

        // у відповіді API — ім'я з суфіксом
        assertThat(updated.getFirstName()).isEqualTo("Grace(Prod)");
    }

    private static Profile profile(String firstName, String lastName, String email) {
        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);
        return profile;
    }
}
