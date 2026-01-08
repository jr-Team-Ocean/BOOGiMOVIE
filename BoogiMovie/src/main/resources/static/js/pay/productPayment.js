/* 도서 / 중고도서 / 영화 상세조회 페이지에서 결제시 */

/* ================================================ */
/*                     포트원                        */
/* ================================================ */

console.log("productPayment.js");

/* 결제정보 수정 창에서 결제 취소시 뒤로가기 */
const cancelPayment = document.querySelector("#cancel_payment");
cancelPayment.addEventListener("click", () => {
    if(confirm("결제 진행을 취소하시겠습니까?")) {
        history.back();
    }
})


/* ================================================ */
/* ================================================ */

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

    /* ================================================================================ */
    /* ================================================================================ */

    // 결제 버튼 요소 찾아서 넣기
    // const paymentBtn = document.querySelector("#payment-btn");
    const paymentBtn = document.querySelector(".order_btn");
    const orderMemberNo = document.querySelector("#orderMemberNo"); // 회원 번호
    const payMethod = document.querySelector("#simplePay");


    /* 주문하려는 아이템 값 */
    const orderItems = [];
        itemElements.forEach(item => {
            orderItems.push({
                product_no: parseInt(item.dataset.productNo),
                quantity: parseInt(item.dataset.quantity)
            });
        });


    if(orderItems.length === 0) {
            alert("주문할 상품이 없습니다.");
            return;
        }

    const validationData = {
        member_no: orderMemberNo.value, // 회원 번호    
        order_items: orderItems         // 상품 리스트
    };

    paymentBtn.addEventListener("click", e => {
        e.preventDefault();
        payment(validationData, needsDelivery, payMethod);
    })

})

async function payment(validationData, needsDelivery, payMethod) {

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
            payMethod: payMethod.value,
            customer: {
                fullName: resp.recipient_name,
                phoneNumber: resp.recipient_phone
            }
        });

        /* code가 있으면 결제 실패 -> 결제 데이터들 삭제 */
        if (response.code !== undefined) {
            console.log("결제 실패 / 취소 : ", response.message);
            console.log(resp.order_no);
            alert("결제가 취소 되었습니다.");

            await fetch("/order/fail", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: resp.order_no // 실패 또는 취소한 주문번호
            });

        } else {
            /* 결제 성공시 사후 검증(최종 승인) */
            /* 이때 delivery 테이블에도 배송 정보 저장 */
            console.log("결제 성공, 사후 검증 요청 : ", response.txId); // 결제 시도 고유 ID

            /* 배송지 정보 */
            const recipientName = document.querySelector(".orderer_name").value;
            const recipientTel = document.querySelector(".orderer_phone").value;
            let orderRequest = null;
            let postCode = null;
            let roadAddress = null;
            let detailAddress = null;

            /* 배송 정보가 필요한 경우에만 세팅 */
            if (needsDelivery) {
                postCode = document.querySelector("#sample6_postcode").value;
                roadAddress = document.querySelector("#sample6_address").value;
                detailAddress = document.querySelector("#sample6_detailAddress").value;
                orderRequest = document.querySelector(".order_request").value;
                
                const reqInput = document.querySelector(".order_request");
                if(reqInput) requestMsg = reqInput.value;
            }

            /* 검증 및 결제수단/배송 테이블에 넣을 값들 담아서 보내주기 */
            const data = {
                order_no: resp.order_no,

                pay_no: response.txId, // 결제 시도 고유 ID
                pay_method: payMethod.value,
                pay_price: resp.total_price, // 총 결제 금액

                /* delivery 테이블에 넣을 값들 */
                recipient_name: recipientName,     // 주문자명
                recipient_tel: recipientTel,       // 주문자 휴대폰번호
                order_request: orderRequest,       // 요청사항
                post_code: postCode,               // 우편번호
                road_address: roadAddress,         // 도로명 주소
                detail_address: detailAddress      // 상세 주소
            }

            const verifyResp = await fetch("/order/payment", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            })

            if(verifyResp.ok) {
                alert("결제 완료하였습니다.");
                location.href = "/order/complete/" + resp.order_no;
            } else {
                alert("결제 진행 중 문제 발생");
            }

            
        }

    } catch (e) {
        console.error(e);
        alert("결제 진행 중 오류 발생");
    }

}