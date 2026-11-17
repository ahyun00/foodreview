package org.foodreview.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReceiptFileService {
	
	// 영수증 이미지를 저장할 폴더
	private final Path uploadDir = Paths.get("uploads", "receipts").toAbsolutePath();
	
	// 업로드된 영수증을 저장하고 파일명을 반환
	public String saveReceipt(MultipartFile file) {
		
		// 파일이 없거나 비어 있다면 등록 중단
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("영수증 이미지를 선택해 주세요.");
		}
		
		// 최대 파일 크기 : 5MB
		if (file.getSize() > 5 * 1024 * 1024) {
			throw new IllegalArgumentException("영수증은 5MB 이하만 업로드할 수 있습니다.");
		}
		
		// 업로드한 파일의 원래 이름
		String originalName = file.getOriginalFilename();
		
		if (originalName == null) {
			throw new IllegalArgumentException("파일 이름을 확인할 수 없습니다.");
		}
		
		// 파일 확장자를 소문자로 변환
		String lowerName = originalName.toLowerCase(java.util.Locale.ROOT);
		
		// 허용한 이미지 확장자인지 확인
		if (!(lowerName.endsWith(".jpg") 
				|| lowerName.endsWith(".png") 
				|| lowerName.endsWith(".webp"))) {
			throw new IllegalArgumentException("JPG, PNG, WEBP 이미지만 업로드할 수 있습니다.");
		}
		
		
		// 실제 파일 내용이 이미지 형식인지 검사
		try (InputStream input = file.getInputStream()){
			
			// 파일의 앞부분 12바이트 읽기
			byte[] header = input.readNBytes(12);
			
			// 이미지 형식이 올바른지 저장할 변수
			boolean validImage = false;
			
			// JPG 검사
			if (lowerName.endsWith(".jpg")) {
				validImage = header.length >= 3 
						&& (header[0] & 0xFF) == 0xFF
						&& (header[1] & 0xFF) == 0xD8
						&& (header[2] & 0xFF) == 0xFF;
			}
			// PNG 검사
			else if (lowerName.endsWith(".png")) {
				byte[] pngSignature = {
						(byte) 0x89, 0x50, 0x4E, 0x47,
						0x0D, 0x0A, 0x1A, 0x0A
				};
				
				validImage = header.length >= 8
						&& Arrays.equals(Arrays.copyOf(header, 8), pngSignature);
			}
			
			// WEBP 검사
			else if (lowerName.endsWith(".webp")) {
				validImage = header.length >=12
						&& new String(header, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
						&& new String(header, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP");
			}
			
			if (!validImage) {
				throw new IllegalArgumentException("실제 이미지 파일만 업로드할 수 있습니다.");
			}
			
		} catch (IOException e) {
			throw new IllegalStateException("영수증 파일 검사에 실패했습니다.", e);
		}
		
		// 파일명 중복을 방지하기 위해 랜덤한 이름 생성
		String extension = lowerName.substring(lowerName.lastIndexOf("."));
		String savedName = UUID.randomUUID() + extension;
		
		try {
			// 	저장 폴더가 없으면 생성
			Files.createDirectories(uploadDir);
			
			// uploadDir 폴더 안에 저장할 파일 경로를 만들고 파일 저장
			file.transferTo(uploadDir.resolve(savedName));
		} catch (IOException e) {
			throw new IllegalStateException("영수증 파일 저장에 실패했습니다.", e);
		}
		
		return savedName;
	}
	
	
	// 저장된 영수증 이미지를 조회
	public Resource loadReceipt(String savedName) {
		
		 // 파일 이름이 비어 있거나 경로 문자가 포함되어 있으면 차단
		if (savedName == null || savedName.isBlank() || !savedName.matches("[0-9a-fA-F-]{36}\\.(jpg|jpeg|png|webp)")) {
			throw new IllegalArgumentException("잘못된 파일 이름입니다.");
		} 
		
		try {
			// 저장 폴더에서 해당 파일의 전체 경로 생성
			Path filePath = uploadDir.resolve(savedName);
			
			// 파일 경로를 Spring에서 읽을 수 있는 Resource로 변환 
			Resource resource = new UrlResource(filePath.toUri());
			
			// 파일이 없거나 읽을 수 없으면 오류 발생
			if (!resource.exists() || !resource.isReadable()) {
				throw new IllegalArgumentException("영수증 파일을 찾을 수 없습니다.");
			}
			return resource;
		} catch(MalformedURLException e) {
			throw new IllegalStateException("영수증 파일 조회에 실패했습니다.", e);
		}
	}
	
	
	// DB 등록 실패 시 이미 저장된 영수증 파일을 삭제
	public void deleteReceipt(String savedName) {
		
		// 기존 메서드를 이용해 파일명 검증 및 존재 여부 확인
		loadReceipt(savedName);
		
		try {
			// 저장 폴더 경로 + 파일명을 합쳐 실제 파일 경로 생성
			Path filePath = uploadDir.resolve(savedName);
			
			Files.delete(filePath);
			
		} catch(IOException e) {
			
			throw new IllegalStateException("영수증 파일 삭제에 실패했습니다.", e);
		}
	}
	


}
















