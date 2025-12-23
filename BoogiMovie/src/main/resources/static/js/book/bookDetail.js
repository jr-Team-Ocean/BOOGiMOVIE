const toggleBtn = document.querySelector('.open-toggle');
const desc = document.querySelector('.detail-text');

toggleBtn.addEventListener('click', () => {
    desc.classList.toggle('closed');

    toggleBtn.textContent = desc.classList.contains('closed') ? '더보기 ▼' : '접기 ▲';
});

// --------------------------------------------------------------------------
// --------------------------------------------------------------------------
// --------------------------------------------------------------------------










// 별점 기능 부분
const reviewWrap = document.querySelectorAll('.review-star.write-star'),
    label = document.querySelectorAll('.review-star .review-star-label'),
    input = document.querySelectorAll('.review-star .review-star-input'),
    labelLength = label.length,
    opacityHover = '1';

let stars = document.querySelectorAll('.review-star.write-star .star-icon')

checkedStar();

reviewWrap.forEach(wrap => {
    wrap.addEventListener('mouseenter', () => {
        stars = wrap.querySelectorAll('.star-icon');

        stars.forEach((starIcon, index) => {
            starIcon.addEventListener('mouseenter', () => {
                if (wrap.classList.contains('readonly') == false) {
                    initStars(); // 이전 선택 별점 무시하고 초기화?
                    filledStar(index, labelLength); // 호버링 카겟만큼 별점 활성

                    // 호버중 활성화됨 별점 opa 조정
                    for (let i = 0; i < stars.length; i++) {
                        if (stars[i].classList.contains('filled')) {
                            stars[i].style.opacity = opacityHover;
                        }
                    }
                    
                }
            });

            starIcon.addEventListener('mouseleave', () => {
                if (wrap.classList.contains('readonly') == false) {
                    starIcon.style.opacity = '1';
                    checkedStar(); // 누른 별점만큼 활서화
                    
                }
            });

            // 별점화면 밖으로 마우스 나가면 철
            wrap.addEventListener('mouseleave', () => {
                if (wrap.classList.contains('readonly') == false) {
                    starIcon.style.opacity = '1';
                }
            });

            // readonly일때 비활성화?
            wrap.addEventListener('click', (e) => {
                if (wrap.classList.contains('readonly')) {
                    e.preventDefault();
                }
            });
        });
    });
});

// 별 채우기
function filledStar(index, length) {
    if (index <= length) {
        for(let i = 0; i <= index; i++) {
            stars[i].classList.add('filled');
        }
    }
}

function checkedStar() {
    let checkedRadio = document.querySelectorAll('.review-star input[type="radio"]:checked');
    
    initStars();
    checkedRadio.forEach(radio => {
        let preSib = preAll(radio);

        for (let i = 0; i < preSib.length; i++) {
            preSib[i].querySelector('.star-icon').classList.add('filled');
        }

        radio.nextElementSibling.classList.add('filled');

        function preAll() {
            let radioSib = [],
                preSibling = radio.parentElement.previousElementSibling;

            while (preSibling) {
                radioSib.push(preSibling);
                preSibling = preSibling.previousElementSibling;
            }
            return radioSib;
            
        }
    });
}

// 별점 초기화
function initStars() {
    for (let i = 0; i < stars.length; i++) {
        stars[i].classList.remove('filled');
    }
    
}