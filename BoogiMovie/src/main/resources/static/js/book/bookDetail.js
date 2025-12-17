const toggleBtn = document.querySelector('.open-toggle');
const desc = document.querySelector('.detail-text');

toggleBtn.addEventListener('click', () => {
    desc.classList.toggle('closed');

    toggleBtn.textContent = desc.classList.contains('closed') ? '더보기 ▼' : '접기 ▲';
});