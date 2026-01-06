package com.bm.project.service.myPage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.dto.IMyPageDto;
import com.bm.project.dto.MemberDto.MemberInfo;
import com.bm.project.entity.Member;
import com.bm.project.entity.Member.IsYN;
import com.bm.project.payment.repository.ProductRepository;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bm.project.dto.MemberDto;
import com.bm.project.entity.Product;
import com.bm.project.repository.MyPageRepository;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
	
	private final MemberRepository memberRepo;
	private final ProductRepository productRepo;
	private final MyPageRepository repository;
	private final BCryptPasswordEncoder bcrypt;
	
	@Value("${my.member.location}")
	private String FILE_PATH;
	
	@Value("${my.member.webpath}")
	private String WEB_PATH;
	

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
	    
	    return productPage.map(product -> {
	        // 여기서 DB의 태그 테이블을 뒤져서 '저자' 이름을 가져오는 쿼리를 실행
	        // 예: String author = repository.findAuthorByProductNo(product.getProductNo());
	        // product.setAuthorName(author);
	        return MemberDto.FavoriteResponse.toDto(product);
	    });
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

	
	// 회원 정보 수정
	@Override
	@Transactional
	public void changeProfileInfo(Long memberNo, String item, String value) {
		
		// 회원 정보 가져오기
		Member member = memberRepo.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		
		switch (item) {
			case "name" : member.setMemberName(value); break;
			case "nickname" : member.setMemberNickName(value); break;
			case "email" : member.setMemberEmail(value); break;
			default : throw new IllegalArgumentException("변경하려는 요소를 찾을 수 없습니다.");
		}	
	}

	
	// 비밀번호 변경
	@Override
	@Transactional
	public void changeProfilePw(Long memberNo, String newPw) {
		
		Member member = memberRepo.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		
		String encPw = bcrypt.encode(newPw);
		member.setMemberPw(encPw);
	}

	
	// 프로필 이미지 변경
	@Override
	@Transactional
	public void changeProfileImg(Long memberNo, MultipartFile profileImage) 
			throws IllegalStateException, IOException {
		
		// 회원 정보 가져오기
		Member member = memberRepo.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		
		
		// 이미지
        if (profileImage != null && !profileImage.isEmpty()) {
        	
        	String originName = profileImage.getOriginalFilename();
        	String reName = UUID.randomUUID().toString() + "_" + originName;
        	
        	File uploadDir = new File(FILE_PATH);
        	if (!uploadDir.exists()) uploadDir.mkdirs();
        	
        	profileImage.transferTo(new File(FILE_PATH + reName));
        	
        	// DB에는 웹 경로만 저장
        	member.setProfilePath(WEB_PATH + reName);
        }
		
		
	}

	
	
	
	
	
}
