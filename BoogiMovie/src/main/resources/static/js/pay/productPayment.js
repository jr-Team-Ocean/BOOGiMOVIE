/* 도서 / 중고도서 / 영화 상세조회 페이지에서 결제시 */

/* ================================================ */
/*                   포트원 테스트                    */
/* ================================================ */

// 결제 버튼 요소 찾아서 넣기
// const paymentBtn = document.querySelector("#payment-btn");

/* 원래는 onclick 메소드 넣고, 해당 제품 정보 가져와서 넣어야 함 */

async function testPayment() {
    // const orderData = {

    //     /* 주문자 정보 */
    //     recipientName : "예솔",
    //     recipientPhone: "01012341234",
    //     deliveryRequest: "부재 시 경비실 고고",

    //     /* 주소 */
    //     postCode: "01234",
    //     roadAddress: "서울특별시 종로구 관철동",
    //     detailAddress: "대왕빌딩 9층 903호",

    //     /* 총 금액 및 결제 수단, 결제 번호 난수 생성해서 요청 보내기 (포트원 v2 방식) */
    //     totalAmount: 51400,
    //     paymentMethod: "CARD",

    //     items: [
    //         {
    //             productId: 101,
    //             productName: "바이브 코딩 너머 개발자 생존법",
    //             quantity: 2,
    //             price: 9100
    //         },
    //         {
    //             productId: 101,
    //             productName: "바이브 코딩 너머 개발자 생존법",
    //             quantity: 2,
    //             price: 9100
    //         }
    //     ]
    // }

    // /* 결제창 띄우고 결제 진행 */
    // const response = await PortOne.requestPayment({
    //   // Store ID 설정
    //   storeId: "store-62992475-f5fa-4580-a8b2-566c4acd5a61",
    //   // 채널 키 설정
    //   channelKey: "channel-key-a237275c-94cd-41ef-b4cc-9a0bd6372dfc",
    //   paymentId: `payment-${crypto.randomUUID()}`,
    //   orderName: "나이키 와플 트레이너 2 SD",
    //   totalAmount: orderData.totalAmount,
    //   currency: "CURRENCY_KRW",
    //   payMethod: orderData.paymentMethod,
    // });

    // /* 결제 성공하면 확정된 paymentId 저장 */
    // orderData.paymentNo = response.paymentId;

    // try {
    //     const payResponse = await fetch("/order/testPayment", {
    //         method: "POST",
    //         headers: { "Content-Type": "application/json" },
    //         body: JSON.stringify(orderData)
    //     });

    //     if(payResponse.ok) {
    //         const success = await serverResponse.text();
    //         alert(success);
    //         location.href = "/order/success";
        
    //     } else {
    //         const fail = await serverResponse.text();
    //         alert(fail);
    //     }

    // } catch(e) {
    //     alert("서버 통신 불가");
    // }


    /* ======================================================================= */
    /* ======================================================================= */

    const validationData = {
        // 주문자 회원 번호
        memberNo: 1,

        // 주문할 아이템들
        orderItems: [
            {
                productNo: 101,
                quantity: 2
            },

            {
                productNo: 102,
                quantity: 1
            }
        ]
    }

    /* 결제 요청 전에 사전 검증 */
    fetch("/order/validation", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(validationData)
    })
    .then(resp => resp.json())
    .then(result => {
        console.log("반환값 확인 : " + result);

    })
    .catch(e => console.log(e))


}
