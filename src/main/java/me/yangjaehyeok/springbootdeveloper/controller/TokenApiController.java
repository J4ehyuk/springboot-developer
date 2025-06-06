package me.yangjaehyeok.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.yangjaehyeok.springbootdeveloper.dto.CreateAccessTokenResponse;
import me.yangjaehyeok.springbootdeveloper.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token") // 리프레시 토큰을 기반으로 새로운 엑세스 토큰 생성
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
            (@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
