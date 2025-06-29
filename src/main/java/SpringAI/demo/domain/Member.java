package SpringAI.demo.domain;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;

@Entity
@Getter
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
}
