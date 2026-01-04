package com.bm.project.payment.model.service;

import java.util.List;

import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.model.dto.CartDto.CartRespDto;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.dto.PayValidationDto.OrderItemDto;
import com.bm.project.payment.model.dto.PayValidationDto.PayResponse;
import com.bm.project.payment.model.dto.PayValidationDto.PaySuccessDto;
import com.bm.project.payment.model.dto.PayValidationDto.PaymentItemDto;

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


	/** 결제 사후 검증
	 * @param successDto
	 */
	void successPayment(PaySuccessDto successDto);


	/** 구매하려는 상품의 상품명 / 가격 / 썸네일 가져오기
	 * @param orderItemList
	 * @return
	 */
	List<PaymentItemDto> getPaymentItems(List<OrderItemDto> orderItemList);


	/** 결제 조회
	 * @param orderNo
	 * @return
	 */
	Orders payComplete(String orderNo);


}
