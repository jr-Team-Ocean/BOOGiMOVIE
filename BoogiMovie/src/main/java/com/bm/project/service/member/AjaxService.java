package com.bm.project.service.member;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

import com.bm.project.dto.MemberDto.DupCheckResponse;

public interface AjaxService {

	boolean checkId(String id);

	boolean checkNickname(String nickname);



}
