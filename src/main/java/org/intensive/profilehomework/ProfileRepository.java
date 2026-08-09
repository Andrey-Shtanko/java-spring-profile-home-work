package org.intensive.profilehomework;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Profile> getAllProfiles() {
        return jdbcTemplate.query("SELECT * FROM profiles", (rs, rowNum) -> {
            Profile profile = new Profile();
            profile.setId(rs.getInt("id"));
            profile.setFirstName(rs.getString("first_name"));
            profile.setLastName(rs.getString("last_name"));
            profile.setEmail(rs.getString("email"));
            profile.setPhone(rs.getString("phone"));
            profile.setAvatar(rs.getString("avatar"));

            return profile;
        });
    }

    public Profile getProfileById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM profiles WHERE id = ?", (rs, rowNum) -> {
            Profile profile = new Profile();
            profile.setId(rs.getInt("id"));
            profile.setFirstName(rs.getString("first_name"));
            profile.setLastName(rs.getString("last_name"));
            profile.setEmail(rs.getString("email"));
            profile.setPhone(rs.getString("phone"));
            profile.setAvatar(rs.getString("avatar"));

            return profile;
        }, id);
    }

    public void saveNewProfile (String firstName, String lastName, String email, String phone, String avatar) {
        jdbcTemplate.update("INSERT INTO profiles (first_name, last_name, email, phone, avatar) VALUES (?, ?, ?, ?, ?)",
                firstName, lastName, email, phone, avatar);
    }

    public void updateProfileById (int id, String firstName, String lastName, String email, String phone, String avatar) {
        jdbcTemplate.update("UPDATE profiles SET first_name = ?, last_name = ?, email = ?, phone = ?, avatar = ? WHERE id = ?",
                firstName, lastName, email, phone, avatar, id);
    }

    public void deleteProfileById (int id) {
        jdbcTemplate.update("DELETE FROM profiles WHERE id = ?", id);
    }
}
