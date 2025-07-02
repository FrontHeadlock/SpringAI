package SpringAI.demo.service;

import SpringAI.demo.domain.ChatAi;
import SpringAI.demo.repository.ChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRepository chatRepository;

  @Transactional
  public List<ChatAi> readAllChats(String userId){
    return chatRepository.findByUserIdOrderByCreatedAtAsc(userId);
  }

}
