/* 도서 / 중고도서 / 영화 상세조회 페이지에서 결제시 */

/* ================================================ */
/*                     포트원                        */
/* ================================================ */

console.log("productPayment.js");

const storeId = document.querySelector("#portone-store-id").value;
const channelKey = document.querySelector("#portone-channel-key").value;

console.log(storeId)
console.log(channelKey)

document.addEventListener("DOMContentLoaded", function () {
    const itemElements = document.querySelectorAll(".order_item_values");
    
    const deliveryInfoContainer = document.querySelector(".order_info_container");

    // 배송이 필요한지 체크하는 변수
    let needsDelivery = false; 
    const PHYSICAL_TYPE_CODE = 1; // 도서 코드

    itemElements.forEach(item => {
        const typeCode = parseInt(item.dataset.type);
        console.log(typeCode)

        // 하나라도 도서 상품이 있다면 배송 정보 요소 필요
        if (typeCode === PHYSICAL_TYPE_CODE) {
            needsDelivery = true;
        }
    });

    if (needsDelivery) {
        // 도서가 하나라도 섞여 있으면 보임
        if(deliveryInfoContainer) deliveryInfoContainer.style.display = "flex";
    } else {
        // 전부 영화라면 숨김
        if(deliveryInfoContainer) deliveryInfoContainer.style.display = "none";
    }


    // 결제 버튼 요소 찾아서 넣기
    // const paymentBtn = document.querySelector("#payment-btn");
    const paymentBtn = document.querySelector(".order_btn");

    const memberNo = document.querySelector(".memberNo") // 주문하는 회원번호

    /* 주문하려는 아이템 값 */

    /* 원래는 onclick 메소드 넣고, 해당 제품 정보 가져와서 넣어야 함 */
    const validationData = {
        // 주문자 회원 번호
        member_no: 6,

        // 주문할 아이템들
        order_items: [
            { product_no: 1001, quantity: 2 },
            { product_no: 1002, quantity: 1 }
        ]
    };

    paymentBtn.addEventListener("click", e => {
        e.preventDefault();
        payment(validationData);
    })

})

async function payment(validationData) {

    try {
        /* 결제 요청 전에 사전 검증 */
        const validation = await fetch("/order/validation", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(validationData)
        });

        /* 반환값!!!
        { 
            "order_no": "20251230-63d30e2c",
            "total_price": 56000,
            "order_name": "2025 정보처리기사 실기 완벽 공략 외 1건",
            "recipient_name": "사용자",
            "recipient_phone": "01012341234"
            } */

        if (!validation.ok) {
            const err = await validation.json();
            alert("주문 생성 실패: ", err.message);
            return;
        }

        const resp = await validation.json();
        console.log("resp : ", resp);

        /* 검증 성공 했을 경우 결제창 띄워서 결제 진행 */
        const response = await PortOne.requestPayment({
            storeId: storeId,
            channelKey: channelKey,
            paymentId: resp.order_no,
            orderName: resp.order_name,
            totalAmount: resp.total_price,
            currency: "CURRENCY_KRW",
            payMethod: "EASY_PAY",
            customer: {
                fullName: resp.recipient_name,
                phoneNumber: resp.recipient_phone
            }
        });

        /* code가 있으면 결제 실패 -> 결제 데이터들 삭제 */
        if (response.code !== undefined) {
            console.log("결제 실패 / 취소 : ", response.message);
            console.log(resp.order_no);
            alert("결제에 실패했습니다.");

            await fetch("/order/fail", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: resp.order_no // 실패 또는 취소한 주문번호
            });

        } else {
            /* 결제 성공시 사후 검증(최종 승인) */
            /* 이때 delivery 테이블에도 배송 정보 저장 */
            console.log("결제 성공, 사후 검증 요청 : ", response.txId); // 결제 시도 고유 ID

            /* 검증 및 결제수단/배송 테이블에 넣을 값들 담아서 보내주기 */
            const data = {
                order_no: resp.order_no,

                pay_no: response.txId, // 결제 시도 고유 ID
                pay_method: "EASY_PAY",
                pay_price: resp.total_price, // 총 결제 금액

                /* 여기는 배송 요청 부분 임의 데이터 */
                recipient_name: "하멍멍",          // 주문자명
                recipient_tel: "01012341234",   // 주문자 휴대폰번호
                order_request: "빠른 배송 부탁",    // 요청사항
                post_code: "01234",               // 우편번호
                road_address: "서울 특별시 관철동",  // 도로명 주소
                detail_address: "9층 903호"        // 상세 주소
            }

            const verifyResp = await fetch("/order/payment", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            })

            if(verifyResp.ok) {
                alert("결제 완료하였습니다.");
            } else {
                alert("결제 진행 중 문제 발생");
            }

            
        }

    } catch (e) {
        console.error(e);
        alert("결제 진행 중 오류 발생");
    }

}