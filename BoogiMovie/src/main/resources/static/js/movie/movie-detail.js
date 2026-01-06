console.log('movie-detail.js loaded....')

const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');

tabButtons.forEach((button, index) => {
    button.addEventListener('click', () => {

        // 버튼 active 처리
        tabButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        // 콘텐츠 전환
        tabContents.forEach(content => content.classList.remove('active'));
        tabContents[index].classList.add('active');
    });
});


// 더보기/접기 버튼
const plusBtn = document.querySelector('.open-toggle')
const movieInfo = document.querySelector('.movie-pre')

plusBtn.addEventListener('click', ()=>{
    movieInfo.classList.toggle('hidden');
    
    plusBtn.textContent = movieInfo.classList.contains('hidden') ? '더보기 ▼' : '접기 ▲';
})


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
                    checkedStar(); // 누른 별점만큼 활성화
                    
                }
            });

            // 별점화면 밖으로 마우스 나가면 처리
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


// ====================================================================

// 관람평
const reviewBtn = document.querySelector('.review-btn')
const sendBtn = document.getElementsByClassName('btn-area')[0]
const reviewInput = document.querySelector('.review-write')
const reviewStar = document.querySelector('.review-star')

reviewBtn?.addEventListener('click', ()=>{
    sendBtn.classList.remove('closed')
    reviewInput.classList.remove('closed')
    reviewStar.classList.remove('closed')
})

// 후기 등록(fetch)
document.querySelector('.submit-btn')?.addEventListener('click', () => {

    // 1. 별점
    const checked = document.querySelector('.review-star.write-star input[name="star"]:checked')

    if(!checked){
        alert('별점을 선택해주세요.');
        return;
    }

    const reviewScore = Number(checked.id.split('-')[1])

    // 2. 내용
    const contentInput = document.querySelector('.review-input')
    const reviewContent = contentInput.value.trim();

    if(reviewContent.length === 0){
        alert('후기 내용을 입력해주세요.')
        return;
    }

    // 3. 서버 전송
    fetch(`/movies/${productNo}/review`, {
        method: 'POST',
        headers : {'Content-Type' : 'application/json'},
        body : JSON.stringify({reviewScore, reviewContent})
    })
    .then(resp => resp.text())
    .then(result => {
        console.log('result', result)

        if(result != 1){
            alert('후기 등록에 실패했습니다.');
            return;
        }

        // 후기 목록 비동기
        alert('후기를 등록하였습니다.')
        loadReviewList();

        // UI 초기화
        contentInput.value = "";
        document
            .querySelectorAll('.review-star.write-star input[name="star"]')
            .forEach(r => r.checked = false);

        document
            .querySelectorAll('.review-star.write-star .star-icon')
            .forEach(star => star.classList.remove('filled'));

        // 작성 영역 닫기
        sendBtn.classList.add('closed')
        reviewInput.classList.add('closed')
        reviewStar.classList.add('closed')
    })
    .catch(err => console.log(err))
})

// 후기 수정/취소(이벤트 위임)
const reviewListArea = document.querySelector('.review-list-area');

reviewListArea?.addEventListener('click', e => {

    // 수정버튼
    const editBtn = e.target.closest('.update-review-btn')
    if(editBtn){
        const box = editBtn.closest('.yes-review')
        if (!box) return;

        const buttons = box.querySelector('.buttons')
        const editArea = box.querySelector('.review-edit')
        const editInput = box.querySelector('.review-update-input')
    
        // 기존 내용 -> input에 세팅
        const currentText = 
            box.querySelector('.review-text span')?.textContent ?? '';
        editInput.value = currentText.trim();

        // 전환
        buttons.classList.add('closed')
        editArea.classList.remove('closed')

        return;
    }

    // 취소
    const cancelBtn = e.target.closest('.reset-btn')
    // console.log("cancelBtn", cancelBtn)
    if(cancelBtn){
        const box = cancelBtn.closest('.yes-review')
        if(!box) return;

        const content = box.querySelector('.review-content')
        const buttons = box.querySelector('.buttons')
        const editArea = box.querySelector('.review-edit')

        // 복원
        editArea.classList.add('closed')
        buttons.classList.remove('closed')
        content.classList.remove('closed')
    }

})

// 후기 수정 submit
reviewListArea?.addEventListener('click', e => {
    if(!e.target.classList.contains('update-btn')) return;

    const box = e.target.closest('.yes-review')
    const reviewNo = box?.dataset.reviewNo;
    const updateContent = box.querySelector('.review-update-input')?.value.trim()

    if(!reviewNo || !updateContent){
        alert('수정할 내용이 없습니다.');
        return;
    }

    fetch(`/movies/review/${reviewNo}/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ reviewContent: updateContent })
    })
    .then(resp => resp.text())
    .then(result => {
        console.log('후기 수정', result)
        if (result != 1) {
            alert('수정 실패');
            return;
        }

        alert('수정되었습니다');
        loadReviewList(); 
    });
})


// 후기 삭제
document.addEventListener('click', e => {
    if(!e.target.classList.contains('delete-review-btn')) return;

    const reviewBox = e.target.closest('.yes-review')
    const reviewNo = reviewBox?.dataset.reviewNo

    if(!reviewNo){
        console.error('reviewNo 없음')
        return;
    }

    if(!confirm('정말 삭제하겠습니까?')) return;

    fetch(`movies/review/${reviewNo}/delete`, {
        method : "POST"
    })
    .then(resp => resp.text())
    .then(result => {
        console.log("후기삭제", result)

        if(result != 1){
            alert('관람평 삭제 실패')
            return;
        }

        alert('관람평이 삭제되었습니다.')
        loadReviewList();
    })
    .catch(err => console.log(err))
})

// 후기 목록 함수 (fragment)
function loadReviewList(){
    fetch(`/movies/${productNo}/review`)
    .then(resp => resp.text())
    .then(list => {
        // console.log(list)
        document.querySelector('.review-list-area').innerHTML = list;
    })
    .catch(err => console.log(err))
}

//===========================================================================

// 수정 버튼 클릭시 update화면으로
document.getElementById('update-btn')?.addEventListener("click", ()=>{
    location.href = `${location.pathname}/update`;
})

// 삭제 버튼 -> 삭제하기
document.getElementById('delete-btn')?.addEventListener('click', ()=>{
    if (confirm("정말 삭제하시겠습니까?")){
        location.href = `${location.pathname}/delete`;
    }
})

// 좋아요
const likeBtn = document.getElementById('movieLikeBtn')
const movieLike = document.querySelector('#movieHeart')

likeBtn.addEventListener('click', (e) => {

    // console.count("LIKE_CLICK");
    e.preventDefault();

    if(loginMemberNo == "" || loginMemberNo == null){
        alert("로그인 후 이용해주세요.");
        return;
    }

    const isLiked = movieLike.classList.contains('fa-solid');
    const check = isLiked ? 0 : 1; // 0: 삭제(취소), 1: 추가

    const likeData = {
        'memberNo' : loginMemberNo,
        'productNo' : productNo,
        'check' : check
    }

    fetch("/movies/like", {
        method : "POST",
        headers : { "Content-Type" : "application/json" },
        body : JSON.stringify(likeData)
    })
    .then(resp => resp.text())
    .then(count => {
        console.log("likeCount : ", count)

        if(count === -1){
            alert("좋아요 처리 중 문제가 발생했습니다.");
            return;
        }

        movieLike.classList.toggle('fa-regular', check === 0);
        movieLike.classList.toggle('fa-solid', check === 1);

        // 현재 좋아요 수 
        document.getElementById('likeCount').innerText = count;
    })
    .catch(err => console.log(err))
})


// 장바구니
document.querySelector('.add-cart').addEventListener('click', () => {

    // 로그인 체크
    if (loginMemberNo === "") {
        alert("로그인 후 이용해주세요.");
        return;
    }

    const buyCount = document.querySelector('.book-price input[type="number"]');


    const quantity = 1;

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


