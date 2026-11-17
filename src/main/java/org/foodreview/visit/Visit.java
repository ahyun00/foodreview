package org.foodreview.visit;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Visit {
	
	private Integer vno;
	private Integer mno;
	private Integer pno;
	private LocalDate visitDate;
	private String receipt;
	private Integer status;
	private LocalDateTime regDate;
	
	private Integer rno;
	private String placeName;


}
