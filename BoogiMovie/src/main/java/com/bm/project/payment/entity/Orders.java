package com.bm.project.payment.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@DynamicInsert // INSERT 시 작성한 값만 SQL에 포함, 나머지 default 활용
@DynamicUpdate // UPDATE 시 변경된 필드만 SQL에 포함
public class Orders {
	// 주문 엔티티
	
	@Id
	@Column(name = "ORDER_NO", nullable = false)
	private String orderNo; // 난수 생성해서 사용
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_NO", nullable = false)
	private Member member; // 회원 번호
	
	@Column(name = "ORDER_QUANTITY", nullable = false)
	private Integer orderQuantity; // 전체 주문 수량
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAY_NO")
	private Payment payment; // 결제 ID
	
	@Column(name = "PAY_STATUS", nullable = false)
	private String payStatus; // 결제 상태
	
	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<OrdersDetail> details = new ArrayList<>();
	
	// 연관관계 편의 메소드
	public void addOrderDetail(OrdersDetail detail) {
		details.add(detail);
		if(detail.getOrders() != this) {
			detail.setOrders(this);
		}
		
	}
	
	@OneToOne(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Delivery delivery;

    // 연관관계 편의 메소드
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery.getOrders() != this) {
            delivery.setOrders(this);
        }
    }
	

}
