package me.yangjaehyeok.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.config.jwt.TokenProvider;
import me.yangjaehyeok.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
// 리프레시 토큰을 전달받아 검증하고,
// "유효한 리프레시 토큰"이라면 "새로운 엑세스 토큰"을 생성하는 [토큰 API]
public class TokenService {
    private final TokenProvider tokenProvider;
    
    // RefreshTokenRepository 보유
    private final RefreshTokenService refreshTokenService;

    // UserRepository 보유
    private final UserService userService;

    
    // 전달받은 refreshToken으로 토큰 유효성 검사를 진행하고, 
    // 유효한 토큰일 때, 리프레시 토큰으로 사용자 ID를 반환
    // 새로운 액세스 토큰 생성
    public String createNewAccessToken(String refreshToken) {

        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected token");
        }

        // 리프레시 토큰으로, 리프레시 토큰 레포에서 UserId 가져오기
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        // 리프레시 토큰 레포에서 가져온 UserId로 유저 레포에서 id로 user 검색
        User user = userService.findById(userId);

        // 가져온 User 객체로 토큰 생성
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }


}
