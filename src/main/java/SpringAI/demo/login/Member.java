package SpringAI.demo.login;

import jakarta.persistence.*;

@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //이름
  @Column(nullable = false, length = 30)
  private String username;

  //비밀번호
  @Column(nullable = false, length = 20)
  private String password;

  //닉네임
  @Column(nullable = false, length = 30)
  private String name;

  //이메일
  @Column(nullable = false,length = 30)
  private String email;
}
