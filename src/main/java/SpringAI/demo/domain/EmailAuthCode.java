package SpringAI.demo.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class EmailAuthCode {

  private final Map<String, String> codeStore = new ConcurrentHashMap<>();
  private final Map<String, ScheduledFuture<?>> expirationTasks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  private static final long CODE_TTL_SECONDS = 180; // 3분

  public void saveCode(String email, String code) {
    codeStore.put(email, code);

    // 기존 만료 작업이 있다면 취소
    ScheduledFuture<?> existingTask = expirationTasks.remove(email);
    if (existingTask != null) {
      existingTask.cancel(false);
    }

    // 만료 예약
    ScheduledFuture<?> expirationTask = scheduler.schedule(() -> {
      codeStore.remove(email);
      expirationTasks.remove(email);
    }, CODE_TTL_SECONDS, TimeUnit.SECONDS);

    expirationTasks.put(email, expirationTask);
  }

  public boolean verifyCode(String email, String inputCode) {
    String storedCode = codeStore.get(email);
    if (storedCode == null) return false;
    if (!storedCode.equals(inputCode)) return false;

    // 인증 후 제거
    codeStore.remove(email);
    ScheduledFuture<?> task = expirationTasks.remove(email);
    if (task != null) task.cancel(false);

    return true;
  }

}
