package com.church.church_platform.service;

import com.church.church_platform.dto.request
        .GoogleAuthRequest;
import com.church.church_platform.dto.response
        .AuthResponse;
import com.church.church_platform.entity.User;
import com.church.church_platform.repository
        .UserRepository;
import com.church.church_platform.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2
        .GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2
        .GoogleIdTokenVerifier;
import com.google.api.client.http.javanet
        .NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation
        .Value;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    public AuthResponse googleLogin(
            GoogleAuthRequest request) {

        try {
            // Step 1 → Verify Google token
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            new GsonFactory()
                    )
                            .setAudience(Collections
                                    .singletonList(googleClientId))
                            .build();

            GoogleIdToken idToken = verifier
                    .verify(request.getToken());

            if (idToken == null) {
                throw new RuntimeException(
                        "Invalid Google token!"
                );
            }

            // Step 2 → Get user info from Google
            GoogleIdToken.Payload payload =
                    idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload
                    .get("name");
            String profilePic = (String) payload
                    .get("picture");

            // Step 3 → Check if user exists
            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user == null) {
                // New user → Create account

                // Determine role
                User.Role role = User.Role.MEMBER;
                if (request.getRole() != null &&
                        request.getRole()
                                .equals("CHURCH_ADMIN")) {
                    role = User.Role.CHURCH_ADMIN;
                }

                // Create user with random password
                // (they login via Google so
                //  password doesn't matter)
                user = User.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(
                                UUID.randomUUID().toString()
                        ))
                        .role(role)
                        .profilePic(profilePic)
                        .enabled(true)
                        .build();

                userRepository.save(user);

            } else {
                // Existing user → Update profile pic
                // from Google if not set
                if (user.getProfilePic() == null
                        && profilePic != null) {
                    user.setProfilePic(profilePic);
                    userRepository.save(user);
                }
            }

            // Step 4 → Generate JWT token
            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );

            // Step 5 → Return response
            return AuthResponse.builder()
                    .token(token)
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .profilePic(user.getProfilePic())
                    .message("Login successful!")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Google login failed: " +
                            e.getMessage()
            );
        }
    }
}