package org.foodreview.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReviewImageFileService {
	
	// 리뷰 이미지 저장 폴더
	private final Path uploadDir =
			Paths.get("uploads", "reviews").toAbsolutePath();
	
	// 리뷰 이미지 저장
	public String saveImage(MultipartFile file) {
		
		// 리뷰 이미지는 선택 사항
		if (file == null || file.isEmpty()) {
			return null;
		}
		
		if (file.getSize() > 5 * 1024 * 1024) {
			throw new IllegalArgumentException(
					"리뷰 이미지는 5MB 이하만 업로드할 수 있습니다.");	
		}
		
		// 파일 이름 확인
		String originalName = file.getOriginalFilename();
		
		if (originalName == null) {
			throw new IllegalArgumentException("파일 이름을 확인할 수 없습니다.");
		}
		
		String lowerName = originalName.toLowerCase(Locale.ROOT);
		
		// 확장자 검사
		if (!(lowerName.endsWith(".jpg") 
				|| lowerName.endsWith(".png")
				|| lowerName.endsWith(".webp"))) {
			
			throw new IllegalArgumentException(
					"JPG, PNG, WEBP 이미지만 업로드할 수 있습니다.");
		}
		
		// 실제 파일 시그니처 검사
		try (InputStream input = file.getInputStream()){
			
			byte[] header = input.readNBytes(12);
			
			boolean validImage = false;
			
			if (lowerName.endsWith(".jpg")) {
				
				validImage = header.length >= 3
						&& (header[0] & 0xFF) == 0xFF
						&& (header[1] & 0xFF) == 0xD8
						&& (header[2] & 0xFF) == 0xFF;
				
			} else if (lowerName.endsWith(".png") ) {
				
				byte[] pngSignature = {
						(byte) 0x89, 0x50, 0x4E, 0x47,
						0x0D, 0x0A, 0x1A, 0x0A
				};
				
				validImage = header.length >= 8
						&& Arrays.equals(Arrays.copyOf(header, 8), pngSignature);
				
			} else if (lowerName.endsWith(".webp")) {
				
				String firstFour = new String(
						header, 0, Math.min(header.length, 4),
						java.nio.charset.StandardCharsets.US_ASCII);
				
				String webp = header.length >= 12 
						? new String(
								header, 8 , 4,
								java.nio.charset.StandardCharsets.US_ASCII) 
						: "";
				
				validImage = header.length >= 12
						&& firstFour.equals("RIFF")
						&& webp.equals("WEBP");
			}
			
			if(!validImage) {
				throw new IllegalArgumentException(
						"실제 이미지 파일만 업로드할 수 있습니다.");
			}
			
		} catch (IOException e) {
			throw new IllegalStateException(
					"리뷰 이미지 검사에 실패했습니다.", e);
		}
		
		// 확장자 추출 및 UUID 파일명 생성
		String extension = lowerName.substring(lowerName.lastIndexOf("."));
		
		String savedName = UUID.randomUUID() + extension;
		
		try {
			// 저장 폴더 생성
			Files.createDirectories(uploadDir);
			
			// 실제 이미지 저장
			file.transferTo(uploadDir.resolve(savedName));
			
		} catch (IOException e) {
			throw new IllegalStateException( "리뷰 이미지 저장에 실패했습니다.", e);
		}
		
		return savedName;
		
	}
	
	// 저장된 이미지 삭제
	public void deleteImage(String savedName) {
		
		if (savedName == null || 
				!savedName.matches("[0-9a-fA-F-]{36}\\.(jpg|png|webp)")) {
			throw new IllegalArgumentException(
					 "잘못된 파일 이름입니다.");
		}
		
		try { Files.deleteIfExists(uploadDir.resolve(savedName));
		
		} catch (IOException e) {
			throw new IllegalStateException(
					"리뷰 이미지 삭제에 실패했습니다.", e);
		}
	}
	
	
	// 저장된 리뷰 이미지 불러오기
	public Resource loadImage(String savedName) {
				
		// 잘못된 파일명 차단
		if (savedName == null || !savedName.matches(
				"[0-9a-fA-F-]{36}\\.(jpg|png|webp)")) {
			
			throw new IllegalArgumentException ( "잘못된 파일 이름입니다.");
			
		}
		
		try { 
			 // 저장된 이미지 파일 경로 생성
			Path filePath = uploadDir.resolve(savedName);
		
			// 파일 경로를 Resource 객체로 변환
			Resource resource = new UrlResource(filePath.toUri());
			
			// 파일이 없거나 읽을 수 없다면 예외 발생
			if (!resource.exists() || !resource.isReadable()) {
				throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
			}
			
			return resource;
			
		} catch (MalformedURLException e) {
			throw new IllegalStateException("리뷰 이미지 조회에 실패했습니다.", e);
		}
	}
	
}














