package org.intensive.profilehomework;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final String firstNameSuffix;

    public ProfileService(ProfileRepository profileRepository,
                          @Value("${profile.first-name-suffix:}") String firstNameSuffix) {
        this.profileRepository = profileRepository;
        this.firstNameSuffix = firstNameSuffix == null ? "" : firstNameSuffix;
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.getAllProfiles().stream().map(this::withSuffix).toList();
    }

    public Profile getProfileById(int id) {
        return withSuffix(profileRepository.getProfileById(id));
    }

    public void deleteProfileById(int id) {
        profileRepository.deleteProfileById(id);
    }

    public Profile updateProfileById(int id, Profile profile) {
        profileRepository.updateProfileById(id, profile.getFirstName(), profile.getLastName(), profile.getEmail(), profile.getPhone(), profile.getAvatar());
        return withSuffix(profileRepository.getProfileById(id));
    }

    public Profile saveNewProfile(Profile profile) {
        Profile saved = profileRepository.saveNewProfile(profile.getFirstName(), profile.getLastName(), profile.getEmail(), profile.getPhone(), profile.getAvatar());
        return withSuffix(saved);
    }

    private Profile withSuffix(Profile profile) {
        String firstName = profile.getFirstName();
        if (firstName == null || firstNameSuffix.isEmpty() || firstName.endsWith(firstNameSuffix)) {
            return profile;
        }
        profile.setFirstName(firstName + firstNameSuffix);
        return profile;
    }
}
