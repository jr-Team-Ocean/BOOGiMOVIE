console.log('member.js loaded....')

const phoneInput = document.getElementById('phone')
const setp2Phone = document.getElementById('phoneNumber')
const sendBtn = document.getElementById('sendBtn')
const okSendBtn = document.getElementById('okBtn')
const reSendBtn = document.getElementById('reBtn')
const auth = document.querySelector('.auth-input')
const authText = document.querySelector('.description')
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
    'memberNickname' : false,
    'memberEmail' : false,
    'memberTel' : false,
    'memberBirth' : false,
    'memberAddress' : false,
    'authKey' : true
}

// step2 페이지로 넘어가기
function goStep2(number){
    headerTitle.innerText = '정보입력'
    stepsOne.classList.remove('select')
    stepsTwo.classList.add('select')
    step1.classList.add('hidden')
    step2.classList.remove('hidden')
    setp2Phone.value = number;
}


// 전화번호 유효성 검사 + 문자인증 발송
let tempPhone;

sendBtn?.addEventListener('click', async (e)=>{
    const regEx = /^010\d{8}$/;

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
        const resp = await fetch(`/dupCheck/phone?phone=${encodeURIComponent(phoneInput.value)}`)
        if(!resp.ok){
            console.log('HTTP status:', resp.status);
            const t = await resp.text();
            console.log('response text:', t);
            alert('서버 요청에 실패했습니다.');
            return;
        }

        // 휴대폰 중복검사 여부
        const data = await resp.json(); 

        if(data.duplicated){
            alert('이미 사용중인 번호입니다.')
            checkObj.memberTel = false;
            return
        }

        // 문자인증 api 연결하기!!
        alert('인증번호를 발송합니다. 휴대폰을 확인해주세요.');
        checkObj.memberTel = true;

        if(regEx.test(phoneInput.value)){
            auth.classList.remove('hidden')
            authText.classList.remove('hidden')
            sendBtn.innerText = '확인';
            checkObj.memberTel = true;
            e.preventDefault()
        }

        okSendBtn.addEventListener('click', ()=>{
            goStep2(phoneInput.value.trim())
        })


    }catch(error){
        console.log('휴대폰 번호 처리 중 에러', error);
        alert('요청 중 오류가 발생했습니다.');
    }

    
})


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

memberPw.addEventListener('input', ()=>{
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
memberPwConfirm.addEventListener('input', ()=>{
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
                checkObj.memberNickname = false;
            
            }else {
                nickNameMessage.innerText = '사용 가능한 닉네임입니다.';
                nickNameMessage.classList.remove('error');
                nickNameMessage.classList.add('confirm');
                checkObj.memberNickname = true;
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

// 주소 구분자(^^^)로 합치기

const addr = document.getElementsByName('address')
// form 제출시
document.getElementById('signUpFrm').addEventListener('submit', e=>{
    e.preventDefault();

    console.log(addr.value)[0]
    console.log(addr.value)[1]
    console.log(addr.value)[2]

    for(let key in checkObj){
        
        if(!checkObj[key]){
            switch(key){
                case 'memberEmail' : alert('이메일이 유효하지 않습니다.'); break;
                case 'memberPw' : alert('비밀번호가 유효하지 않습니다.'); break;
                case 'memberPwCheck'  : alert('비밀번호 확인이 유효하지 않습니다.'); break;
                case 'memberName' : alert('이름이 유효하지 않습니다.'); break;
                case 'memberNickname' : alert('닉네임이 유효하지 않습니다.'); break;
                case 'memberEmail' : alert('전화번호가 유효하지 않습니다.'); break;
                case 'memberTel' : alert('핸드폰 번호가 유효하지 않습니다.'); break;
                case 'authKey' : alert('인증번호가 유효하지 않습니다.'); break;
            }

            document.getElementById(key).focus();
            return;
        }
    }

})