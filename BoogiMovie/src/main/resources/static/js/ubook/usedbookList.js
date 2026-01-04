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