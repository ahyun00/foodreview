package org.foodreview.place;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Place {
	private Integer pno;
	private String name;
	private String category;
	private String address;
	private String phone;
	private String description;
	private LocalDateTime regDate;
	private String placeImage;
	
}
