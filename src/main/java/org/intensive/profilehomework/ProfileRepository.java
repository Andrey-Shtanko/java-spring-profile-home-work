package org.intensive.profilehomework;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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

    public Profile saveNewProfile(String firstName, String lastName, String email, String phone, String avatar) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO profiles (first_name, last_name, email, phone, avatar) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, avatar);
            return ps;
        }, keyHolder);

        Profile profile = new Profile();
        profile.setId(keyHolder.getKey().intValue());
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setAvatar(avatar);
        return profile;
    }

    public void updateProfileById (int id, String firstName, String lastName, String email, String phone, String avatar) {
        jdbcTemplate.update("UPDATE profiles SET first_name = ?, last_name = ?, email = ?, phone = ?, avatar = ? WHERE id = ?",
                firstName, lastName, email, phone, avatar, id);
    }

    public void deleteProfileById (int id) {
        jdbcTemplate.update("DELETE FROM profiles WHERE id = ?", id);
    }
}
