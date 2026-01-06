const toggleBtn = document.querySelector('.open-toggle');
const desc = document.querySelector('.detail-text');

toggleBtn.addEventListener('click', () => {
    desc.classList.toggle('closed');

    toggleBtn.textContent = desc.classList.contains('closed') ? '더보기 ▼' : '접기 ▲';
});

// --------------------------------------------------------------------------
// --------------------------------------------------------------------------
// --------------------------------------------------------------------------

const writeArea = document.querySelector('.review-write-area');
const writeBtn = document.querySelector('.review-write-btn');
const targets = document.querySelectorAll(
  '.review-star, .review-write-box, .review-write-controller'
);

const cancelBtn = document.querySelector('.review-write-cancel');

writeBtn?.addEventListener('click', () => {
    writeArea.classList.add('open');
    targets.forEach(el => el.classList.remove('hidden-review'));
});

cancelBtn?.addEventListener('click', () => {
    writeArea.classList.remove('open');
    targets.forEach(el => el.classList.add('hidden-review'));
});

// 후기 등록 (fetch)
document.querySelector('.review-write-submit')?.addEventListener('click', () => {

    // 1. 별점
    const checked = document.querySelector(
        '.review-star.write-star input[name="star"]:checked'
    );

    if (!checked) {
        alert('별점을 선택해주세요.');
        return;
    }

    const reviewScore = Number(checked.id.split('-')[1]);

    // 2. 내용
    const contentInput = document.querySelector('.review-write-input');
    const reviewContent = contentInput.value.trim();

    if (reviewContent.length === 0) {
        alert('후기 내용을 입력해주세요.');
        return;
    }

    // 3. 서버 전송
    fetch(`/books/${productNo}/reviews`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            reviewScore,
            reviewContent
        })
    })
    .then(resp => resp.text())
    
    .then(result => {
        if (result != 1) {
            alert('후기 등록에 실패했습니다.');
            return;
        }

        // 후기 목록 비동기 갱신
        alert("후기를 등록하였습니다.!")
        loadReviewList();

        // UI 초기화
        contentInput.value = '';
        document
          .querySelectorAll('.review-star.write-star input[name="star"]')
          .forEach(r => r.checked = false);

        document
          .querySelectorAll('.review-star.write-star .star-icon')
          .forEach(star => star.classList.remove('filled'));

        // 작성 영역 닫기
        writeArea.classList.remove('open');
        targets.forEach(el => el.classList.add('hidden-review'));
    })
    .catch(err => console.error(err));
});





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



// 글쓰기 버튼 동작
document.getElementById("update-btn")?.addEventListener("click", () => {
    location.href = `${location.pathname}/update`;
});

document.getElementById("delete-btn")?.addEventListener("click", () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const form = document.createElement("form");
    form.method = "post";
    form.action = `${location.pathname}/delete`;

    document.body.appendChild(form);
    form.submit();
});






const bookLike = document.getElementById("bookLike");

bookLike.addEventListener("click", e => {

    // 로그인 여부
    if (loginMemberNo == "") {
        alert("로그인 후 이용해주세요.");
        return;
    }


    let check; // 0= 안함, 1 = 함

    // 좋아요 여부
    if (e.target.classList.contains("fa-regular")) {
        check = 0;
    } else {
        check = 1;
    }

    // 서버로 보낼 데이터
    const data = {
        memberNo  : loginMemberNo,
        productNo : productNo,
        check     : check
    };

    // 비동기
    fetch("/books/like", {
        method  : "POST",
        headers : { "Content-Type" : "application/json" },
        body    : JSON.stringify(data)
    })
    .then(resp => resp.text())
    .then(count => {

        // 실패
        if (count == -1) {
            alert("좋아요 처리 중 문제가 발생했습니다.");
            return;
        }

        // 토글
        e.target.classList.toggle("fa-regular");
        e.target.classList.toggle("fa-solid");

        // 좋아요 수
        document.getElementById("likeCount").innerText = count;
    })
    .catch(err => console.log(err));
});


document.querySelector('.review-list-area').addEventListener('click', e => {

    if (e.target.classList.contains('review-more-btn')) {
        document.querySelectorAll('.review-box.hidden-review')
            .forEach(el => el.classList.remove('hidden-review'));

        e.target.style.display = 'none';
    }

});

// 후기 삭제
document.addEventListener('click', e => {
    if (!e.target.classList.contains('review-delete-btn')) return;

    const reviewBox = e.target.closest('.review-box');
    const reviewNo = reviewBox?.dataset.reviewNo;

    if (!reviewNo) {
        console.error('reviewNo 없음');
        return;
    }

    if (!confirm('정말 삭제하시겠습니까?')) return;

    fetch(`/books/reviews/${reviewNo}/delete`, {
        method: 'POST'
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert('삭제 실패');
            return;
        }

        alert('삭제되었습니다');
        loadReviewList();
    });
});



// 후기 수정 / 취소 (이벤트 위임)
const reviewListArea = document.querySelector('.review-list-area');

reviewListArea?.addEventListener('click', (e) => {

    // 수정버튼
    const editBtn = e.target.closest('.review-edit-btn');
    if (editBtn) {
        const box = editBtn.closest('.review-box');
        if (!box) return;

        
        const controll = box.querySelector('.review-controll');
        const editArea = box.querySelector('.review-edit-area');
        const editInput = box.querySelector('.review-edit-input');

        // 기존 내용 → input에 세팅
        const currentText =
            box.querySelector('.review-text span')?.textContent ?? '';
        editInput.value = currentText.trim();

        // 전환
        controll.classList.add('hidden-review');
        editArea.classList.remove('hidden-review');

        return;
    }

    // 취소
    const cancelBtn = e.target.closest('.review-edit-cancel');
    if (cancelBtn) {
        const box = cancelBtn.closest('.review-box');
        if (!box) return;

        const detail   = box.querySelector('.review-detail');
        const controll = box.querySelector('.review-controll');
        const editArea = box.querySelector('.review-edit-area');

        // 복원
        editArea.classList.add('hidden-review');
        detail.classList.remove('hidden-review');
        controll.classList.remove('hidden-review');

        return;
    }
});

// 후기 수정 submit
reviewListArea?.addEventListener('click', (e) => {
    if (!e.target.classList.contains('review-edit-submit')) return;

    const box = e.target.closest('.review-box');
    const reviewNo = box?.dataset.reviewNo;
    const newContent = box
        .querySelector('.review-edit-input')
        ?.value.trim();

    if (!reviewNo || !newContent) {
        alert('수정할 내용이 없습니다.');
        return;
    }

    fetch(`/books/reviews/${reviewNo}/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ reviewContent: newContent })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert('수정 실패');
            return;
        }

        alert('수정되었습니다');
        loadReviewList(); 
    });
});






// 후기 목록 다시 로드 (fragment)
function loadReviewList() {
    fetch(`/books/${productNo}/reviews`)
        .then(resp => resp.text())
        .then(html => {
            document
                .querySelector('.review-list-area')
                .innerHTML = html;
        })
        .catch(err => console.error(err));
}



// 장바구니
document.querySelector('.add-cart').addEventListener('click', () => {

    // 로그인 체크
    if (loginMemberNo === "") {
        alert("로그인 후 이용해주세요.");
        return;
    }

    const buyCount = document.querySelector('.book-price input[type="number"]');

    const quantity = Number(buyCount.value);

    if (quantity < 1) {
        alert("수량은 1개 이상이어야 합니다.");
        return;
    }

    fetch(`/cart/addCart`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            productNo,
            quantity
        })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert("장바구니 담기에 실패했습니다.");
            return;
        }

        if (confirm("장바구니에 담았습니다. 이동하시겠습니까?")) {
            location.href = "/cart";
        }
    })
    .catch(err => console.error(err));
});