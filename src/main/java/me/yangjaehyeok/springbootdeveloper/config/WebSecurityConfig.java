package me.yangjaehyeok.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.yangjaehyeok.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig { // 실제 인증 처리를 하는 시큐리티 설정 파일.

    private final UserDetailService userService;

    // [스프링 시큐리티 기능 비활성화]
    // ================================================================================================================================
    @Bean
    public WebSecurityCustomizer configure(){
        // 스프링 시큐리티의 모든 기능을 사용하지 않게 설정, 정적 리소스(이미지, HTML 파일)에 설정
        return (web) -> web.ignoring()
                // h2의 데이터를 확인하는데 사용하는 h2-console 하위 url. 즉, 웹 브라우저에서 H2 DB에 접근하는 주소
                .requestMatchers(toH2Console())

                // 정적 리소스
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    // [특정 HTTP 요청에 대한 웹 기반 보안 구성]
    // ================================================================================================================================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                /*
                    requestMatchers : 특정 요청과 일치하는 url에 대한 엑세스 설정
                    permitAll       : 누구나 해당 url로 요청을 하면, 인증/인가 없이 접근 가능
                    anyRequest      : 위에서 설정한 url이외의 요청에 대해서 설정
                    authenticated   : 인가는 필요하지 않지만 인증이 성공된 상태에서 접근 가능
                 */
                
                .authorizeRequests(auth -> auth // 인증, 인가 설정
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll()                   // login, signup, user url로는 인증/인가 없이 접근 가능
                        .anyRequest().authenticated())  // 이외의 주소는 인증이 된 상태에서 접근 가능

                /*
                loginPage           : 로그인 페이지 경로 지정
                defaultSuccessUrl   : 로그인 완료 시, 경로
                 */
                .formLogin(formLogin -> formLogin // 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles")
                )

                /*
                logoutSuccessUrl        : 로그아웃이 완료 되었을때 이동할 경로
                invalidateHttpSession   : 로그아웃 이후에 세션을 전체 삭제할지 여부 설정
                 */
                .logout(logout -> logout // 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                .build();
    }

    // [인증 관리자 관련 설정]
    // ================================================================================================================================
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
     BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
        throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    // [패스워드 인코더로 사용할 빈 등록]
    // ================================================================================================================================
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}


