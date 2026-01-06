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


document.addEventListener("DOMContentLoaded", () => {

    // 소설/희곡 버튼
    const novelBtn = document.querySelector(
        '.ucategoryBtn[data-value="11"]'
    );

    // 하위 카테고리
    const subCategory = document.querySelector(".category_small");

    if (!novelBtn || !subCategory) return;

    // 처음엔 숨김
    subCategory.style.display = "none";

    // 상위 카테고리 hover
    novelBtn.addEventListener("mouseenter", () => {
        subCategory.style.display = "flex";
    });

    // 하위 카테고리 hover 유지
    subCategory.addEventListener("mouseenter", () => {
        subCategory.style.display = "flex";
    });

    // 상위에서 나갔을 때
    novelBtn.addEventListener("mouseleave", () => {
        setTimeout(() => {
            if (!subCategory.matches(":hover")) {
                subCategory.style.display = "none";
            }
        }, 100);
    });

    // 하위에서 나갔을 때
    subCategory.addEventListener("mouseleave", () => {
        subCategory.style.display = "none";
    });

});


document.getElementById('enroll').addEventListener('click', () => {

    location.href = `/ubooks/insert`;

})