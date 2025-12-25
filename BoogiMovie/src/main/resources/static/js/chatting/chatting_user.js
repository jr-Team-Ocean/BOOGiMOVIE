console.log('chatting_user.js')

// 페이지네이션 관련 변수
let currentPage = 1;
let totalPages = 1;

document.addEventListener('DOMContentLoaded', () => {

    // 버튼 이벤트용 변수 
    const sendBtn = document.getElementById('sendBtn');
    const chattingInput = document.getElementById('chattingInput');

    // 전송 버튼 클릭 시에도 전송
    sendBtn.addEventListener('click', sendMessage);

    // 페이지네이션 이벤트 (상단)
    const headerNavPages = document.querySelectorAll('.header_nav_page a');
    headerNavPages.forEach(link => {
        link.addEventListener('click', handlePagination);
    });

    // 엔터 입력 시 전송 로직
    chattingInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            if (e.isComposing) return;
            if (!e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        }
    });

    // 파일 첨부 버튼
    const attachButton = document.querySelector('.attach_button');
    attachButton.addEventListener('click', triggerFileInput);

    // 초기 페이지 상태 표시
    updatePageStatus();
    
    // 초기 메시지 목록 로드
    selectMessageList();

    return;
})

// 전역변수 선언 
let adminNo;
let adminName = '관리자';
let adminProfile = '/resources/images/user.png';


// ========== 페이지네이션 ==========

/**
 * 페이지네이션 처리
 */
function handlePagination(e) {
    e.preventDefault();
    
    const text = e.target.textContent;
    
    if(text === '«') {
        currentPage = 1;
    } else if(text === '<') {
        if(currentPage > 1) currentPage--;
    } else if(text === '>') {
        if(currentPage < totalPages) currentPage++;
    } else if(text === '»') {
        currentPage = totalPages;
    }
    
    // 페이지 상태 업데이트 및 목록 재조회
    updatePageStatus();
    selectMessageList();
}

/**
 * 페이지 상태 업데이트 함수
 */
function updatePageStatus() {
    const pageStatus = document.querySelector('.page_status');
    if(pageStatus) {
        pageStatus.innerText = ` ${currentPage} / ${totalPages}`;
    }
}


// ========== 파일 첨부 기능 ==========

/**
 * 파일 첨부 버튼 클릭 - 파일 선택 다이얼로그 열기
 */
function triggerFileInput() {
    let fileInput = document.getElementById('hiddenFileInput');
    
    // 파일 입력 요소가 없으면 동적으로 생성
    if(!fileInput) {
        fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.id = 'hiddenFileInput';
        fileInput.accept = 'image/*';
        fileInput.style.display = 'none';
        fileInput.addEventListener('change', handleFileSelect);
        document.body.appendChild(fileInput);
    }
    
    fileInput.click();
}

/**
 * 파일 선택 처리
 */
function handleFileSelect(e) {
    const file = e.target.files[0];
    
    if(!file) return;
    
    // 이미지 파일인지 확인
    if(!file.type.startsWith('image/')) {
        alert('이미지 파일만 선택 가능합니다.');
        return;
    }
    
    // 파일 크기 확인 (10MB 이상이면 거절)
    if(file.size > 10 * 1024 * 1024) {
        alert('파일 크기는 10MB 이하여야 합니다.');
        return;
    }
    
    // 파일을 읽어서 전송
    const reader = new FileReader();
    reader.onload = (event) => {
        sendFileMessage(file.name, event.target.result);
    };
    reader.readAsDataURL(file);
    
    // 파일 입력 초기화
    e.target.value = '';
}

/**
 * 파일 메시지 전송
 */
function sendFileMessage(fileName, fileData) {
    const obj = {
        senderNo: loginMemberNo,
        messageContent: '[이미지: ' + fileName + ']',
        fileData: fileData,
        isFile: true
    };
    
    chattingSock.send(JSON.stringify(obj));
}


// ========== 메세지 관리 ==========

/**
 * 비동기로 메세지 목록을 조회하는 함수
 */
function selectMessageList() {
    if(!loginMemberNo) {
        console.log('로그인 정보가 없습니다.');
        return;
    }
    
    // 페이지 파라미터만 추가 (필터 없음)
    let url = "/chatting/userMessageList?memberNo=" + loginMemberNo + "&page=" + currentPage;
    
    fetch(url)
        .then(resp => resp.json())
        .then(data => {
            // totalPages 업데이트
            if(data.totalPages) {
                totalPages = data.totalPages;
            }
            
            const messageList = data.messageList || data;
            const ul = document.querySelector('.display-chatting')  
            ul.innerHTML = '';

            for (let msg of messageList) {
                const li = document.createElement('li')

                // 보낸 시간 
                const span = document.createElement('span') 
                span.classList.add('chatDate')
                span.innerText = msg.sendTime

                // 메시지 내용
                const p = document.createElement('p')
                p.classList.add('chat')
                p.innerText = msg.messageContent;

                // 누구 발송인지 확인
                if (loginMemberNo == msg.senderNo) {
                    li.classList.add('my-chat')
                    li.appendChild(span);
                    li.appendChild(p);
                } else {
                    li.classList.add('target-chat')

                    // 관리자 프로필 
                    const img = document.createElement('img')
                    img.setAttribute('src', adminProfile)

                    const div = document.createElement('div')

                    // 관리자 이름 
                    const b = document.createElement('b')
                    b.innerText = adminName
                    
                    // 메시지와 시간을 감싸는 컨테이너
                    const msgContainer = document.createElement('div');
                    msgContainer.style.display = 'flex';
                    msgContainer.style.gap = '8px';
                    msgContainer.style.alignItems = 'flex-end';
                    
                    msgContainer.appendChild(p);
                    msgContainer.appendChild(span);

                    div.appendChild(b)
                    div.appendChild(msgContainer)
                    li.appendChild(img)
                    li.appendChild(div)
                }
                
                ul.appendChild(li);
            }
            
            // 페이지 상태 최종 업데이트
            updatePageStatus();
            
            // 읽음 처리
            if(messageList.length > 0) {
                setTimeout(() => {
                    updateReadFlag();
                }, 200)
            }
            
            // 스크롤 맨 아래로 이동
            scrollToBottom();
        })
        .catch(err => console.log(err))
}

/**
 * 읽음 상태 업데이트
 */
function updateReadFlag() {
    if(!loginMemberNo) return;
    
    fetch('/chatting/updateUserReadFlag', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            'memberNo': loginMemberNo
        })
    })
        .then(resp => resp.text())
        .then(result => {
            console.log('읽음 처리:', result)
        })
        .catch(err => console.log(err))
}

/**
 * 스크롤을 메시지 맨 아래로 이동
 */
function scrollToBottom() {
    const ul = document.querySelector('.display-chatting');
    if(ul) {
        requestAnimationFrame(() => {
            ul.scrollTop = ul.scrollHeight;
        });
    }
}


// ========== WebSocket 설정 ==========

let chattingSock;

if (typeof loginMemberNo !== 'undefined' && loginMemberNo !== '') {
    chattingSock = new SockJS("/chattingSock")
}


// ========== 메시지 전송 ==========

/**
 * 메시지 전송 함수
 */
const sendMessage = () => {
    const chattingInput = document.getElementById('chattingInput');
    
    if(chattingInput.value.trim().length == 0) {
        alert('채팅을 입력해주세요')
        return;
    }

    if(chattingInput.value.trim().length > 0) {
        const messageContent = chattingInput.value;
        
        const obj = {
            senderNo: loginMemberNo,
            messageContent: messageContent
        }

        chattingSock.send(JSON.stringify(obj));

        // 입력창 초기화 및 포커스
        chattingInput.value = '';
        chattingInput.focus();
    }
}


// ========== WebSocket 메시지 수신 ==========

/**
 * 서버에서 메세지 전달 받으면 자동실행 콜백 함수 (채팅창에 표시)
 */
chattingSock.onmessage = e => {

    const msg = JSON.parse(e.data)

    // 큰틀 
    const ul = document.querySelector('.display-chatting')

    // 개별 메시지 틀 
    const li = document.createElement('li');

    // 개별 메시지 내용 
    const p = document.createElement('p')
    p.innerHTML = msg.messageContent
    p.classList.add('chat')

    // 보낸 시간 
    const span = document.createElement('span')
    span.innerText = msg.sendTime;
    span.classList.add('chatDate');
    
    // 내가 작성한 메시지의 경우 
    if (loginMemberNo == msg.senderNo) {
        li.classList.add('my-chat')
        li.appendChild(span);
        li.appendChild(p);
    } else {
        li.classList.add('target-chat')

        // 관리자 프로필
        const img = document.createElement('img');
        img.setAttribute('src', adminProfile)

        const div = document.createElement('div')

        // 관리자 이름 
        const b = document.createElement('b')
        b.innerText = adminName;

        // 메시지와 시간을 감싸는 컨테이너
        const msgContainer = document.createElement('div');
        msgContainer.style.display = 'flex';
        msgContainer.style.gap = '8px';
        msgContainer.style.alignItems = 'flex-end';
        
        msgContainer.appendChild(p);
        msgContainer.appendChild(span);

        div.appendChild(b)
        div.appendChild(msgContainer)
        li.appendChild(img)
        li.appendChild(div)
    }

    ul.appendChild(li)

    // 스크롤 맨 아래로 이동
    scrollToBottom();
    
    // 목록 업데이트
    selectMessageList();
}