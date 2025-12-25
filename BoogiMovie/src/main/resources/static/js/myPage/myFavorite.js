console.log('myFavorite.js')

let currentKeyword = ''; // 현재 검색어 저장
let currentSort = 'recent'; // 현재 정렬 방식

document.addEventListener('DOMContentLoaded', () => {

    // 정렬 드롭다운 이벤트
    const searchOrder = document.querySelector('.search_order');
    if (searchOrder) {
        searchOrder.addEventListener('change', handleSortChange);
    }

    // 페이지네이션 이벤트 (동적 이벤트 위임)
    const pagination = document.querySelector('.pagination');
    if (pagination) {
        pagination.addEventListener('click', handlePaginationClick);
    }

    // 검색 기능
    setupSearchFunction();

    return;
})


// ========== 검색 기능 ==========

/**
 * 검색 기능 설정
 */
function setupSearchFunction() {
    const selectContainer = document.querySelector('.select_container');
    const searchInput = selectContainer ? selectContainer.parentElement.querySelector('input[type="text"]') : null;
    
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                currentKeyword = this.value.trim();
                loadFavorites(1);
            }
        });
    }
}


// ========== 정렬 처리 ==========

/**
 * 정렬 방식 변경
 */
function handleSortChange(e) {
    const value = e.target.value;
    
    currentSort = value; // 'recent', 'name', 'price'
    loadFavorites(1);
    
    console.log('정렬 방식 변경:', currentSort);
}


// ========== 페이지네이션 ==========

/**
 * 페이지네이션 클릭 처리 (이벤트 위임)
 */
function handlePaginationClick(e) {
    if (e.target.tagName !== 'A') return;
    
    e.preventDefault();
    
    const pageNum = parseInt(e.target.getAttribute('data-page'));
    
    if (!isNaN(pageNum) && pageNum > 0) {
        loadFavorites(pageNum);
    }
}


// ========== 찜한 상품 목록 ==========

/**
 * 찜한 상품 목록 조회
 * 서버 API: GET /favorites/list?cp={page}&sort={sort}&keyword={keyword}
 */
function loadFavorites(cp = 1) {
    const keyword = currentKeyword || '';
    const sort = currentSort || 'recent';
    
    let url = `/favorites/list?cp=${cp}&sort=${sort}`;
    if (keyword) {
        url += `&keyword=${encodeURIComponent(keyword)}`;
    }
    
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('조회된 데이터:', data);
            
            let favoriteList, pagination;
            
            if (Array.isArray(data)) {
                favoriteList = data;
                pagination = null;
            } else {
                favoriteList = data.favoriteList || data.list || [];
                pagination = data.pagination || data.pageInfo;
            }
            
            if (favoriteList && favoriteList.length > 0) {
                renderFavoritesList(favoriteList);
                if (pagination) {
                    renderPagination(pagination);
                }
            } else {
                showEmptyMessage();
            }
        })
        .catch(error => {
            console.error('데이터 조회 실패:', error);
            alert('찜한 상품을 불러오는데 실패했습니다.');
            showEmptyMessage();
        });
}

/**
 * 찜한 상품 목록 렌더링
 */
function renderFavoritesList(favoriteList) {
    console.log('renderFavoritesList 호출됨', favoriteList);

    const container = document.querySelector('.search_content');
    
    if (!container) {
        console.error('search_content 컨테이너를 찾을 수 없습니다.');
        return;
    }
    
    // search_box_menu 제외한 기존 결과 제거
    const results = container.querySelectorAll('.search_box_results');
    results.forEach(el => el.remove());
    
    // 빈 메시지 제거
    const emptyDiv = container.querySelector('[style*="text-align: center"]');
    if (emptyDiv) {
        emptyDiv.remove();
    }
    
    favoriteList.forEach((item) => {
        const resultDiv = document.createElement('div');
        resultDiv.classList.add('search_box_results');
        
        // 상품 이미지
        const picDiv = document.createElement('div');
        picDiv.classList.add('search_box_pic');
        const imgLink = document.createElement('a');
        imgLink.href = '#';
        const img = document.createElement('img');
        img.src = item.productImage || '../../static/images/sample_book.png';
        img.alt = item.productTitle || '상품 이미지';
        imgLink.appendChild(img);
        picDiv.appendChild(imgLink);
        
        // 상품 정보
        const detailDiv = document.createElement('div');
        detailDiv.classList.add('search_box_detail');
        
        const titleDiv = document.createElement('div');
        titleDiv.classList.add('title');
        titleDiv.innerText = item.productTitle || '상품명 없음';
        
        const authorDiv = document.createElement('div');
        authorDiv.classList.add('writer');
        authorDiv.innerText = '지은이 : ' + (item.productAuthor || '정보 없음');
        
        const priceDiv = document.createElement('div');
        priceDiv.classList.add('price');
        priceDiv.innerText = '상품상태(' + (item.productStatus || '미분류') + ')/판매가격(' + 
                           (item.productPrice ? formatPrice(item.productPrice) + '원' : '가격정보 없음') + ')';
        
        detailDiv.appendChild(titleDiv);
        detailDiv.appendChild(authorDiv);
        detailDiv.appendChild(priceDiv);
        
        // 찜 정보
        const likeDiv = document.createElement('div');
        likeDiv.classList.add('search_box_like');
        
        const heartLink = document.createElement('a');
        heartLink.href = '#';
        heartLink.addEventListener('click', (e) => {
            e.preventDefault();
            removeFavorite(item.favoriteNo);
        });
        
        const heartImg = document.createElement('img');
        heartImg.src = '../../static/svg/heart_filled.svg';
        heartImg.alt = '찜 해제';
        heartLink.appendChild(heartImg);
        
        const dateDiv = document.createElement('div');
        dateDiv.innerText = formatDate(item.favoriteDate);
        
        likeDiv.appendChild(heartLink);
        likeDiv.appendChild(dateDiv);
        
        // 전체 조립
        resultDiv.appendChild(picDiv);
        resultDiv.appendChild(detailDiv);
        resultDiv.appendChild(likeDiv);
        
        container.appendChild(resultDiv);
    });
}

/**
 * 페이지네이션 렌더링
 */
function renderPagination(pagination) {
    const paginationDiv = document.querySelector('.pagination');
    
    if (!paginationDiv || !pagination) {
        return;
    }
    
    let html = '';
    
    // 처음 («)
    html += `<a href="#" data-page="1">&laquo;</a>`;
    
    // 이전 (<)
    html += `<a href="#" data-page="${pagination.prevPage}">&lt;</a>`;
    
    // 페이지 번호
    for (let i = pagination.startPage; i <= pagination.endPage; i++) {
        if (i === pagination.currentPage) {
            html += `<a href="#" class="active" data-page="${i}">${i}</a>`;
        } else {
            html += `<a href="#" data-page="${i}">${i}</a>`;
        }
    }
    
    // 다음 (>)
    html += `<a href="#" data-page="${pagination.nextPage}">&gt;</a>`;
    
    // 마지막 (»)
    html += `<a href="#" data-page="${pagination.maxPage}">&raquo;</a>`;
    
    paginationDiv.innerHTML = html;
}

/**
 * 데이터가 없을 때 메시지 표시
 */
function showEmptyMessage() {
    const container = document.querySelector('.search_content');
    if (container) {
        // 기존 결과 제거
        const results = container.querySelectorAll('.search_box_results');
        results.forEach(el => el.remove());
        
        // 기존 빈 메시지 제거
        const existingEmpty = container.querySelector('[style*="text-align: center"]');
        if (existingEmpty) {
            existingEmpty.remove();
        }
        
        // 빈 메시지 추가
        const emptyDiv = document.createElement('div');
        emptyDiv.style.textAlign = 'center';
        emptyDiv.style.padding = '60px 20px';
        emptyDiv.style.color = '#999';
        emptyDiv.innerHTML = `
            <p style="font-size: 16px; margin-bottom: 10px;">찜한 상품이 없습니다.</p>
            <p style="font-size: 14px;">마음에 드는 상품을 찜해보세요!</p>
        `;
        container.appendChild(emptyDiv);
    }
    
    const paginationDiv = document.querySelector('.pagination');
    if (paginationDiv) {
        paginationDiv.innerHTML = '';
    }
}

/**
 * 찜 해제
 */
function removeFavorite(favoriteNo) {
    if (!confirm('이 상품을 찜 해제하시겠습니까?')) {
        return;
    }

    fetch(`/favorites/remove/${favoriteNo}`, {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'}
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                alert('찜 해제되었습니다.');
                loadFavorites(1);
            } else {
                alert(result.message || '찜 해제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('찜 해제 실패:', error);
            alert('찜 해제 중 오류가 발생했습니다.');
        });
}

/**
 * 가격 포맷팅 (천 단위 콤마)
 */
function formatPrice(price) {
    if (!price || price === 0) return '0';
    return Number(price).toLocaleString('ko-KR');
}

/**
 * 날짜 포맷팅 (YYYY년 MM월 DD일)
 */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    
    try {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}년 ${month}월 ${day}일`;
    } catch (error) {
        console.error('날짜 포맷팅 실패:', error);
        return dateStr;
    }
}