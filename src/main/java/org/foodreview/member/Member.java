package org.foodreview.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Member {
	
	private Integer mno;
	private String name;
	private String email;
	private String pwd;
	private String nickname;
	private String gender;
	private LocalDate birth;
	private String phone;
	private LocalDateTime joinDate;
	private Integer active;
	private String role;

}
