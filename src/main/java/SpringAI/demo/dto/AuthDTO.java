package SpringAI.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

  public static record LoginRequest(
      @NotBlank String username,
      @NotBlank String password
  ){}

  public static record LoginResponse(){}

}
