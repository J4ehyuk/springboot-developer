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

    // 전달받은 유저 ID로 유저를 검색해서 전달하는 메서드
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
