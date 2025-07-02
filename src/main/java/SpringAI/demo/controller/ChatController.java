package SpringAI.demo.controller;

import SpringAI.demo.domain.ChatAi;
import SpringAI.demo.repository.ChatRepository;
import SpringAI.demo.service.ChatService;
import SpringAI.demo.service.OpenAIService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Controller
public class ChatController {

  private final OpenAIService openAIService;
  private final ChatRepository chatRepository;

  public ChatController(OpenAIService openAIService, ChatRepository chatRepository) {
    this.openAIService = openAIService;
    this.chatRepository = chatRepository;
  }

  @GetMapping("/")
  public String chatPage(){
    return "chat";
  }

  @ResponseBody
  @PostMapping("/chat")
  public String chat(@RequestBody Map<String, String> body) {
    return openAIService.generate(body.get("text"));
  }

  @ResponseBody
  @PostMapping("/chat/stream")
  public Flux<String> streamChat(@RequestBody Map<String, String> body) {
    return openAIService.generateStream(body.get("text"));
  }

  @Transactional(readOnly = true)
  public List<ChatAi> readAllChats(String userId) {
    return chatRepository.findByUserIdOrderByCreatedAtAsc(userId);
  }

}
