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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bm.project.dto.MemberDto;
import com.bm.project.entity.Product;
import com.bm.project.repository.MyPageRepository;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
	
	private final MemberRepository memberRepo;
	private final ProductRepository productRepo;
	private final MyPageRepository repository;

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
	
	// 내가 찜한 상품
	@Override
    @Transactional(readOnly = true)
    public Page<MemberDto.FavoriteResponse> getFavoriteList(Long memberNo, String order, Pageable pageable) {
        
        Page<Product> productPage = repository.findByMemberNo(memberNo, order, pageable);
        
        return productPage.map(MemberDto.FavoriteResponse::toDto);
    }

	// 좋아요 삭제
	@Override
	@Transactional // <- 이 어노테이션이 없으면 삭제 시 에러가 발생합니다!
	public boolean removeFavorite(int productNo, Long memberNo) {
	    try {
	        repository.deleteFavorite(productNo, memberNo);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
