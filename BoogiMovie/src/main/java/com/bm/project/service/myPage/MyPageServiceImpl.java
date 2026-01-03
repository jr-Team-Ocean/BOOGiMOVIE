package com.bm.project.service.myPage;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.dto.IMyPageDto;
import com.bm.project.dto.MemberDto.MemberInfo;
import com.bm.project.entity.Member;
import com.bm.project.entity.Member.IsYN;
import com.bm.project.payment.repository.ProductRepository;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
	
	private final MemberRepository memberRepo;
	private final ProductRepository productRepo;

	// 회원 정보 가져오기
	@Override
	public MemberInfo getMemberInfo(Long memberNo) {
		
		Member member = memberRepo.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		
		// 소장한 영화 목록 조회
		List<IMyPageDto> myMovies = productRepo.getPurchasedMovie(memberNo);
		
		
		return MemberInfo.infoToDto(member, myMovies);
	}

	// 회원 탈퇴 처리
	@Override
	@Transactional // 변경 감지
	public int secession(Long memberNo) {
		Member member = memberRepo.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		
		// 탈퇴 여부 값 변경
		member.setSecessionFl(IsYN.Y);
		
		// 개인정보 지워주기
		member.setMemberPw("deleted_" + member.getMemberNo());
		member.setMemberName("탈퇴한 회원_" + member.getMemberNo());
		member.setMemberPhone("deleted_" + member.getMemberNo());
		member.setMemberAddress("-");
		
		return 1;
	}

}
