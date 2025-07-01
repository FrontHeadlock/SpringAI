package SpringAI.demo.controller;

import SpringAI.demo.service.OpenAIService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

  private final OpenAIService openAIService;

  public ChatController(OpenAIService openAIService) {
    this.openAIService = openAIService;
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

}
