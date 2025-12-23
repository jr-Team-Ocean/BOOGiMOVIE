package com.bm.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 필수
@AllArgsConstructor
@DynamicInsert // INSERT 시 작성한 값만 SQL에 포함, 나머지 default 활용
@DynamicUpdate // UPDATE 시 변경된 필드만 SQL에 포함
public class Member {
	
	@Id
	@Column(name = "MEMBER_NO", nullable = false) // NOT NULL
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_NO")
	@SequenceGenerator(name = "SEQ_MEMBER_NO", 
					   sequenceName = "SEQ_MEMBER_NO",
					   initialValue = 1,
					   allocationSize = 1) // initialValue: 1부터 시작, allocationSize: 한 번에 가져올 시퀀스 번호
	private Long memberNo;

	@Column(name = "MEMBER_EMAIL", length = 50, nullable = false)
	private String memberEmail;
	
	@Column(name = "MEMBER_ID", length = 30, nullable = false)
	private String memberId;
	
	@Column(name = "MEMBER_PW", length = 300, nullable = false)
	private String memberPw;
	
	@Column(name = "MEMBER_NAME", length = 50, nullable = false)
	private String memberName;
	
	@Column(name = "MEMBER_NICKNAME", length = 30, nullable = false)
	private String memberNickName;
	
	@Column(name = "MEMBER_ADDRESS", length = 200, nullable = false)
	private String memberAddress;
	
	@Column(name = "MEMBER_BIRTH", length = 8, nullable = false)
	private String memberBirth;
	
	@Column(name = "PROFILE_PATH", length = 200) // NULL 허용
	private String profilePath;
	
	// columnDefinition: JPA가 테이블을 생성 하는 쿼리를 만들 때, 해당 문자열 넣음
	@Column(name = "ENROLL_DATE", 
			columnDefinition = "DATE DEFAULT SYSDATE",
			nullable = false)
	private LocalDateTime enrollDate;
	
	// enum 클래스 (N, Y 값만 쓰도록)
	public enum IsYN {
		N, Y
	}
	
	@Column(name = "SECESSION_FL", nullable = false)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'N'")
	private IsYN secessionFl;
	
	@Column(name = "IS_ADMIN", nullable = false)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'N'")
	private IsYN isAdmin;
	
}
