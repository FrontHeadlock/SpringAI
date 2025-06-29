package SpringAI.demo.repository;

import SpringAI.demo.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  //이름
  Optional<Member> findByUsername(String username);

  //이메일
  Optional<Member> findByEmail(String email);

}
