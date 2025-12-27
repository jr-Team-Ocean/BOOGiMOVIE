console.log("bookList loaded")

document.querySelectorAll('.category-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const category = btn.dataset.value;

        const params = new URLSearchParams(window.location.search);
        params.set('category', category);
        params.set('page', 1); // 카테고리 바뀌면 1페이지로

        location.href = '/books?' + params.toString();
    });
});