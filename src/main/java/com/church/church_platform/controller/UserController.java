package com.church.church_platform.controller;

import com.church.church_platform.entity.User;
import com.church.church_platform.repository.UserRepository;
import com.church.church_platform.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "profilePic", user.getProfilePic() != null ?
                        user.getProfilePic() : ""
        ));
    }

    // Update profile picture
    @PutMapping("/update-profile-pic")
    public ResponseEntity<?> updateProfilePic(
            @RequestBody Map<String, String> body) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );

        user.setProfilePic(body.get("profilePic"));
        userRepository.save(user);

        // Return new token with updated info
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "profilePic", user.getProfilePic(),
                "message", "Profile picture updated! ✅"
        ));
    }
}