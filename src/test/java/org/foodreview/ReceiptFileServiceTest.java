package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.foodreview.file.ReceiptFileService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class ReceiptFileServiceTest {
	
	  @Test
	    void 가짜JPG파일_업로드차단() {
		  
		  ReceiptFileService service = new ReceiptFileService();
		  
		// JPG 확장자지만 실제 내용은 일반 텍스트
		MockMultipartFile file = new MockMultipartFile(
				"receiptFile",
				"fake.jpg",
				"image/jpeg",
				"hello".getBytes(StandardCharsets.UTF_8)
				);
		
		assertThrows(IllegalArgumentException.class, 
						() -> service.saveReceipt(file));
		  
	  }
	  
	  
	  @Test
	  void PNG시그니처파일_저장성공() throws IOException {

	      // 실제 ReceiptFileService 객체 생성
	      ReceiptFileService service = new ReceiptFileService();

	      // PNG 파일의 시작 부분에 들어가는 고유한 바이트 값
	      // PNG 파일의 시그니처(파일 형식 식별값)
	      byte[] pngHeader = {
	    	(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0X0A, 0x1A, 0x0A	  
	      };
	      
	      // PNG 파일인 것처럼 테스트용 업로드 파일 생성
	      MockMultipartFile file = new MockMultipartFile(
	    		  "receiptFile",
	    		  "test.png",
	    		  "image/png",
	    		  pngHeader
	    		  );
	      
	      // 나중에 저장된 파일을 삭제하기 위해 파일명을 기억할 변수
	      String savedName = null;
	      
	      try {
	    	  // 실제 파일 저장 메서드 실행
	          // 성공하면 UUID가 포함된 저장 파일명을 반환
	    	  savedName = service.saveReceipt(file);
	    	  
	    	  assertTrue(savedName.endsWith(".png"));
	    	  
	    	  // 저장한 파일을 다시 불러올 수 있는지 확인
	          // loadReceipt()가 예외 없이 실행되고 null이 아니면 통과
	    	  assertNotNull(service.loadReceipt(savedName));
	    
	      } finally {
	    	  
	    	  if (savedName != null) {
	    		  service.deleteReceipt(savedName);
	    	  }
	      }      
	  }
	  
	  
	  @Test
	  void 가짜PNG파일_업로드차단() {
		  
		 ReceiptFileService service = new ReceiptFileService();
		 
		 // 확장자는 PNG지만 실제 내용은 일반 텍스트
		 MockMultipartFile file = new MockMultipartFile(
				 "receiptFile",
				 "fake.png",
				 "image/png",
				 "hello".getBytes(StandardCharsets.UTF_8)
				 );
		// 실제 PNG 시그니처가 아니므로 예외가 발생해야 성공 
		assertThrows(IllegalArgumentException.class, 
								() -> service.saveReceipt(file)
		);		 
	  }
	  
	  @Test
	  void JPG시그니처파일_저장성공() {
		  
		  ReceiptFileService service = new ReceiptFileService();
		  
		  // JPG 파일의 시작 바이트 
		  byte[] jpgHeader = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
		  
		  // JPG 시그니처를 가진 테스트 파일 생성  
		  MockMultipartFile file = new MockMultipartFile(
				  "receiptFile",
				  "test.jpg",
				  "image/jpeg",
				  jpgHeader
		   );
		  
		  String savedName = null;
		  
		  try {
			  // 실제 파일 저장
			  savedName = service.saveReceipt(file);
			  
			  // 저장된 파일의 확장자 확인
			  assertTrue(savedName.endsWith(".jpg"));
			  
			  // 저장된 파일을 다시 불러올 수 있는지 확인
			  assertNotNull(service.loadReceipt(savedName));
			  
		  } finally {
			  
			  if (savedName != null) {
				  service.deleteReceipt(savedName);
			  }
		  }
	  }

	  
	  @Test
	  void 가짜WEBP파일_업로드차단() {
		  
		  ReceiptFileService service = new ReceiptFileService();

		  // 확장자는 WEBP지만 실제 내용은 일반 텍스트
		  MockMultipartFile file = new MockMultipartFile(
				  "receiptFile",
				  "fake.webp",
				  "image/webp",
				  "hello".getBytes(StandardCharsets.UTF_8)
		  );		
		  
		 // 이미지가 아니므로 예외가 발생해야 테스트 성공
		 assertThrows(
				 IllegalArgumentException.class, () -> service.saveReceipt(file)
				 );
	  }
	  
	  @Test
	  void WEBP시그니처파일_저장성공() {

	      ReceiptFileService service = new ReceiptFileService();

	      // WEBP 파일의 시작 부분을 나타내는 12바이트
	      byte[] webpHeader = {
	    		  'R', 'I', 'F', 'F',
	    		  0, 0, 0, 0,
	    		  'W', 'E', 'B', 'P'
	      };
	      
	      // WEBP 시그니처를 가진 테스트 파일 생성
	      MockMultipartFile file  = new MockMultipartFile(
	    		  "receiptFile",
	    		  "test.webp",
	    		  "image/webp",
	    		  webpHeader
	    	);
	      
	      String savedName = null;
	      
	      try {
	    	  // 실제 파일 저장
	    	 savedName = service.saveReceipt(file);
	    	 
	    	 assertTrue(savedName.endsWith(".webp"));
	    	 
	    	 assertNotNull(service.loadReceipt(savedName));
	    	 
	      } finally {
	    	  
	    	  if (savedName != null) {
	    		  service.deleteReceipt(savedName);
	    	  }
	      }  
	  }
	  
	  @Test
	  void 파일크기_5MB초과시_업로드차단() {

	      ReceiptFileService service = new ReceiptFileService();

	      // 5MB보다 1바이트 큰 파일 데이터 생성
	      byte[] largeData = new byte[5 * 1024 * 1024 + 1];
	      
	      // 테스트용 업로드 파일 생성
	      MockMultipartFile file = new MockMultipartFile(
	    		  "receiptFile",
	    		  "large.jpg",
	    		  "image/jpeg",
	    		  largeData
		   );
	      
	      // 파일 크기가 초과됐으므로 예외 발생 확인
	      IllegalArgumentException exception = assertThrows(
	    		  IllegalArgumentException.class, () -> service.saveReceipt(file));
	      
	      // 정확히 파일 크기 제한 때문에 차단됐는지 확인
	      assertEquals (
	    		  "영수증은 5MB 이하만 업로드할 수 있습니다.",
	    		  exception.getMessage()
		  );
	  }
}











