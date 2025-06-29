package SpringAI.demo.login;

import jakarta.persistence.*;

@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


}
