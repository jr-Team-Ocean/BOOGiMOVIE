document.addEventListener("DOMContentLoaded", () => {

    const allCheckBox = document.getElementById("item_delete"); // 전체 선택 체크박스
    const allDeleteItemBtn = document.querySelector(".all_delete_item a"); // 전체 삭제 텍스트 버튼

    const checkBox = document.querySelectorAll(".product_checkbox"); // 개별 선택 체크박스
    const deleteBtn = document.querySelectorAll(".delete_btn"); // 개별 삭제 텍스트 버튼


    /* ======================================================================== */
    /* ======================================================================== */

    /* 가격 */
    const productPrice = document.querySelectorAll(".product_price"); // 상품 개별 가격
    const totalProductPrice = document.querySelector(".total_product_price"); // 전체 상품 개별 가격
    const deliveryPrice = document.querySelector(".delivery_price"); // 배송비
    const finalPrice = document.querySelector(".final_price"); // 최종 결제 예정 금액

    /* 수량 컨트롤 */
    const quantity = document.querySelectorAll(".quantity_input"); // 수량 input
    const quantityUp = document.querySelectorAll(".quantity_plus");
    const quantityDown = document.querySelectorAll(".quantity_minus");

    /* ======================================================================== */
    /* ======================================================================== */



    /* 초기 로딩 시 금액 계산 */
    updateTotalPrice();

    /* 전체 선택 및 해제 */
    allCheckBox.addEventListener("change", e => {
        const isChecked = e.target.checked; // 전체 선택 체크박스의 상태 (t/f)

        /* 모든 개별 체크박스를 돌면서 상태 맞춰주기 */
        checkBox.forEach(checkbox => {
            checkbox.checked = isChecked;
        });

        /* 가격 재계산 */
        updateTotalPrice();
        
    });

    /* 개별 체크 박스 하나라도 끄면 전체 선택 취소 */
    checkBox.forEach(checkbox => {
        checkbox.addEventListener("change", e => {
            /* 체크 개수와 전체 개수 같은지 확인 */
            const totalCount = checkBox.length;
            const checkedCount = document.querySelectorAll(".product_del_checkbox:checked").length;
            // console.log(totalCount);
            // console.log(checkedCount);

            /* 개별적으로 다 체크 되어있다면 전체 선택도 체크, 아니면 해제 */
            allCheckBox.checked = (totalCount == checkedCount);

            /* 가격 재계산 */
            updateTotalPrice();
        });
    });

    /* ======================================================================== */
    /* ======================================================================== */

    /* 개별 삭제 */
    deleteBtn.forEach(btn => {
        btn.addEventListener("click", e => {
            if(confirm("해당 상품을 삭제하시겠습니까?")) {
                /* 화면에서 해당 상품 컨테이너 통째로 삭제 */
                const itemBox = e.target.closest(".cart-item");
                const inputItemNo = document.querySelector(".itemNo"); // 삭제할 아이템 번호
                const itemNo = parseInt(inputItemNo.value);
                console.log(itemNo);

                /* 아이템 삭제 함수 호출 */
                deleteCartItem([itemNo], function() {
                    itemBox.remove(); // 성공 시 화면에서 지우기
                });

                itemBox.remove();
    
                /* 삭제 후 가격 다시 계산 */
                updateTotalPrice();

                /* 개별적으로 아이템 다 지우면 전체 선택 체크박스 끄기 */
                if(document.querySelectorAll(".cart-item").length === 0) {
                    allCheckBox.checked = false;
                }
            }
        })
    });

    /* ======================================================================== */
    /* ======================================================================== */

    /* 전체 삭제 */
    allDeleteItemBtn.addEventListener("click", e => {
        e.preventDefault(); // 기본 동작 막기
        
        const inputs = document.querySelectorAll(".itemNo");
        const cartNoList = [];

        inputs.forEach(input => cartNoList.push(parseInt(input.value)));

        if(confirm("장바구니를 비우시겠습니까?")) {
            /* 전체 선택 체크박스 해제 */
            allCheckBox.checked = false;

            deleteCartItem(cartNoList, function() {
                    inputs.forEach(input => input.closest(".cart-item").remove());
                });


        }
    });

    /* ======================================================================== */
    /* ======================================================================== */

    /* 삭제 비동기 요청 */
    function deleteCartItem(itemNo, callback) {
        console.log(itemNo);
        fetch("/cart/delete", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ cartNoList: itemNo })
            // "cartNoList": [1, 2, ...]
        })
        .then(resp => {
            if (resp.ok) return resp.text();
            throw new Error("장바구니 아이템 삭제 실패");
        })
        .then(msg => {
            
            if (callback) callback();
            updateTotalPrice();
            alert(msg);

            // 아이템 없으면 새로고침
            if (document.querySelectorAll(".cart-item").length === 0) {
                location.reload();
            }
        })
        .catch(e => console.log(e))
    }


    /* ======================================================================== */
    /* ======================================================================== */

    /* 수량 변경 (+) */
    quantityUp.forEach(up => {
        up.addEventListener("click", e => {
            const itemBox = e.target.closest(".cart-item");
            const input = itemBox.querySelector(".quantity_input");
            const inputItemNo = itemBox.querySelector(".itemNo"); // 수량 변경할 아이템 번호
            const itemNo = parseInt(inputItemNo.value);

            let currentVal = parseInt(input.value);
            input.value = currentVal + 1;

            let quantity = parseInt(input.value);
            updateQuantity(itemNo, quantity);
            // updateTotalPrice();
        });
    });
    
    /* 수량 변경 (-) */
    quantityDown.forEach(down => {
        down.addEventListener("click", e => {
            const itemBox = e.target.closest(".cart-item");
            const input = itemBox.querySelector(".quantity_input");
            const inputItemNo = itemBox.querySelector(".itemNo"); // 수량 변경할 아이템 번호
            const itemNo = parseInt(inputItemNo.value);

            let currentVal = parseInt(input.value);

            /* 수량이 1개 이상인 경우에만 감소 */
            if(currentVal > 1) {
                input.value = currentVal - 1;
                let quantity = parseInt(input.value);
                updateQuantity(itemNo, quantity);
                // updateTotalPrice();
            } else {
                alert("최소 주문 수량은 1개입니다.");
            }
        });
    });

    /* ======================================================================== */
    /* ======================================================================== */

    /* 수량 변경시 비동기 요청 */
    function updateQuantity(itemNo, quantity) {
        fetch("/cart/setQuantity", {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ itemNo: itemNo, quantity: quantity })
        })
        .then(resp => {
            if (resp.ok) return resp.text();
            throw new Error("수량 변경 세팅 실패");
        })
        .then(msg => {
            updateTotalPrice(); // 가격 업데이트
            console.log("수량 변경 완료!");
        })
        .catch(e => console.log(e))
    }

    /* ======================================================================== */
    /* ======================================================================== */


    /* 가격 계산 함수 */
    function updateTotalPrice() {
        let productTotal = 0; // 상품 금액

        /* 현재 화면에 남아있는 모든 아이템을 기준으로 계산 */
        const currentItems = document.querySelectorAll(".cart-item");
        currentItems.forEach(item => {
            const checkbox = item.querySelector(".product_checkbox");

            /* 체크박스가 선택된 것들만 계산*/
            if(checkbox.checked) {
                const productPrice = item.querySelector(".product_price"); // 개별 가격

                const quantityCount = item.querySelector(".quantity_input"); // 상품 수량

                /* 숫자만 추출 */
                const priceValue = parseInt(productPrice.innerText.replace(/[^0-9]/g, ""));

                let countValue = 1;
                if(quantityCount != null) {
                    countValue = parseInt(quantityCount.value);
                } 
                /* 상품 금액 */
                productTotal += priceValue * countValue;

            }
        }); // 반복문 끝

        /* =================================================== */
        /* =================================================== */

        /* 배송비 계산 */
        let deliveryFee = 0;

        if(productTotal === 0) {
            deliveryFee = 0;
        } else if(productTotal >= 30000) {
            /* 3만원 이상 배송비 무료 */
            // deliveryFee = 0;
        } else {
            /* 기본 3000원 */
            // deliveryFee = 3000;
        }

        /* 배송비 포함 */
        let lastTotal = productTotal + deliveryFee;
        

        /* 배송비 제외 상품 금액 */
        totalProductPrice.innerText = productTotal.toLocaleString() + "원";

        /* 배송비 */
        deliveryPrice.innerText = deliveryFee.toLocaleString() + "원";

        /* 배송비 포함 결제 예정 금액 */
        finalPrice.innerText = lastTotal.toLocaleString() + "원";

    }

    /* ===================================================================== */
    /* ===================================================================== */

    /* 쇼핑 계속하기 버튼 */
    const continueBtn = document.querySelector(".continue_shopping_btn");

    if (continueBtn) {
        continueBtn.addEventListener("click", function() {
            location.href = "/books"; 
        });
    }

    /* 장바구니에 상품이 없는 경우 홈으로 버튼 */
    const goHome = document.querySelector(".go_home");

    if (goHome) {
        goHome.addEventListener("click", function() {
            location.href = "/"; 
        });
    }
    
    /* ===================================================================== */
    /* ===================================================================== */
    
    /* 주문하기 눌렀을 경우 체크한 상품들만 결제창으로 값들 보내주기 */
    const orderBtn = document.querySelector(".order_btn > button");

    orderBtn.addEventListener("click", () => {
        /* 체크 되어있는 체크박스 가져오기 */
        const checkedBoxes = document.querySelectorAll(".product_checkbox:checked");

        /* 체크된 거 없다면 */
        if(checkedBoxes.length == 0) {
            alert("주문할 상품을 선택해주세요.")
            return;
        }

        /* 주문할 데이터 담을 배열 */
        const orderItemList = [];
        
        checkedBoxes.forEach(checkBox => {
            const parentBox = checkBox.closest(".cart-item"); // 상품 하나 박스
            const productNo = parentBox.querySelector(".productNo"); // 상품 번호
            const quantity = parentBox.querySelector(".quantity_input"); // 수량

            const quantityVal = quantity ? parseInt(quantity.value) : 1;

            const orderItem = {
                product_no: parseInt(productNo.value), // 카멜 표기법 / 스네이크 표기법 주의..
                quantity: parseInt(quantityVal)
            }
            
            console.log(orderItem);
            orderItemList.push(orderItem); // 리스트에 추가
        });

        // console.log(orderItemList);

        fetch("/cart/order", {
            method: "POST",
            headers: {"Content-Type" : "application/json"},
            body: JSON.stringify(orderItemList)
        })
        .then(resp => resp.text())
        .then(result => {
            console.log(result);

            if(result > 0) {
                location.href = "/order/delivery";
            } else {
                console.log("배송정보 이동 실패");
            }
        })
        .catch(e => console.log(e))

    })
});

