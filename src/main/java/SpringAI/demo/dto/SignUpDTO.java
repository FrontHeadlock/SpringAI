package SpringAI.demo.dto;

import SpringAI.demo.domain.MemberStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpDTO {

  private String Membername;
  private String password;
  private String email;
  private MemberStatus status;

}
