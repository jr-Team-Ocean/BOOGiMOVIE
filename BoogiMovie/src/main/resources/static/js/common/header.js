document.addEventListener("DOMContentLoaded", () => {
    const searchList = document.querySelector(".popular_search_list");
    let items = searchList.querySelectorAll("li");

    const itemHeight = 20; // li 하나 높이
    let currentIdx = 0;

    const firstItemClone = items[0].cloneNode(true);
    searchList.appendChild(firstItemClone);

    setInterval(() => {
        currentIdx++;

        searchList.style.transition = "transform 0.5s ease-in-out";
        searchList.style.transform = `translateY(-${currentIdx * itemHeight}px)`;

        if (currentIdx === items.length) {

            setTimeout(() => {
                searchList.style.transition = "none"; // 애니메이션 끄기
                searchList.style.transform = "translateY(0)"; // 처음으로 이동
                currentIdx = 0; // 인덱스 초기화
            }, 500);
        }
    }, 3000); // 3초마다 실행

    /* ============================================================================ */
    /* ============================================================================ */

    /* 통합검색 */

    const searchInput = document.querySelector('.search_input input'); // 검색바
    const searchDropdown = document.querySelector('.search_dropdown'); // 결과 박스

    searchInput.addEventListener('input', e => {
        const keyword = e.target.value.trim();

        
        /* 검색어 없으면 결과 박스 숨김 */
        if (keyword.length === 0) {
            searchDropdown.style.display = 'none';
            return;
        }

        
        /* 사용자가 엔터를 보냈을 경우 로그 찍기 (엔터 상태값) */
        // if(e.keyword == 'Enter') {
        //     console.log("엔터 입력");
        // }

        const isEnter = '&isEnter=yes';

        fetch(`/search?query=${keyword}&isEnter=${isEnter}`)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                renderSearchData(data);

                searchDropdown.style.display = 'flex';
            })
            .catch(error => {
                console.error('검색 실패', error);
            });
    });


    /* ============================================================================ */
    /* ============================================================================ */

    function renderSearchData(data) {
        searchDropdown.innerHTML = '';
        let hasResult = false; // 결과가 하나라도 있는지 체크용

        /* 도서가 있는 경우 */
        if (data.books && data.books.length > 0) {
            const categoryHtml = makeCategoryHtml('도서', data.books, 'books');

                        /* 요소 바로 안에서 마지막 자식 이후에 위치 */
            searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
            hasResult = true;
        }

        /* 중고 도서가 있는 경우 */
        if (data.usedBooks && data.usedBooks.length > 0) {
            const categoryHtml = makeCategoryHtml('중고도서', data.usedBook, 'ubooks');
            searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
            hasResult = true;
        }

        /* 영화가 있는 경우 */
        if (data.movies && data.movies.length > 0) {
            const categoryHtml = makeCategoryHtml('영화', data.movies, 'movies');
            searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
            hasResult = true;
        }

        // 데이터가 없는 경우
        if (!hasResult) {
            searchDropdown.innerHTML = '<div style="padding:20px; text-align:center;">검색 결과가 없습니다.</div>';
        }
    }

    /* 검색 결과 그리기 */
    function makeCategoryHtml(categoryTitle, items, urlCategory) {
        let itemsHtml = '';
        items.forEach(item => {
            const detailLink = `/${urlCategory}/${item.product_no}`;
            itemsHtml += `
                <a href="${detailLink}" class="category_a">
                    <div class="dropdown_item">
                        
                        <div class="dropdown_img">
                            <img src="${item.img_path}" alt="${item.product_title}"> 
                        </div>
                        
                        <div class="dropdown_text_area">
                            
                            <div class="dropdown_item_name dropdown_item_title" style="font-weight: bold; font-size: 15px; margin-bottom: 2px; color: #333;">
                                ${item.product_title}
                            </div>
                            
                            <div class="dropdown_item_name dropdown_item_publisher" style="color: #666;">
                                ${item.publisher || ''} 
                            </div>

                            <div class="dropdown_item_name dropdown_item_creator" style="color: #999;">
                                ${item.creator || ''}
                            </div>

                        </div>
                    </div>
                </a>
            `;
        });

        return `
        <div class="search_category">
            <div class="dropdown_category bold font-18px">${categoryTitle}</div>
            <div class="search_result_container">
                ${itemsHtml}
            </div>
        </div>
    `;
    }

    updateHeaderUnreadCount();
});

/* ============================================================================ */
    /* 안읽은 알림 카운트 동기화 (홈 이동 시 초기화 방지) */
/* ============================================================================ */

    function updateHeaderUnreadCount() {
    console.log('=== updateHeaderUnreadCount 시작 ===');
    console.log('현재 경로:', window.location.pathname);
    
    if (window.location.pathname.includes('/chatting')) {
        console.log('채팅 페이지이므로 업데이트 건너뜀');
        return;
    }
    
    console.log('fetch 요청 시작: /chatting/totalUnreadCount');
    
    fetch('/chatting/totalUnreadCount') 
        .then(resp => {
            console.log('응답 상태:', resp.status);
            return resp.text();
        })
        .then(count => {
            console.log('받아온 카운트:', count);
            console.log('카운트 타입:', typeof count);
            
            const headerBadge = document.getElementById('headerUnreadBadge');
            console.log('headerBadge 요소:', headerBadge);
            
            // ✅ 요소가 없으면 (로그인 안 했거나 권한 없음) 조용히 종료
            if (!headerBadge) {
                console.log('배지 요소가 없습니다 (로그인 안 했거나 권한 없음)');
                return;
            }
            
            const cnt = Number(count);
            console.log('숫자로 변환된 카운트:', cnt);
            headerBadge.innerText = cnt;
            headerBadge.style.display = cnt > 0 ? 'inline-block' : 'none';
            console.log('✅ 배지 업데이트 완료');
        })
        .catch(err => console.error("헤더 카운트 동기화 실패:", err));
}