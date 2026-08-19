package org.intensive.profilehomework;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profiles")
    public List<Profile> profiles() {
        return profileService.getAllProfiles();
    }

    @GetMapping("/profiles/{id}")
    public Profile profileById(@PathVariable int id) {
        return profileService.getProfileById(id);
    }

    @PostMapping("/profiles")
    public Profile saveProfile(@RequestBody Profile profile) {
        profileService.saveNewProfile(profile);
        return profile;
    }

    @PutMapping("/profiles/{id}")
    public Profile updateProfile(@RequestBody Profile profile, @PathVariable int id) {
        profileService.updateProfileById(id, profile);
        return profile;
    }

    @DeleteMapping("/profiles/{id}")
    public void deleteProfile(@PathVariable int id) {
        profileService.deleteProfileById(id);
    }
}

