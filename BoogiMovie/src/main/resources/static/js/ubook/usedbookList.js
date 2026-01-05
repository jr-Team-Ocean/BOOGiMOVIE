function getParams() {
    return new URLSearchParams(window.location.search);
}

// 페이지네이션
document.querySelectorAll('.pagination-link').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();

        
        const page = link.dataset.page
        const params = new URLSearchParams(window.location.search)
        
        if (!page) return;
        
        params.set('page', page)

        location.href = '/ubooks?' + params.toString();
    });
});


// 페이지 이동해도 상태 유지
(function restoreState() {

    const params = getParams();

    // 카테고리
    const category = params.get('category') ?? '0';

    document.querySelectorAll('.ucategoryBtn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.value === category);
    });

    // 정렬
    const sort = params.get('ubookSort');
    const sortSelect = document.getElementById('ubookListSort');
    if (sort && sortSelect) {
        sortSelect.value = sort;
    }

    

})();


// 카테고리
document.querySelectorAll('.ucategoryBtn').forEach(btn => {
    
    btn.addEventListener('click', () => {

        const category = btn.dataset.value;
        const params = getParams();

        params.set('category', category);
        params.set('page', 1); // 카테고리 바뀌면 1페이지로

        location.href = '/ubooks?' + params.toString();
    });
});

// 정렬
const sortSelect = document.getElementById('ubookListSort');

if (sortSelect) {
    sortSelect.addEventListener('change', () => {
        
        const params = getParams();

        params.set('ubookSort', sortSelect.value);
        params.set('page', 1); // 정렬 변경 시 1페이지

        location.href = '/ubooks?' + params.toString();
    });
}