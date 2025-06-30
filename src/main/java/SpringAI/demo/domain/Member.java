package SpringAI.demo.domain;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //이름
  @Column(nullable = false, length = 30)
  private String username;

  //비밀번호
  @Column(nullable = false)
  private String password;

  //UUID
//  @Column(unique = true, nullable = false, updatable = false)
//  private UUID uuid = UUID.randomUUID();

  //닉네임
  @Column(nullable = true, length = 30)
  private String name;

  //이메일
  @Column(nullable = false,length = 30)
  private String email;

  //이메일 인증 여부
  private boolean emailVerified;

  //이메일 인증 기반
  public void verifyEmail() {
    if (this.emailVerified) {
      throw new IllegalStateException("이미 이메일 인증이 완료되었습니다.");
    }
    this.emailVerified = true;
  }

  public boolean isEmailVerified() {
    return this.emailVerified;
  }

  //로그인 상태
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private MemberStatus status;

}
