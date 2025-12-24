package com.bm.project.service.member;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;


public interface AjaxService {

	boolean checkId(String id);

	boolean checkNickname(String nickname);

	boolean checkPhone(String phone);

}
