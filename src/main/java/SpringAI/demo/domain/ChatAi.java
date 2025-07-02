package SpringAI.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.ai.chat.messages.MessageType;

@Entity
@Getter
@Setter
public class ChatAi {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id")
  private String userId;

  @Column(columnDefinition = "Text")
  private String Content;

  @Enumerated(EnumType.STRING)
  private MessageType type;

  @CreationTimestamp
  @Column(nullable = false,name="created_at")
  private LocalDateTime createdAt;
}
