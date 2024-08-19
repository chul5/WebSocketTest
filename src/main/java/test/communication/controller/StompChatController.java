package test.communication.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import test.communication.domain.ChatMessageDTO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

	@PostMapping("/file/single")
	@ResponseBody
	public List<String> singleThreadFile(@RequestParam("files") MultipartFile[] files) { // hadnleFileUpload메소드는 클라이언트가 업로드한 파일을 서버에 저장하는 역할
		if (files.length == 0) {
			throw new RuntimeException("Failed to store empty file."); // 비어 있으면 예외처리
		}

		log.info("# 싱글스레드 업로드 POST");
		List<String> response = new ArrayList<>();
		Path fileStorageLocation = Paths.get("src/main/resources/message/").toAbsolutePath();

		try {
			Files.createDirectories(fileStorageLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not create storage directory", e);
		}

		for (MultipartFile file : files) {
			String originalFileName = file.getOriginalFilename();
			String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
			try {
				Path targetName = fileStorageLocation.resolve(storedFileName);
				Files.copy(file.getInputStream(), targetName, StandardCopyOption.REPLACE_EXISTING);
				response.add(originalFileName + "::" + storedFileName);
			} catch (IOException e) {
				throw new RuntimeException("Failed to store File:" + originalFileName, e);
			}
		}
		return response;
	}

	@PostMapping("/file/upload")
	@ResponseBody
	public List<String> multiThreadFileUpload(@RequestParam("files") MultipartFile[] files) {
		if (files.length == 0) {
			throw new RuntimeException("Failed to store empty file.");
		}
		log.info("# 멀티스레드 업로드 POST");
		List<String> uploadedFilesInfo = new ArrayList<>();
		Path fileStorageLocation = Paths.get("src/main/resources/message/").toAbsolutePath();

		int numberOfThreads = files.length;
		if (files.length > Runtime.getRuntime().availableProcessors())
			numberOfThreads = Runtime.getRuntime().availableProcessors();

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<Future<String>> futures = new ArrayList<>();


		try {
			Files.createDirectories(fileStorageLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not create storage directory", e);
		}

		for (MultipartFile file : files) {
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
		executor.shutdown();
		return uploadedFilesInfo;
	}


}
