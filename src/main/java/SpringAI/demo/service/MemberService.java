package SpringAI.demo.service;

import SpringAI.demo.domain.Member;
import SpringAI.demo.domain.MemberStatus;
import SpringAI.demo.dto.SignUpDTO;
import SpringAI.demo.repository.MemberRepository;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.memberRepository = memberRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public Long signUp(SignUpDTO dto) {

    // 유효성은 컨트롤러에서 @Valid 처리
    Member member = Member.builder()
        .username(dto.getMembername())
        .password(bCryptPasswordEncoder.encode(dto.getPassword()))
        .email(dto.getEmail())
        .status(MemberStatus.PENDING)
        .build();

    try {
      Member saved = memberRepository.save(member);
      return saved.getId();
    } catch (DataIntegrityViolationException ex) {
      throw new UsernameNotFoundException(member.getUsername());
    }
  }

}
