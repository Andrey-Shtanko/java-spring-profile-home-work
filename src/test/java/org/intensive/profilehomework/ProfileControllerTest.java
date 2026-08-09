package org.intensive.profilehomework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Test
    void shouldGetAllProfiles() throws Exception {
        Profile profile = profile(1, "Ada", "Lovelace", "ada@example.com");
        when(profileService.getAllProfiles()).thenReturn(List.of(profile));

        mockMvc.perform(get("/profiles"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":1,"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}]
                        """));
    }

    @Test
    void shouldGetProfileById() throws Exception {
        when(profileService.getProfileById(1)).thenReturn(profile(1, "Ada", "Lovelace", "ada@example.com"));

        mockMvc.perform(get("/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":1,"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}
                        """));
    }

    @Test
    void shouldCreateProfile() throws Exception {
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Grace","lastName":"Hopper","email":"grace@example.com","phone":"+380501234567","avatar":"grace.png"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"firstName":"Grace","lastName":"Hopper","email":"grace@example.com","phone":"+380501234567","avatar":"grace.png"}
                        """));

        verify(profileService).saveNewProfile(org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        mockMvc.perform(put("/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Augusta","lastName":"King","email":"augusta@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"firstName":"Augusta","lastName":"King","email":"augusta@example.com"}
                        """));

        verify(profileService).updateProfileById(org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void shouldDeleteProfile() throws Exception {
        doNothing().when(profileService).deleteProfileById(1);

        mockMvc.perform(delete("/profiles/1"))
                .andExpect(status().isOk());

        verify(profileService).deleteProfileById(1);
    }

    private static Profile profile(int id, String firstName, String lastName, String email) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);
        return profile;
    }
}
