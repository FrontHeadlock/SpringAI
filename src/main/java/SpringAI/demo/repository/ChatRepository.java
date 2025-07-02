package SpringAI.demo.repository;

import SpringAI.demo.domain.ChatAi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatAi,Long> {

}
