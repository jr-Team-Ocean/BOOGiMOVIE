/* 도서 / 중고도서 / 영화 상세조회 페이지에서 결제시 */

/* ================================================ */
/*                   포트원 테스트                    */
/* ================================================ */


document.addEventListener("DOMContentLoaded", function () {
    // 결제 버튼 요소 찾아서 넣기
    // const paymentBtn = document.querySelector("#payment-btn");
    const paymentBtn = document.querySelector(".order_btn > button");

    /* 원래는 onclick 메소드 넣고, 해당 제품 정보 가져와서 넣어야 함 */
    const validationData = {
        // 주문자 회원 번호
        member_no: 2,

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

        /* 검증 성공 했을 경우 결제창 띄워서 결제 진행 */
        const response = await PortOne.requestPayment({
            // Store ID 설정
            storeId: "store-62992475-f5fa-4580-a8b2-566c4acd5a61",
            // 채널 키 설정
            channelKey: "channel-key-a237275c-94cd-41ef-b4cc-9a0bd6372dfc",
            paymentId: resp.order_no,
            orderName: resp.order_name,
            totalAmount: resp.total_price,
            currency: "CURRENCY_KRW",
            payMethod: "CARD",
            customer: {
                fullName: resp.recipient_name,
                phoneNumber: resp.recipient_phone
            }
        });

        /* code가 있으면 결제 실패 -> 결제 데이터들 삭제 */
        if (response.code !== undefined) {
            console.log("결제 실패 / 취소 : ", response.message);
            alert("결제에 실패했습니다.");

            await fetch("/order/fail", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ orderNo: resp.order_no }) // 실패 또는 취소한 주문번호
            });

        } else {
            /* 결제 성공시 사후 검증(최종 승인) */
        }

    } catch (e) {
        console.error(e);
        alert("결제 진행 중 오류 발생");
    }

}