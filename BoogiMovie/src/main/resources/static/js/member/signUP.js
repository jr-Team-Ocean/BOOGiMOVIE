console.log('member.js loaded....')

const phoneInput = document.getElementById('phone')
const sendBtn = document.getElementById('sendBtn')
const reSendBtn = document.getElementById('rebtn')
const auth = document.querySelector('.auth-input')
const authText = document.querySelector('.description')
const headerTitle = document.querySelector('.header > div:first-child')
const step1 = document.querySelector('.step1')
const step2 = document.querySelector('.step2')
const stepsOne = document.querySelector('.steps > div:first-child')
const stepsTwo = document.querySelector('.steps > div:last-child')

let authTimer;
let authMin = 4;
let authSec = 59;

// 전화번호 유효성 검사 + 문자인증 발송
let tempPhone;

sendBtn?.addEventListener('click', (e)=>{
    const regEx = /^010\d{8}$/;

    if(phoneInput.value == ""){
        alert('휴대폰 번호를 입력해주세요.')
        phoneInput.focus()
        return
    }

    if(!regEx.test(phoneInput.value)){
        alert('010으로 시작하는 숫자11자리로 입력해주세요.')
        phoneInput.value = "";
        phoneInput.focus();
        return
    }

    if(regEx.test(phoneInput.value)){
        authMin = 2;
        authSec = 59;
        
        auth.classList.remove('hidden')
        authText.classList.remove('hidden')
        sendBtn.innerText = '확인';
        checkObj.memberTel = true;
        e.preventDefault()

        // fetch()
        // .then(resp => resp.text())
        // .then(result => {
        //     if(result > 0){
        //         console.log('인증번호가 발송되었습니다.');
        //         tempPhone = phoneInput.value

        //     }else{
        //         console.log('인증번호 발송 실패!!')
        //     }
        // })
        // .catch(error => {
        //     console.log('휴대폰 번호 발송 중 에러 발생');
        //     console.log(error);
        // })
    }

    // step2 페이지로 넘어가기
    e.target?.addEventListener('click', ()=>{
        headerTitle.innerText = '정보입력'
        stepsOne.classList.remove('select')
        stepsTwo.classList.add('select')
        step1.classList.add('hidden')
        step2.classList.remove('hidden')
    })
})

const checkObj = {
    'memberId' : false,
    'memberPw' : false,
    'memberPwCheck' : false,
    'memberName' : false,
    'memberNickname' : false,
    'memberEmail' : false,
}

// 아이디 유효성 검사
const memberId = document.getElementById('ID')
const idMessage = document.getElementById('id-message')

memberId?.addEventListener('input', ()=>{
    if(memberId.value == ''){
        idMessage.innerText = "4 ~ 12자리 영소문자, 숫자를 입력해주세요.";
        idMessage.classList.remove('confirm' ,'error');
        checkObj.memberId = false;
        return
    }

    const idRegEx = /^[a-z0-9]{4,12}$/
    if(idRegEx.test(memberId.value)){
        fetch(`/dupCheck/id?id=${encodeURIComponent(memberId.value)}`)
        .then(resp => resp.json())
        .then(result => {

            if(result.duplicated){
                idMessage.innerText = '이미 사용중인 아이디입니다.';
                idMessage.classList.remove('confirm');
                idMessage.classList.add('error');
                checkObj.memberId = false;
            
            }else {
                idMessage.innerText = '사용 가능한 아이디입니다.';
                idMessage.classList.remove('error');
                idMessage.classList.add('confirm');
                checkObj.memberId = true;
            }
        })
        .catch(err => console.log(err))
    
    }else{
        idMessage.innerText = "사용 불가능한 아이디 입니다.";
        idMessage.classList.remove("confirm");
        idMessage.classList.add("error");
        checkObj.memberId = false;
    }
})

// 비밀번호 유효성 검사
const memberPw = document.getElementById('password')
const memberPwConfirm = document.getElementById('passwordCheck')
const pwMessage = document.getElementById('pw-message')
const pwConfirmMessage = document.getElementById('pw-check-message')

memberPw?.addEventListener('input', ()=>{
    if(memberPw.value == ''){
        pwMessage.innerText = '8~16자리 영어, 숫자, 특수문자(!@#$%^&*-)를 입력해주세요.';
        pwMessage.classList.remove('confirm', 'error');
        checkObj.memberPw = false;
        return;
    }

    const pwRegEx = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*\-])[A-Za-z\d!@#$%^&*\-]{8,16}$/;
    if(pwRegEx.test(memberPw.value)){
        checkObj.memberPw = true;

        if(memberPwConfirm.value == ''){
            pwMessage.innerText = '사용 가능한 비밀번호 입니다.';
            pwMessage.classList.remove('error');
            pwMessage.classList.add('confirm');
        
        }else{
            checkPw();
        }
    
    }else{
        pwMessage.innerText = "사용 불가능한 비밀번호 입니다.";
        pwMessage.classList.remove("confirm");
        pwMessage.classList.add("error");
        checkObj.memberPw = false;
    }
})

// 비밀번호 확인 - 비밀번호와 일치하는지
memberPwConfirm?.addEventListener('input', ()=>{
    if(memberPw.value == '' && memberPwConfirm.value == ''){
        pwConfirmMessage.innerText = '비밀번호를 입력해주세요.';
        memberPw.focus()
        memberPwConfirm.value = '';
        return;
    }

    // 유효한 경우
    if(checkObj.memberPw){
        checkPw()
    }else{
        checkObj.memberPwConfirm = false;
    }
})

function checkPw(){
    // 비밀번호 == 비밀번호 확인
    if(memberPw.value == memberPwConfirm.value){
        pwConfirmMessage.innerText = '비밀번호가 일치합니다.';
        pwConfirmMessage.classList.add('confirm');
        pwConfirmMessage.classList.remove('error');
        checkObj.memberPwConfirm = true;

    }else{
        pwConfirmMessage.innerText = '비밀번호가 일치하지 않습니다.';
        pwConfirmMessage.classList.add('error');
        pwConfirmMessage.classList.remove('confirm');
        checkObj.memberPwConfirm = false;
    }
}

// 이름 유효성 검사
const memberName = document.getElementById('realName')
const nameMessage = document.getElementById('name-message')

memberName?.addEventListener('input', ()=>{
    if(memberName.value == ''){
        nameMessage.innerText = '이름을 입력해주세요.';
        nameMessage.classList.remove('confirm', 'error');
        checkObj.memberName = false;
        return;
    }

    const nameRegEx = /^[가-힣]{2,5}$/;
    if(nameRegEx.test(memberName.value)){
        checkObj.memberName = true;
        nameMessage.innerText = '유효한 이름입니다.';
        nameMessage.classList.add('confirm');
        nameMessage.classList.remove('error');
        
    }else{
        nameMessage.innerText = '2~5자 사이에 한글만 입력해주세요.';
        nameMessage.classList.add('error');
        nameMessage.classList.remove('confirm');
        checkObj.memberName = false;
    }
})

// 닉네임 유효성 검사
const nickName = document.getElementById('nickName')
const nickNameMessage = document.getElementById('nickName-message')

nickName?.addEventListener('input', ()=>{
    if(nickName.value == ''){
        nickNameMessage.innerText = '한글,영어,숫자로만 2~8글자로 입력해주세요.';
        nickNameMessage.classList.remove('error','confirm');
        checkObj.memberNickname = false;
        return;
    }

    // 닉네임 중복검사
    const nickRegEx = /^[가-힣a-zA-z0-9]{2,8}$/;
    if(nickRegEx.test(nickName.value)){
        fetch(`/dupCheck/nickname?nickname=${encodeURIComponent(nickName.value)}`)
        .then(resp => resp.json())
        .then(result => {

            if(result.duplicated){
                nickNameMessage.innerText = '이미 사용중인 닉네임입니다.';
                nickNameMessage.classList.remove('confirm');
                nickNameMessage.classList.add('error');
                checkObj.memberId = false;
            
            }else {
                nickNameMessage.innerText = '사용 가능한 닉네임입니다.';
                nickNameMessage.classList.remove('error');
                nickNameMessage.classList.add('confirm');
                checkObj.memberId = true;
            }
        })
        .catch(err => console.log(err))
    
    }else{
        nickNameMessage.innerText = '사용 불가능한 닉네임 입니다.';
        nickNameMessage.classList.remove("confirm");
        nickNameMessage.classList.add("error");
        checkObj.memberNickname = false;
    }
})

// 이메일 유효성 검사
const email = document.getElementById('email')
const emailMessage = document.getElementById('email-message')

email?.addEventListener('input', ()=>{
    if(email.value == ''){
        emailMessage.innerText = '이메일을 입력해주세요.';
        emailMessage.classList.remove('confirm', 'error');
        checkObj.memberEmail = false;
        return;
    }

    const emailRegEx = /^[\w-]{4,}@[a-z]+(\.[a-z]+){1,2}$/;
    if(emailRegEx.test(email.value)){
        checkObj.memberEmail = true;
        emailMessage.innerText = '유효한 이메일 입니다.';
        emailMessage.classList.add('confirm');
        emailMessage.classList.remove('error');
    
    }else{
        emailMessage.innerText = '유효한 이메일을 입력해주세요.';
        emailMessage.classList.add('error');
        emailMessage.classList.remove('confirm');
        checkObj.memberEmail = false;
    }
})