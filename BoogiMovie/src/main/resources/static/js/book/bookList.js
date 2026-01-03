console.log("bookList loaded")


function getParams() {
    return new URLSearchParams(window.location.search);
}

// 페이지 이동해도 상태 유지
(function restoreState() {

    const params = getParams();

    // 카테고리
    const category = params.get('category') ?? '0';

    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.value === category);
    });

    // 정렬
    const sort = params.get('sort');
    const sortSelect = document.getElementById('list-sort');
    if (sort && sortSelect) {
        sortSelect.value = sort;
    }

    // 검색
    const query = params.get('query');
    const searchInput = document.getElementById('mini-search-input');
    if (query && searchInput) {
        searchInput.value = query;
    }


    // 하위 카테고리 선택 시 펼침 유지
    const novelSub = document.querySelector('.category-novel-group .category-sub');

    if (novelSub) {
        const catNum = Number(category);

        // category = 11 ~ 18 일 때만 펼쳐진 상태 유지
        if (!isNaN(catNum) && catNum >= 11 && catNum <= 18) {
            novelSub.classList.add('open');
        } else {
            novelSub.classList.remove('open');
        }
    }

})();

// 카테고리
document.querySelectorAll('.category-btn').forEach(btn => {
    
    btn.addEventListener('click', () => {

        const category = btn.dataset.value;
        const params = getParams();

        params.set('category', category);
        params.set('page', 1); // 카테고리 바뀌면 1페이지로

        location.href = '/books?' + params.toString();
    });
});

// 정렬
const sortSelect = document.getElementById('list-sort');

if (sortSelect) {
    sortSelect.addEventListener('change', () => {
        
        const params = getParams();

        params.set('sort', sortSelect.value);
        params.set('page', 1); // 정렬 변경 시 1페이지

        location.href = '/books?' + params.toString();
    });
}

// 검색
const searchBtn = document.getElementById('mini-search-btn');
const searchInput = document.getElementById('mini-search-input');

function applySearch() {
    const keyword = searchInput.value.trim();
    const params = getParams();

    if (keyword) {
        params.set('query', keyword);
    } else {
        params.delete('query');
    }

    params.set('page', 1);
    location.href = '/books?' + params.toString();
}

if (searchBtn && searchInput) {
    searchBtn.addEventListener('click', applySearch);

    // Enter 키 검색
    searchInput.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            e.preventDefault();
            applySearch();
        }
    });
}

// 페이지네이션
document.querySelectorAll('.pagination-link[data-page]').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();

        const page = link.dataset.page;
        const params = getParams();
        
        params.set('page', page);

        location.href = '/books?' + params.toString();
    });
});

// 글쓰기 버튼 동작
document.getElementById("write-btn")?.addEventListener("click", () => {
    location.href = `${location.pathname}/write`;
});