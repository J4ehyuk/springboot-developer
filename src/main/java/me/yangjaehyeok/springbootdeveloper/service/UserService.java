package me.yangjaehyeok.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.domain.User;
import me.yangjaehyeok.springbootdeveloper.dto.AddUserRequest;
import me.yangjaehyeok.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // AddUserRequest 객체를 인수로 받는 회원 정보 추가 메서드
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 패스워드 암호화
                .build()
        ).getId();
    }
}
