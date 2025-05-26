package me.yangjaehyeok.springbootdeveloper.config.jwt;




import io.jsonwebtoken.Jwts;
import me.yangjaehyeok.springbootdeveloper.domain.User;
import me.yangjaehyeok.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest { // TokenProvider 클래스를 테스트하는 클래스
    @Autowired
    private TokenProvider tokenProvider;    // 테스트 할 대상
    @Autowired
    private UserRepository userRepository;  // 레포
    @Autowired
    private JwtProperties jwtProperties;    // 프로퍼티 값 저장 객체


    // 1. generateToken() 검증 테스트
    // =======================================================================================
    @DisplayName("generateToken() : 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given
        // 토큰에 유저 정보를 추가하기 위한 테스트 유저를 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when
        // 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }


    // 2. validToken() 검증 테스트
    // =======================================================================================
    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).
                        toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() : 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isTrue();
    }

    // 3. getAuthenticaiton() 검증 테스트
    @DisplayName("getAuthenticaiton() : 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthenticaiton() {
        // given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties); // String 타입 토큰 반환

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        // getPrincipal()은 로그인한 사용자의 객체(정보)를 반환하는 메서드
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).
                isEqualTo(userEmail);
    }

    // 4. getUserId() 검증 테스트
    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long userIdByToken = tokenProvider.getUserId(token);

        // then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
