console.log('member.js loaded....')

const phoneInput = document.getElementById('phone')
const step2Phone = document.getElementById('memberPhone')
const sendBtn = document.getElementById('sendBtn')
const okSendBtn = document.getElementById('okBtn') // 인증번호 확인 버튼
const reSendBtn = document.getElementById('reBtn') // 재전송 버튼

const authArea = document.querySelector('.auth-area')

const headerTitle = document.querySelector('.header > div:first-child')
const step1 = document.querySelector('.step1')
const step2 = document.querySelector('.step2')
const stepsOne = document.querySelector('.steps > div:first-child')
const stepsTwo = document.querySelector('.steps > div:last-child')

let authTimer;
let authMin = 2;
let authSec = 59;

const checkObj = {
    'memberId' : false,
    'memberPw' : false,
    'memberPwCheck' : false,
    'memberName' : false,
    'memberNickName' : false,
    'memberEmail' : false,
    'memberTel' : false,
    'memberBirth' : false,
    'memberAddress' : false,
    'authKey' : false
}

// step2 페이지로 넘어가기
function goStep2(number){
    headerTitle.innerText = '정보입력'
    stepsOne.classList.remove('select')
    stepsTwo.classList.add('select')
    step1.classList.add('hidden')
    step2.classList.remove('hidden')
    step2Phone.value = number;
}


// 전화번호 유효성 검사 + 문자인증 발송
let tempPhone;

sendBtn?.addEventListener('click', async (e)=>{
    const state = sendBtn.dataset.state
    const regEx = /^010\d{8}$/;

    if(state == 'next'){
        if(!checkObj.authKey){
            alert('인증번호 확인을 먼저 해주세요');
            return;
        }
        goStep2(phoneInput.value.trim())
    }

    if(phoneInput.value.trim() == ""){
        alert('휴대폰 번호를 입력해주세요.')
        phoneInput.focus()
        return
    }

    if(!regEx.test(phoneInput.value.trim())){
        alert('010으로 시작하는 숫자11자리로 입력해주세요.')
        phoneInput.value = "";
        phoneInput.focus();
        return
    }

    try{
        // 휴대폰 중복검사 여부
        const resp = await fetch(`/dupCheck/phone?phone=${encodeURIComponent(phoneInput.value)}`)
        
        if(!resp.ok){
            console.log('HTTP status:', resp.status);
            const t = await resp.text();
            console.log('response text:', t);
            alert('서버 요청에 실패했습니다.');
            return;
        }

        const data = await resp.text(); 
        
        if(data == 'true'){
            alert('이미 사용중인 번호입니다.')
            checkObj.memberTel = false;
            return
        }
        
        if(state == 'send'){
            alert('인증번호를 발송합니다. 휴대폰을 확인해주세요.');
            checkObj.memberTel = true;
            checkObj.authKey = false
            authArea.classList.remove('hidden')
            sendBtn.dataset.state = 'next'
            sendBtn.innerText = '다음';
        }
        
    }catch(error){
        console.log('휴대폰 번호 처리 중 에러', error);
        alert('요청 중 오류가 발생했습니다.');
    }
})

okSendBtn?.addEventListener("click", () => {
    // TODO: 여기서 실제 인증번호 검증(fetch) 넣어야 함
    // 일단 성공했다고 가정
    checkObj.authKey = true;
    alert("인증이 완료되었습니다.");
})


// 아이디 유효성 검사
const memberId = document.getElementById('memberId')
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
        .then(resp => resp.text())
        .then(result => {

            if(result == 'true'){
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
const memberPw = document.getElementById('memberPw')
const memberPwConfirm = document.getElementById('memberPwCheck')
const pwMessage = document.getElementById('pw-message')
const pwConfirmMessage = document.getElementById('pw-check-message')

memberPw?.addEventListener('input', ()=>{
    if(memberPw.value == ''){
        pwMessage.innerText = '8~16자리 영어, 숫자, 특수문자(!@#$%^&*-)를 입력해주세요.';
        pwMessage.classList.remove('confirm', 'error');
        checkObj.memberPw = false;
        return;
    }

    const pwRegEx = /^[\w!#@\-]{8,16}$/;
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
    if(memberPw.value.trim() == ''){
        pwConfirmMessage.innerText = '비밀번호를 입력해주세요.';
        memberPw.focus()
        memberPwConfirm.value = '';
        return;
    }

    // 유효한 경우
    if(checkObj.memberPw){
        checkPw()

    }else{
        checkObj.memberPwCheck = false;
    }
})

function checkPw(){
    // 비밀번호 == 비밀번호 확인
    if(memberPw.value == memberPwConfirm.value){
        pwConfirmMessage.innerText = '비밀번호가 일치합니다.';
        pwConfirmMessage.classList.add('confirm');
        pwConfirmMessage.classList.remove('error');
        checkObj.memberPwCheck = true;

    }else{
        pwConfirmMessage.innerText = '비밀번호가 일치하지 않습니다.';
        pwConfirmMessage.classList.add('error');
        pwConfirmMessage.classList.remove('confirm');
        checkObj.memberPwCheck = false;
    }
}

// 이름 유효성 검사
const memberName = document.getElementById('memberName')
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

// 생년월일 유효성 검사
const birth = document.getElementById('memberBirth')
const birthMessage = document.getElementById('birth-message')

birth?.addEventListener('input', ()=>{
    if(birth.value == ''){
        birthMessage.innerText = '생년월일 8자를 입력해주세요';
        birthMessage.classList.remove('error','confirm');
        checkObj.memberBirth = false;
        return;
    }

    const birthRegEx = /^[0-9]{8}$/;
    if(birthRegEx.test(birth.value)){
        checkObj.memberBirth = true;
        birthMessage.innerText = '👌'
    
    }else{
        checkObj.memberBirth = false;
        birthMessage.innerText = '숫자만 입력해주세요.'
        birthMessage.classList.add('error')
    }
})

// 닉네임 유효성 검사
const nickName = document.getElementById('memberNickName')
const nickNameMessage = document.getElementById('nickName-message')

nickName?.addEventListener('input', ()=>{
    if(nickName.value == ''){
        nickNameMessage.innerText = '한글,영어,숫자로만 2~8글자로 입력해주세요.';
        nickNameMessage.classList.remove('error','confirm');
        checkObj.memberNickName = false;
        return;
    }

    // 닉네임 중복검사
    const nickRegEx = /^[가-힣a-zA-z0-9]{2,8}$/;
    if(nickRegEx.test(nickName.value)){
        fetch(`/dupCheck/nickname?nickname=${encodeURIComponent(nickName.value)}`)
        .then(resp => resp.text())
        .then(result => {

            if(result == 'true'){
                nickNameMessage.innerText = '이미 사용중인 닉네임입니다.';
                nickNameMessage.classList.remove('confirm');
                nickNameMessage.classList.add('error');
                checkObj.memberNickName = false;
            
            }else {
                nickNameMessage.innerText = '사용 가능한 닉네임입니다.';
                nickNameMessage.classList.remove('error');
                nickNameMessage.classList.add('confirm');
                checkObj.memberNickName = true;
            }
        })
        .catch(err => console.log(err))
    
    }else{
        nickNameMessage.innerText = '사용 불가능한 닉네임 입니다.';
        nickNameMessage.classList.remove("confirm");
        nickNameMessage.classList.add("error");
        checkObj.memberNickName = false;
    }
})

// 이메일 유효성 검사
const email = document.getElementById('memberEmail')
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

// 주소 입력했는지 확인
const addr1 = document.getElementById('sample6_postcode')
const addr2 = document.getElementById('sample6_address')

function validateAddress() {
    if (addr1.value.trim() == "" && addr2.value.trim() == "") {
        checkObj.memberAddress = false;
    } else {
    checkObj.memberAddress = true;
    }
}

// key -> focus 대상(selector)
const focusTarget = {
    memberId: "#memberId",
    memberPw: "#memberPw",
    memberPwCheck: "#memberPwCheck",
    memberName: "#memberName",
    memberNickName: "#memberNickName",      
    memberEmail: "#memberEmail",
    memberTel: "#memberPhone",              // key: memberTel / input id: memberPhone
    memberBirth: "#memberBirth",
    memberAddress: "#sample6_postcode",     // 주소 대표로 우편번호
    authKey: "#authKey"                     // step1 인증번호 입력칸
};

// key -> 안내 메시지
const errorMessage = {
    memberId: "아이디가 유효하지 않습니다.",
    memberPw: "비밀번호가 유효하지 않습니다.",
    memberPwCheck: "비밀번호 확인이 유효하지 않습니다.",
    memberName: "이름이 유효하지 않습니다.",
    memberNickName: "닉네임이 유효하지 않습니다.",
    memberEmail: "이메일이 유효하지 않습니다.",
    memberTel: "핸드폰 번호가 유효하지 않습니다.",
    memberBirth: "생년월일이 유효하지 않습니다.",
    memberAddress: "주소를 입력해주세요.",
    authKey: "인증번호가 유효하지 않습니다."
};

// step 이동 함수: authKey가 false면 step1로, 나머지는 step2에 있으니 step2로
function ensureStepVisible(key) {
    const step1 = document.querySelector(".step1");
    const step2 = document.querySelector(".step2");

    if (!step1 || !step2) return;

    // authKey는 step1에 있음
    if (key === "authKey") {
        step2.classList.add("hidden");
        step1.classList.remove("hidden");
        return;
    }

    // 나머지는 step2에 있음(주소/회원정보 등)
    step1.classList.add("hidden");
    step2.classList.remove("hidden");
}

document.getElementById("signUpFrm").addEventListener("submit", (e) => {
    validateAddress();
    e.preventDefault();


    for (const key in checkObj) {
        if (!checkObj[key]) {
        alert(errorMessage[key] ?? "입력값이 유효하지 않습니다.");

        // 필요한 step으로 이동(안 보이는 input에 focus 방지)
        ensureStepVisible(key);

        const selector = focusTarget[key];
        const el = selector ? document.querySelector(selector) : null;

        if (el) el.focus();
        else console.error("포커스 대상 없음:", key, selector);

        return;
        }
    }

    // 모두 통과
    e.target.submit();
});
