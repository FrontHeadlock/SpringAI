package SpringAI.demo.repository;

import SpringAI.demo.domain.ChatAi;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatAi,Long> {

  List<ChatAi> findByUserIdOrderByCreatedAtAsc(String userId);
}
