package com.bm.project.entity;

import com.bm.project.enums.CommonEnums.SocialProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "SOCIAL_LOGIN", uniqueConstraints = {
			@UniqueConstraint(name="SOCIAL_PROVIDER_ID", columnNames = {"PROVIDER", "PROVIDER_ID"}),
			@UniqueConstraint(name = "MEMBER_PROVIDER", columnNames = {"MEMBER_NO", "PROVIDER"})
		})
public class MemberSocial {
	
	@Id
	@Column(name = "SOCIAL_LOGIN_NO", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOCIAL_NO")
	@SequenceGenerator(name = "SEQ_SOCIAL_NO", 
						sequenceName = "SEQ_SOCIAL_NO", 
						allocationSize = 1, 
						initialValue = 1)
	private Long socialNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_NO", nullable = false)
	private Member member;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PROVIDER", nullable = false)
	private SocialProvider provider;
	
	@Column(name = "PROVIDER_ID", nullable = false)
	private String providerId;
	
}
