package org.intensive.profilehomework;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.getAllProfiles();
    }

    public Profile getProfileById(int id) {
        return profileRepository.getProfileById(id);
    }

    public void deleteProfileById(int id) {
        profileRepository.deleteProfileById(id);
    }

    public void updateProfileById(int id, Profile profile) {
        profileRepository.updateProfileById(id, profile.getFirstName(), profile.getLastName(), profile.getEmail(), profile.getPhone(), profile.getAvatar());
    }

    public void saveNewProfile(Profile profile) {
        profileRepository.saveNewProfile(profile.getFirstName(), profile.getLastName(), profile.getEmail(), profile.getPhone(), profile.getAvatar());
    }
}
