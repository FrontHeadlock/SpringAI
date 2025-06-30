package SpringAI.demo.service;

import SpringAI.demo.domain.EmailAuthCode;
import java.security.SecureRandom;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

  private final EmailAuthCode codeStore;
  private final JavaMailSender mailSender;
  private final Random random = new SecureRandom();

  //이메일 인증 시간
  private static final long EMAIL_CODE_TTL = 5*60;

  //이메일 인증 요청 로직
  public void request(String email){
    if (!email.endsWith("@catholic.ac.kr")) {
      throw new IllegalArgumentException("학교 이메일이 아닙니다.");
    }

    String code = String.format("%06d", random.nextInt(1_000_000));

    // 이메일 발송
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("카톡시 회원가입 인증 코드");
    message.setText("인증 코드는 다음과 같습니다: " + code);
    mailSender.send(message);

    codeStore.saveCode(email, code);
  }

  //이메일 인증 검증 로직
  public boolean verifyAuthCode(String email, String code){
    return codeStore.verifyCode(email,code);
  }


}
