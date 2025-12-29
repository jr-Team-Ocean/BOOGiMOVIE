package com.bm.project.payment.model.service;

import com.bm.project.dto.MemberDto;
import com.bm.project.payment.model.dto.OrderRequestDto;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.dto.PayValidationDto.PayResponse;

public interface PaymentService {

	/** 결제 전 총금액 사전 검증
	 * @param payValidation
	 * @return
	 */
	PayResponse prePaymentValidation(PayValidationDto.PayRequest payValidation);


	/** 결제 진행 중 취소 또는 실패시
	 * @param orderNo
	 */
	void failPayment(String orderNo, String reason);


}
