package me.yangjaehyeok.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
// 토큰을 생성, 토큰 유효성 검사, 토큰에서 필요한 정보 가져오는 클래스
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // 1. JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) { // 만료시간, 유저 정보
        Date now = new Date();

        return Jwts.builder() // set 계열의 메서드로 여러 값 지정.
                // [헤더] typ : JWT
                // ========================================================================
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)

                // [내용] iss : ajufresh@gmail.com(propertise 파일에서 설정한 값)
                // ========================================================================
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)              // 내용 iat : 현재 시간
                .setExpiration(expiry)         // 내용 exp : expiry 멤버 변숫값
                .setSubject(user.getEmail())   // 내용 sub : 유저의 이메일
                .claim("id", user.getId())  // 클레임 id : 유저 ID

                // [서명] : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                // ========================================================================
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // 2. JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    // 3. JWT 토큰을 파싱해 사용자 정보와 권한을 추출한 뒤, Spring Security에서 사용할 인증 객체(Authentication)를 생성하는 메서드
    // Authentication 객체 : Spring Security가 사용자 인증에 사용하는 객체
    public Authentication getAuthentication(String token) {
        // 프로퍼티즈 파일에 저장한 비밀값으로 토큰을 복호화한 다음, 클레임을 가져오는 메서드. getClaims();
        Claims claims = getClaims(token);

        // 사용자의 권한 정보를 만드는 부분
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new
                SimpleGrantedAuthority("ROLE_USER")); // 간단하게 "ROLE_USER" 하나만 권한으로 설정

        // User 객체는 Spring Security에서 제공하는 사용자 정보 객체
        /*
            UsernamePasswordAuthenticationToken은 인증된 사용자 정보를 담는 토큰.
                첫 번째 인자: 사용자 정보
                두 번째 인자: 자격 증명 (여기서는 token 자체)
                세 번째 인자: 권한 정보
         */
        return new UsernamePasswordAuthenticationToken(new org.springframework.
                security.core.userdetails.User(claims.getSubject(),
                "", authorities), token, authorities);
    }

    // 4. 토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

}
