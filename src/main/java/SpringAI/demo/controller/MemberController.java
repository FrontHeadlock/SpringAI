package SpringAI.demo.controller;

import SpringAI.demo.domain.Member;
import SpringAI.demo.login.jwt.JwtUtil;
import SpringAI.demo.service.EmailService;
import SpringAI.demo.service.MemberService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final JwtUtil jwtUtil;

  //이메일 인증 기반
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

  //조회


}
