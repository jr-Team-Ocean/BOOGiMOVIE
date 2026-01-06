console.log("change loaded")

// 닉네임 중복 검사
async function dupNicknameCheck(nickname) {
    const resp = await fetch(
        `/dupCheck/nickname?nickname=${encodeURIComponent(nickname)}`
    );
    const result = await resp.text();
    return result === 'true';
}


// // 비밀번호 유효성
// function validatePassword(pw) {
//     const regEx = /^[\w!#@\-]{8,16}$/;
//     return regEx.test(pw);
// }

/* ---------------------------------------------------------------------- */
async function handleSubmit(infoBox) {

    const field = infoBox.dataset.field;
    const input = infoBox.querySelector('.edit-input');

    const originValue = infoBox.dataset.oldValue;
    const inputValue = input.value.trim();

    // 변경값 없음
    if (originValue === inputValue) {
        location.reload();
        return;
    }

    // 빈 값
    if (inputValue === '') {
        alert('값을 입력해주세요.');
        input.focus();
        return;
    }


    // 이름 변경
    if (field === 'name') {

        const nameRegEx = /^[가-힣]{2,5}$/;
        if (!nameRegEx.test(inputValue)) {
            alert('이름은 2~5자 한글만 입력 가능합니다.');
            input.focus();
            return;
        }

        if (!confirm('이름을 변경하시겠습니까?')) {
            location.reload();
            return;
        }

        updateMember('/myPage/change/name', inputValue);
        return;
    }

    
    // 이메일 변경
    if (field === 'email') {

        const emailRegEx = /^[\w-]{4,}@[a-z]+(\.[a-z]+){1,2}$/;
        if (!emailRegEx.test(inputValue)) {
            alert('유효한 이메일 형식이 아닙니다.');
            input.focus();
            return;
        }

        if (!confirm('이메일을 변경하시겠습니까?')) {
            location.reload();
            return;
        }

        updateMember('/myPage/change/email', inputValue);
        return;
    }

    
    // 닉네임 변경
    if (field === 'nickname') {

        const nickRegEx = /^[가-힣a-zA-Z0-9]{2,8}$/;
        if (!nickRegEx.test(inputValue)) {
            alert('닉네임은 2~8자 한글/영문/숫자만 가능합니다.');
            input.focus();
            return;
        }

        const isDup = await dupNicknameCheck(inputValue);
        if (isDup) {
            alert('이미 사용중인 닉네임입니다.');
            input.focus();
            return;
        }

        if (!confirm('닉네임을 변경하시겠습니까?')) {
            location.reload();
            return;
        }

        updateMember('/myPage/change/nickname', inputValue);
        return;
    }
}


// 변경 호출
function updateMember(url, value) {
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ value })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert('수정에 실패했습니다.');
            return;
        }

        alert('변경이 완료되었습니다.');
        location.reload();
    })
    .catch(err => {
        console.error(err);
        alert('오류가 발생했습니다.');
    });
}


/* ---------------------------------------------------------------------- */


// 버튼 기능 복구용
document.querySelector('.info-text-area').addEventListener('click', (e) => {

    // closest : 자기 자신부터 거꾸로 올라가면서 조건에 맞는 요소 탐색
    const changeBtn = e.target.closest('.change-btn');
    if (changeBtn) {

        const infoBox = changeBtn.closest('.info-box');
        let field = null;   // 항목
        let label = '';     // 컬럼 이름
        let value = '';     // 값

        if (changeBtn.classList.contains('name')) {
            field = 'name';
            label = '이름';
            value = infoBox.querySelector('.user-name').innerText.trim();

        } else if (changeBtn.classList.contains('email')) {
            field = 'email';
            label = '이메일';
            value = infoBox.querySelector('.user-email').innerText.trim();

        } else if (changeBtn.classList.contains('nickname')) {
            field = 'nickname';
            label = '닉네임';
            value = infoBox.querySelector('.user-nickname').innerText.trim();
        }

        if (!field) return;

        // 상태 저장
        infoBox.dataset.field = field;
        infoBox.dataset.oldValue = value;

        // 수정 구역으로 전환
        infoBox.innerHTML = `
            <div class="edit-box">
                <b class="t-name">${label}</b>
                <input type="text" class="edit-input" value="${value}">
                <button class="change-submit">완료</button>
            </div>
        `;

        const input = infoBox.querySelector('.edit-input');
        input.focus();
        input.setSelectionRange(input.value.length, input.value.length);
        return;
    }

    // 완료버튼
    const submitBtn = e.target.closest('.change-submit');
    if (submitBtn) {
        const infoBox = submitBtn.closest('.info-box');
        handleSubmit(infoBox);
    }
});


/* ---------------------------------------------------------------------- */

// 비밀번호 변경 화면 열기
document.querySelector('.info-text-area').addEventListener('click', (e) => {

    const pwBtn = e.target.closest('.change-btn.pw');
    if (!pwBtn) return;

    const pwBox = document.querySelector('.change-pw-box');
    pwBox.classList.toggle('open');

    // 열릴 때 input 초기화
    if (pwBox.classList.contains('open')) {
        pwBox.querySelector('.edit-pw').value = '';
        pwBox.querySelector('.edit-pw-confirm').value = '';
        pwBox.querySelector('.edit-pw').focus();
    }
});


document.querySelector('.change-pw-box').addEventListener('click', async (e) => {

    const submitBtn = e.target.closest('.change-pw-submit');
    if (!submitBtn) return;

    const pwBox = submitBtn.closest('.change-pw-box');
    const pwInput = pwBox.querySelector('.edit-pw');
    const pwConfirmInput = pwBox.querySelector('.edit-pw-confirm');

    const pw = pwInput.value.trim();
    const pwConfirm = pwConfirmInput.value.trim();

    // 빈값
    if (pw === '') {
        alert('비밀번호를 입력해주세요.');
        pwInput.focus();
        return;
    }

    // 유효성검사
    const pwRegEx = /^[\w!#@\-]{8,16}$/;
    if (!pwRegEx.test(pw)) {
        alert('8~16자리 영문, 숫자, 특수문자(!@#-)만 가능합니다.');
        pwInput.focus();
        return;
    }

    // 확인 빈값
    if (pwConfirm === '') {
        alert('비밀번호 확인을 입력해주세요.');
        pwConfirmInput.focus();
        return;
    }

    // 비밀번호 불일치
    if (pw !== pwConfirm) {
        alert('비밀번호가 일치하지 않습니다.');
        pwConfirmInput.focus();
        return;
    }

    // 알림
    if (!confirm('비밀번호를 변경하시겠습니까?')) {
        pwBox.classList.remove('open');
        return;
    }

    // 요청
    fetch('/myPage/changePw', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ value: pw })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert('비밀번호 변경에 실패했습니다.');
            return;
        }
        alert('비밀번호가 변경되었습니다.');
        location.reload();
    })
    .catch(err => {
        console.error(err);
        alert('오류가 발생했습니다.');
    });
});


/* ---------------------------------------------------------------------- */

const imageInput = document.getElementById("profile-img");
const imagePreview = document.getElementById("change-img");


// 프로필 사진 선택
imagePreview.addEventListener('click', () => {
    imageInput.click();
});


// 이미지 미리보기
imageInput.addEventListener("change", function (e) {

    const file = e.target.files[0];

    // 파일 선택
    if (file != undefined) {

        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = function (e) {
            imagePreview.setAttribute("src", e.target.result);
        };
    }

    // 취소
    else {
        const defaultImg = imagePreview.dataset.defaultSrc;
        imagePreview.setAttribute("src", defaultImg);
        imageInput.value = "";
    }
});

// 사진 업로드
document.querySelector('.img-change-btn').addEventListener('click', () => {

    const file = imageInput.files[0];
    if (!file) {
        alert('선택된 파일이 없습니다.');
        return;
    }

    if (!confirm('프로필 사진을 변경하시겠습니까?')) {
        return;
    }

    const formData = new FormData();
    formData.append("profileImage", file);

    fetch("/myPage/changeProfileImg", {
        method: "POST",
        body: formData
    })
    .then(resp => resp.text())
    .then(result => {
        if (result != 1) {
            alert("프로필 이미지 변경에 실패했습니다.");
            return;
        }

        alert('프로필 이미지 변경이 완료되었습니다.');
        location.reload();
    })
    .catch(err => {
        console.error(err);
        alert("오류 발생");
    });
});
