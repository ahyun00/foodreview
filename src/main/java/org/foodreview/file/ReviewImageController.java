package org.foodreview.file;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewImageController {
	
	private final ReviewImageFileService reviewImageFileService;
	
	// 저장된 리뷰 이미지 조회
	@GetMapping("/review-images/{savedName}")
	public ResponseEntity<Resource> getImage (
			@PathVariable("savedName") String savedName) {
	
		// 저장된 이미지 파일 가져오기
		Resource resource = reviewImageFileService.loadImage(savedName);
		
		// 확장자에 따라 이미지의 Content-Type 설정
		MediaType mediaType;
		
		if (savedName.endsWith(".png")) {
			mediaType = MediaType.IMAGE_PNG;
		
		} else if (savedName.endsWith(".webp")) {
			mediaType = MediaType.parseMediaType("image/webp");
			
		} else {
			mediaType = MediaType.IMAGE_JPEG;
		}
		
	   // 브라우저에 이미지 데이터 반환
		return ResponseEntity.ok().
				header("X_Content_Type_Options", "nosniff")
				.contentType(mediaType).body(resource);
	}
	
	

}











