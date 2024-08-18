package test.communication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import test.communication.domain.ChatMessageDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {
	private final SimpMessagingTemplate template; // 특정 broker로 메세지 전달

	@MessageMapping(value = "/chat/enter")
	public void enter(ChatMessageDTO message) {
		message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}

	@MessageMapping(value = "/chat/message")
	public void message(ChatMessageDTO message) {
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}

	@PostMapping("/file/upload")
	@ResponseBody
	public List<String> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
		if (files.length == 0) {
			throw new RuntimeException("Failed to store empty file.");
		}

		log.info("# 채팅방 업로드 POST");


		List<String> uploadedFilesInfo = new ArrayList<>();
		Path fileStorageLocation = Paths.get("src/main/resources/message/").toAbsolutePath();

		ExecutorService executor = Executors.newFixedThreadPool(files.length);
		List<Future<String>> futures = new ArrayList<>();

		System.out.println(Runtime.getRuntime().availableProcessors());

		try {
			// 디렉토리 생성
			Files.createDirectories(fileStorageLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not create storage directory", e);
		}

		for (MultipartFile file : files){
			Callable<String> fileSaveJob = () -> {
				String originalFileName = file.getOriginalFilename();
				String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
				try {
					Path targetName = fileStorageLocation.resolve(storedFileName);
					Files.copy(file.getInputStream(), targetName, StandardCopyOption.REPLACE_EXISTING);
					return originalFileName + "::" + storedFileName;
				} catch (IOException e) {
					throw new RuntimeException("Failed to store File:" + originalFileName, e);
				}


			};

			futures.add(executor.submit(fileSaveJob));
		}

		for (Future<String> future : futures) {
			try {
				uploadedFilesInfo.add(future.get());
			} catch (Exception e) {
				throw new RuntimeException("Failed to retrieve file upload result", e);
			}
		}
		executor.shutdown(); // thread 종료!!

		// 예외 발생 시 명확한 로그와 스레드 안전 종료
		try {
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				executor.shutdownNow();
				if (!executor.awaitTermination(60, TimeUnit.SECONDS))
					log.error("Executor did not terminate");
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}

		return uploadedFilesInfo; // 업로드된 파일 정보 리스트 반환
	}
}
