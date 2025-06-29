package SpringAI.demo.login.jwt;

import SpringAI.demo.domain.Member;
import SpringAI.demo.dto.AuthDTO;
import SpringAI.demo.dto.CustomUserDetails;
import SpringAI.demo.login.config.JwtConfig;
import SpringAI.demo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final JwtConfig jwtConfig;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final MemberRepository memberRepository;

  // UsernamePasswordAuthenticationFilter에서 상속
  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("로그인 시도 : {}", request.getRequestURI());
    try {

      // JSON 요청 본문을 LoginRequestDto로 매핑
      // Login 요청 전문 DTO
      AuthDTO.LoginRequest loginRequestDto = objectMapper.readValue(request.getInputStream(), AuthDTO.LoginRequest.class);

      String username = loginRequestDto.username();
      String password = loginRequestDto.password();

      log.info("로그인 시도 User 명 : {}",username);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

      // 인증 시도
      return authenticationManager.authenticate(authToken);
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // 로그인 성공시 메서드
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
    log.info("로그인 성공");

    // 유저 정보 가져오기 (authentication 객체에서 username 추출)
    String username = authentication.getName();

    // 사용자 권한을 추출 -> 권한 부여는 따로 하지 않을 것이기 때문에 기본값 ROLE_USER 세팅
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.hasNext() ? iterator.next() : null;
    String role = auth != null ? auth.getAuthority() : "ROLE_USER";
    log.info("사용자 권한 추출");

    // JWT Access Token 생성 (Refresh Token,Cookie 설정 추가적으로 필요)
    String access = jwtUtil.createJwt("access",username,role, jwtConfig.getAccessTokenValidityInSeconds());
    log.info("Access Token 생성");

    // 응답 헤더에 토큰 설정
    response.setHeader("Authorization", "Bearer " + access);

    // 유저 정보 조회
    Member byUsername = memberRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    memberRepository.save(byUsername);

    // JSON 응답 구성
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    var responseBody = new LinkedHashMap<String, Object>();
    responseBody.put("isSuccess", true);
    responseBody.put("code", "COMMON200");
    responseBody.put("message", "성공입니다");

    var result = new LinkedHashMap<String, Object>();
    result.put("name", byUsername.getUsername());
    result.put("nickname", byUsername.getName());
    result.put("memberId", byUsername.getId());

    responseBody.put("result", result);

    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(responseBody));

    response.setStatus(HttpStatus.OK.value());
  }

  // 로그인 실패 메서드
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    log.info("로그인 실패");
    response.setStatus(401);
  }

}