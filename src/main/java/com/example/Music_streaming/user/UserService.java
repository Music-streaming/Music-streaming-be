package com.example.Music_streaming.user;

import com.example.Music_streaming.config.JwtTokenProvider;
import com.example.Music_streaming.spotify.dto.SpotifyTokenRequest;
import com.example.Music_streaming.user.dto.LoginRequestDto;
import com.example.Music_streaming.user.dto.LoginResponseDto;
import com.example.Music_streaming.user.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Long register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .username(dto.username())
                .build();

        return userRepository.save(user).getId();
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return new LoginResponseDto(token, user.getUsername(), user.getEmail());
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Transactional
    public void updateSpotifyTokens(SpotifyTokenRequest request) {
        if (request == null || request.accessToken() == null || request.accessToken().isBlank()) {
            throw new IllegalArgumentException("Spotify Access Token이 필요합니다.");
        }
        User user = getCurrentUser();
        Instant expiresAt = request.expiresIn() == null
                ? null
                : Instant.now().plusSeconds(request.expiresIn());
        user.updateSpotifyTokens(request.accessToken(), request.refreshToken(), expiresAt);
    }

    public String getCurrentUserSpotifyAccessToken() {
        User user = getCurrentUser();
        if (!user.hasValidSpotifyAccessToken()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Spotify 토큰이 없거나 만료되었습니다.");
        }
        return user.getSpotifyAccessToken();
    }
}
