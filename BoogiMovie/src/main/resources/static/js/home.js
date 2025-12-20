document.addEventListener("DOMContentLoaded", function () {
    const container = document.querySelector('.best_seller');

    const slideArea = container.querySelector('.slide_area');
    const prevBtn = container.querySelector('.product_pre_btn');
    const nextBtn = container.querySelector('.product_next_btn');

    let currentIndex = 0;    // 현재 페이지 (0: 앞 5개, 1: 뒤 5개)
    const itemsPerSlide = 5; // 한 번에 보여줄 개수
    const itemWidth = 180;   // 카드 가로 길이
    const gap = 40;          // 카드 사이 간격

    // 한 번 이동할 거리 = (180 + 40) * 5 = 1100px
    const moveStep = (itemWidth + gap) * itemsPerSlide;

    const totalItems = slideArea.children.length;
    const maxIndex = Math.ceil(totalItems / itemsPerSlide) - 1;

    function updateSlide() {
        slideArea.style.transform = `translateX(-${currentIndex * moveStep}px)`;
        updateBtnState();
    }

    function updateBtnState() {
        // 첫 페이지면 이전 버튼 숨김
        if (currentIndex === 0) {
            prevBtn.style.visibility = 'hidden';
        } else {
            prevBtn.style.visibility = 'visible';
        }

        // 마지막 페이지면 다음 버튼 숨김
        if (currentIndex >= maxIndex) {
            nextBtn.style.visibility = 'hidden';
        } else {
            nextBtn.style.visibility = 'visible';
        }
    }

    // 다음 버튼
    nextBtn.addEventListener('click', () => {
        if (currentIndex < maxIndex) {
            currentIndex++;
            updateSlide();
        }
    });

    // 이전 버튼
    prevBtn.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateSlide();
        }
    });

    updateBtnState();
});