package SpringAI.demo.login.config;

import SpringAI.demo.login.jwt.JwtFilter;
import SpringAI.demo.login.jwt.JwtUtil;
import SpringAI.demo.login.jwt.LoginFilter;
import SpringAI.demo.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtConfig jwtConfig;

  private final MemberRepository memberRepository;

  // Authentication Manager Bean 등록 -> UsernamePasswordAuthenticationFilter에서 필요
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtConfig jwtConfig) throws Exception {

    //csrf disable
    http
        .csrf(AbstractHttpConfigurer::disable);

    //cors 설정 (배포 X)
    http
        .cors(cors -> cors.disable());

    //Form 로그인 방식 disable -> Custom하게 설정
    http
        .formLogin((auth) -> auth.disable());

    //http basic 인증 방식 disable
    http
        .httpBasic((auth) -> auth.disable());

    //경로별 인가 작업
    http
        .authorizeHttpRequests((auth)-> auth
            .requestMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger 허용
            .requestMatchers("/login","/","/signUp").permitAll()
            .anyRequest().permitAll()
        );

    // jwt토큰 요청 감지, 해당 토큰 유효성 검사 (jwtFilter 등록)
    // loginFilter 이전에 실행, 로그인 요청 외 모든 요청에서 JWT 검증 처리 가능
    http
        .addFilterBefore(new JwtFilter(jwtUtil,jwtConfig,memberRepository), LoginFilter.class);


    // '/login' 에 동작, 사용자의 이메일/비밀번호를 받아 인증 (loginFilter 등록)
    // usernamePasswordAuthenticationFilter 자리에서 동작
    http
        .addFilterBefore((new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, jwtConfig,memberRepository)), UsernamePasswordAuthenticationFilter.class);

    // 세션 설정
    // JWT -> Session 항상 Stateless 상태로 둬야 함
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    return http.build();
  }

}
