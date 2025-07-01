package SpringAI.demo.login.config;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

// 챗 멀티턴 및 히스토리 위한 Config
// 구현체가 크게 2종류 InMemory, JDBC
@Configuration
public class AIConfig {

  //기본 설정시 자동 등록이지만, 수동 등록도 가능
  @Bean
  public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager ) {
    return JdbcChatMemoryRepository.builder()
        .jdbcTemplate(jdbcTemplate)
        .transactionManager(transactionManager)
        .build();
  }


}
