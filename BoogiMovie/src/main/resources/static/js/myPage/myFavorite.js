// 1. 요소 선택
const searchOrder = document.getElementById('search_order'); // 정렬 셀렉트 박스
const searchContent = document.querySelector('.search_content'); // 결과가 렌더링될 구역
const searchBoxMenu = document.getElementById('search_box_menu'); // "검색 결과" 헤더

// 2. 셀렉트 박스 변경 이벤트 리스너
searchOrder.addEventListener('change', () => {

    // 3. URLSearchParams 사용
    const params = new URLSearchParams();
    params.append('order', searchOrder.value);

    // 4. fetch 요청
    fetch('/myPage/searchResult?' + params.toString())
        .then(response => response.json())
        .then(data => { // 변수명을 list에서 data로 변경 (객체를 받으므로)
            
            // 5. 기존 결과 삭제
            const results = document.querySelectorAll('.search_box_results');
            results.forEach(el => el.remove());

            // ⭐ 중요: 실제 리스트는 data.favorites에 들어있음
            const list = data.favorites;

            console.log("서버에서 온 데이터 한 개 확인:", list[0]);

            // 6. 데이터가 없을 경우 처리
            if (!list || list.length === 0) {
                const emptyMsg = `
                    <div class="search_box_results" style="text-align: center; padding: 60px 20px; color: #999;">
                        <p style="font-size: 16px; margin-bottom: 10px;">찜한 상품이 없습니다.</p>
                    </div>`;
                searchContent.insertAdjacentHTML('beforeend', emptyMsg);
                return;
            }

            // 7. 받아온 리스트로 새로운 요소 생성
            list.forEach(favorite => {
                // 필드명 매칭 (서버 응답이 snake_case인 경우)
                const pNo = favorite.product_no;
                const pAuthor = favorite.product_author;
                const pTitle = favorite.product_title;
                const pImage = favorite.product_image;
                const pStatus = favorite.product_status;
                const pPrice = new Intl.NumberFormat().format(favorite.product_price || 0);
                const fDate = favorite.favorite_date; 

                const html = `
                    <div class="search_box_results">
                        
                        <div class="search_box_pic">
                            <a href="/product/detail/${pNo}">
                                <img src="${pImage}" alt="${pTitle}">
                            </a>
                        </div>

                        <div class="search_box_detail">
                            <div class="title">${pTitle}</div>
                            
                            <div>지은이 :${pAuthor}</div>
                            
                            <div class="price">
                                상품상태(${pStatus})/판매가격(${pPrice}원)
                            </div>
                        </div>

                        <div class="search_box_like">
                            <a href="#" onclick="removeFavorite(${pNo}, this); return false;">
                                <img src="/svg/heart_filled.svg" alt="찜 해제" style="width: 24px; height: 24px;">
                            </a>
                            <div>${fDate}</div>
                        </div>

                    </div>
                `;
                
                searchContent.insertAdjacentHTML('beforeend', html);
            });

            // 8. (추가 선택) 페이지네이션 업데이트 로직이 필요하다면 여기서 data.pageDto를 활용
        })
        .catch(err => console.error("조회 중 오류 발생:", err));
});




/** 찜 삭제 함수 */
function removeFavorite(productNo, btn) { // 1. btn 매개변수 추가
    if(!confirm("찜 목록에서 삭제하시겠습니까?")) return;
    
    // fetch 실행 전, 삭제할 카드를 미리 변수에 담아두는 것이 안전합니다.
    const productCard = btn.closest('.search_box_results'); 

    fetch('/myPage/deleteFavorite', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(productNo)
    })
    .then(response => {
        if (response.ok) {
            // 2. 미리 찾아둔 카드를 삭제합니다.
            if (productCard) {
                productCard.remove();
                console.log(`${productNo}번 상품 화면 삭제 완료`);
            }

            // 남은 아이템 확인 로직
            const remainingItems = document.querySelectorAll('.search_box_results');
            if (remainingItems.length === 0) {
                searchContent.innerHTML = `
                    <div class="search_box_results" style="text-align: center; padding: 60px 20px; color: #999;">
                        <p style="font-size: 16px; margin-bottom: 10px;">찜한 상품이 없습니다.</p>
                    </div>`;
            }
        } else {
            alert("삭제에 실패했습니다.");
        }
    })
    .catch(err => {
        console.error("삭제 요청 중 오류 발생:", err);
    });
}

