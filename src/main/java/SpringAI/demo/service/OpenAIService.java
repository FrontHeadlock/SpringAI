package SpringAI.demo.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class OpenAIService {

  private final OpenAiChatModel openAiChatModel;
  private final OpenAiEmbeddingModel openAiEmbeddingModel;
  private final OpenAiImageModel openAiImageModel;
  private final OpenAiAudioSpeechModel openAiAudioSpeechModel;
  private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
  private final ChatMemoryRepository chatMemoryRepository;

  public String generate(String text) {

    // 메시지
    SystemMessage systemMessage = new SystemMessage("");
    UserMessage userMessage = new UserMessage(text);
    AssistantMessage assistantMessage = new AssistantMessage("");

    // 옵션 (인자를 통해 받아서 사용도 가능)
    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .model("gpt-4.1-mini")
        .temperature(0.7)
        .build();

    // 프롬프트
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage, assistantMessage), options);

    // 요청 및 응답
    ChatResponse response = openAiChatModel.call(prompt);
    return response.getResult().getOutput().getText();
  }

  public Flux<String> generateStream(String text) {

    //유저&페이지 별 ChatMemory 관리 위한 Key(우선은 명시적 적용)
    //추후 유저 아이디,변수로 저장 필요
    String userId = "xxxjjhhh" + "_" + "1";

    //챗 메모리로 메시지 관리
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
        .maxMessages(10)
        .chatMemoryRepository(chatMemoryRepository)
        .build();

    //신규 메시지도 추가
    chatMemory.add(userId, new UserMessage(text));

    // 옵션
    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .model("gpt-4.1-mini")
        .temperature(0.7)
        .build();

    // 프롬프트
    Prompt prompt = new Prompt(chatMemory.get(userId), options);

    // 응답 메시지를 저장할 임시 버퍼 (토큰을 받을 곳)
    StringBuilder responseBuffer = new StringBuilder();

    // 요청 및 응답
    return openAiChatModel.stream(prompt)
        .mapNotNull(response -> {
          String token = response.getResult().getOutput().getText();
          responseBuffer.append(token);
          return token;
        })
        //String 과정 마무리 되면 완료된 responseBuffer의 데이터를 ChatMemory에 추가
        //chatMemoryRepository에 그동안의 정보 저장
        .doOnComplete(() -> {

          chatMemory.add(userId, new AssistantMessage(responseBuffer.toString()));
          chatMemoryRepository.saveAll(userId, chatMemory.get(userId));
        });
  }

  // 임베딩 api 호출 메서드
  public List<float[]> generateEmbedding(List<String> texts, String model){

    //옵션
    EmbeddingOptions embeddingOptions = OpenAiEmbeddingOptions.builder()
        .model(model)
        .build();

    //프롬프트 (사용자에게 받은 문장을 넣어 임베딩 옵션으로 임베딩)
    EmbeddingRequest prompt = new EmbeddingRequest(texts, embeddingOptions);

    //요청 및 응답
    EmbeddingResponse response = openAiEmbeddingModel.call(prompt);

    return response.getResults().stream()
        .map(Embedding::getOutput)
        .toList();
  }

  // 이미지 모델 메서드
  // 어떤 이미지를 만들지, 몇 개, 규격
  public List<String> generateImages(String text, int count, int height, int width) {

    // 옵션
    OpenAiImageOptions imageOptions = OpenAiImageOptions.builder()
        .quality("hd")
        .N(count)
        .height(height)
        .width(width)
        .build();

    // 프롬프트
    ImagePrompt prompt = new ImagePrompt(text, imageOptions);

    // 요청 및 응답(Base64기반) -> url 주소를 받음
    ImageResponse response = openAiImageModel.call(prompt);
    return response.getResults().stream()
        .map(image -> image.getOutput().getUrl())
        .toList();
  }

  // TTS 텍스트 -> 음성 출력
  public byte[] tts(String text) {

    // 옵션
    OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
        .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
        .speed(1.0f)
        .model(OpenAiAudioApi.TtsModel.TTS_1.value)
        .build();

    // 프롬프트
    SpeechPrompt prompt = new SpeechPrompt(text, speechOptions);

    // 요청 및 응답
    SpeechResponse response = openAiAudioSpeechModel.call(prompt);
    return response.getResult().getOutput();
  }

  // STT 음성 -> 텍스트 출력
  public String stt(Resource audioFile) {

    // 옵션
    OpenAiAudioApi.TranscriptResponseFormat responseFormat = OpenAiAudioApi.TranscriptResponseFormat.VTT;
    OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
        .language("ko") // 인식할 언어
        .prompt("Ask not this, but ask that") // 음성 인식 전 참고할 텍스트 프롬프트
        .temperature(0f)
        .model(OpenAiAudioApi.TtsModel.TTS_1.value)
        .responseFormat(responseFormat) // 결과 타입 지정 VTT 자막형식
        .build();

    // 프롬프트
    AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);

    // 요청 및 응답
    AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
    return response.getResult().getOutput();
  }


}
