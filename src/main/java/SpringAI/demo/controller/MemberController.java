package SpringAI.demo.controller;

import SpringAI.demo.dto.AuthDTO;
import SpringAI.demo.dto.AuthDTO.LoginResponse;
import SpringAI.demo.dto.SignUpDTO;
import SpringAI.demo.login.config.JwtConfig;
import SpringAI.demo.login.jwt.JwtUtil;
import SpringAI.demo.service.EmailService;
import SpringAI.demo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/m")
public class MemberController {

  private final MemberService memberService;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final JwtConfig jwtConfig;

  //이메일 전송
  @PostMapping("/email/request")
  public ResponseEntity<Void> request(@RequestParam String email) {
    emailService.request(email);
    return ResponseEntity.ok().build();
  }

  //이메일 검증
  @PostMapping("/email/verify")
  public ResponseEntity<Boolean> verifyCode(@RequestParam String email, @RequestParam String code) {
    boolean result = emailService.verifyAuthCode(email, code);
    return ResponseEntity.ok(result);
  }

  //회원가입
  public ResponseEntity<ApiResponse> signUp(@RequestBody @Valid SignUpDTO dto) {
    memberService.signUp(dto); // 내부에서 PENDING 상태로 저장

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "Login API")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthDTO.LoginRequest request) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        )
    );

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    // 사용자 정보 추출
    String username = userDetails.getUsername();
    String role = userDetails.getAuthorities()
        .stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse("ROLE_USER"); // 기본 권한 처리

    // JWT 생성
    String token = jwtUtil.createJwt("access", username, role,
        jwtConfig.getAccessTokenValidityInSeconds() // 유효 시간
    );

    return ResponseEntity.ok(new LoginResponse(token));
  }

}