console.log('chatting_manager.js')

// 페이지네이션 관련 변수
let currentPage = 1;
let totalPages = 4; // 서버에서 받아올 값
let filterType = 'all'; // 'all' or 'notread'

document.addEventListener('DOMContentLoaded', () => {

    // 문서 로딩시 3가지 수행 (목록, 채팅방, 채팅 알림 클릭)
    roomListAddEvent();

    // 버튼 이벤트용 변수 
    const sendBtn = document.getElementById('sendBtn');
    const chattingInput = document.getElementById('chattingInput');

    // 전송 버튼 클릭 시에도 전송
    sendBtn.addEventListener('click', sendMessage);

    // URL 파라미터로 채팅방 자동 열기
    const params = new URLSearchParams(location.search)
    const chatNo = params.get('chat-no')
    const chattingItems = document.querySelectorAll('.chatting-item')
    if(chatNo != null){
        chattingItems.forEach( item => {
            if(item.getAttribute('chat-no') == chatNo){
                item.click()
                return;
            }
        })
    }

    // 필터 버튼 이벤트
    const filterButtons = document.querySelectorAll('.header_nav_button > button');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', handleFilterClick);
    });

    // 페이지네이션 이벤트 (하단)
    const paginationLinks = document.querySelectorAll('.side_list_pagination a');
    paginationLinks.forEach(link => {
        link.addEventListener('click', handlePagination);
    });

    // 페이지네이션 이벤트 (상단) - 추가 ✅
    const headerNavPages = document.querySelectorAll('.header_nav_page a');
    headerNavPages.forEach(link => {
        link.addEventListener('click', handlePagination);
    });

    // 엔터 입력 시 전송 로직
    chattingInput.addEventListener('keydown', (e) => {
        // e.key를 사용하여 엔터키 확인 (window.e.key 아님)
        if (e.key === 'Enter') {
            // 한글 입력 시 중복 이벤트 방지 (IME 에러 방지)
            if (e.isComposing) return;
            if (!e.shiftKey) {
                e.preventDefault(); // 엔터 시 줄바꿈 방지
                sendMessage();      // 메시지 전송 함수 호출
            }
        }
    });

    // 파일 첨부 버튼
    const attachButton = document.querySelector('.attach_button');
    attachButton.addEventListener('click', triggerFileInput);

    // 초기 페이지 상태 표시
    updatePageStatus();
    
    // 초기 채팅방 목록 로드 (서버에서 totalPages 받아오기)
    selectRoomList();

    return;
})

// 전역변수 선언 
let selectChattingNo;
let selectTargetNo;
let selectTargetName;
let selectTargetProfile;


// ========== 필터 및 페이지네이션 ==========

/**
 * 필터 버튼 클릭 처리
 */
function handleFilterClick(e) {
    const buttons = document.querySelectorAll('.header_nav_button > button');
    
    // 기존 활성 버튼 제거
    buttons.forEach(btn => btn.classList.remove('active'));
    
    // 클릭한 버튼 활성화
    e.target.classList.add('active');
    
    // 필터 타입 설정
    if(e.target.textContent === '전체') {
        filterType = 'all';
    } else if(e.target.textContent === '안읽음') {
        filterType = 'notread';
    }
    
    // 1페이지로 초기화하고 목록 재조회
    currentPage = 1;
    selectRoomList();
}

/**
 * 페이지 상태 업데이트 함수
 * HTML의 "< 1 / 4 >" 부분을 업데이트
 */
function updatePageStatus() {
    const pageStatus = document.querySelector('.page_status');
    if(pageStatus) {
        pageStatus.innerText = ` ${currentPage} / ${totalPages}`;
    }
}

/**
 * 페이지네이션 처리
 */
function handlePagination(e) {
    e.preventDefault();
    
    const text = e.target.textContent;
    const pageLinks = document.querySelectorAll('.side_list_pagination a');
    
    if(text === '«') {
        currentPage = 1;  // 처음으로
    } else if(text === '<') {
        if(currentPage > 1) currentPage--;  // 이전
    } else if(text === '>') {
        if(currentPage < totalPages) currentPage++;  // 다음
    } else if(text === '»') {
        currentPage = totalPages;  // 마지막으로
    } else if(!isNaN(text)) {
        currentPage = parseInt(text);  // 페이지 번호 선택
    }
    
    // 페이지 버튼 업데이트 및 목록 재조회
    updatePageButtons();
    updatePageStatus();  // ✅ 추가: 상단 페이지 상태 업데이트
    selectRoomList();
}

/**
 * 페이지 버튼 UI 업데이트
 */
function updatePageButtons() {
    const pageLinks = document.querySelectorAll('.side_list_pagination a');
    
    pageLinks.forEach(link => {
        const text = link.textContent;
        
        // 숫자 버튼의 경우
        if(!isNaN(text) && text !== '' && text !== '<' && text !== '>' && text !== '«' && text !== '»') {
            link.classList.remove('active');
            if(parseInt(text) === currentPage) {
                link.classList.add('active');
            }
        }
    });
}

/**
 * 엔터 키 입력 처리
 * IME(한글 입력) 중복 이벤트 방지
 */
function handleEnterKey(e) {
    // e.key를 사용하여 엔터키 확인 (window.e.key 아님)
    if (e.key === 'Enter') {
        // 한글 입력 시 중복 이벤트 방지 (IME 에러 방지)
        if (e.isComposing) return;
        if (!e.shiftKey) {
            e.preventDefault(); // 엔터 시 줄바꿈 방지
            sendMessage();      // 메시지 전송 함수 호출
        }
    }
}

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
    
    // 파일 입력 초기화 (같은 파일을 다시 선택할 수 있도록)
    e.target.value = '';
}

/**
 * 파일 메시지 전송
 */
function sendFileMessage(fileName, fileData) {
    if(!selectChattingNo) {
        alert('채팅방을 선택해주세요.');
        return;
    }
    
    const obj = {
        senderNo: loginMemberNo,
        targetNo: selectTargetNo,
        chattingNo: selectChattingNo,
        messageContent: '[이미지: ' + fileName + ']',
        fileData: fileData,
        isFile: true
    };
    
    chattingSock.send(JSON.stringify(obj));
    
    // 채팅 알림 보내기
    const url = `${location.pathname}?chat-no=${selectChattingNo}`;
    sendNotification('chatting', url, selectTargetNo, '[이미지 전송]');
}


// ========== 채팅 목록 관리 ==========

/**
 * 목록 로딩 및 이벤트 추가
 */
function roomListAddEvent() {
    const chattingItems = document.querySelectorAll('.chatting-item');

    for (let item of chattingItems) {
        item.addEventListener('click', e => {

            selectChattingNo = item.getAttribute('chat-no');
            selectTargetNo = item.getAttribute('target-no');
            selectTargetProfile = item.querySelector('.list-profile')?.getAttribute('src') || '/resources/images/user.png';
            selectTargetName = item.querySelector('.target-name')?.innerText || '사용자';

            // 알림이 있는 경우 삭제 
            const notReadCount = item.querySelector('.not-read-count');
            if (notReadCount != undefined) {
               notReadCount.remove();
            }

            // 채팅방에서 해당된 부분만 select 추가 
            item.classList.add('select');
            for (let it of chattingItems) {
                if(it !== item) {
                    it.classList.remove('select');
                }
            }

            // 채팅방 목록 조회
            selectRoomList();

            // 메세지 목록 조회
            selectMessageList();
        })
    }
}

/**
 * 비동기로 채팅방 목록 조회
 */
function selectRoomList() {
    // 필터 파라미터 추가
    let url = '/chatting/roomList?page=' + currentPage + '&filter=' + filterType;
    
    fetch(url)
        .then(resp => resp.json())
        .then(data => {
            // totalPages 업데이트 (서버에서 받아온다고 가정)
            if(data.totalPages) {
                totalPages = data.totalPages;
            }
            
            const roomList = data.roomList || data;
            const chattingList = document.querySelector('.chatting_list')
            chattingList.innerHTML = '';

            for (let room of roomList) {
                const li = document.createElement('li')
                li.classList.add('chatting-item')
                li.setAttribute('chat-no', room.chattingNo)
                li.setAttribute('target-no', room.targetNo)

                if (room.chattingNo == selectChattingNo) {
                    li.classList.add('select')
                }
                
                // item-header 부분
                const listProfile = document.createElement('img')
                listProfile.classList.add('list-profile')

                if (room.targetProfile == undefined) {
                    listProfile.setAttribute('src', '/resources/images/user.png')
                } else {
                    listProfile.setAttribute('src', room.targetProfile);
                }

                li.appendChild(listProfile);

                // item-body 부분
                const itemBody = document.createElement('div')
                itemBody.classList.add('item-body')

                const p = document.createElement('p')

                const targetName = document.createElement('span')
                targetName.classList.add('target-name')
                targetName.innerText = room.targetNickName;

                const recentSendTime = document.createElement('span')
                recentSendTime.classList.add('recent-send-time')
                recentSendTime.innerText = room.sendTime;

                p.appendChild(targetName);
                p.appendChild(recentSendTime);

                const div = document.createElement('div')

                const recentMessage = document.createElement('p')
                recentMessage.classList.add('recent-message')

                if (room.lastMessage != undefined) {
                    recentMessage.innerHTML = room.lastMessage
                }

                div.appendChild(recentMessage);

                itemBody.appendChild(p);
                itemBody.appendChild(div);

                // 보고 있는 채팅방과 아닌 채팅방을 구분하여 메세지 왔을때 개수 표시
                if (room.notReadCount > 0 && room.chattingNo != selectChattingNo) {
                    const notReadCount = document.createElement('p')
                    notReadCount.classList.add('not-read-count')
                    notReadCount.innerText = room.notReadCount;
                    li.appendChild(notReadCount);
                } else if(room.chattingNo == selectChattingNo) {
                    // 현재 보고 있는 채팅방은 읽음 처리
                    setTimeout(() => {
                        updateReadFlag();
                    }, 200)
                }
                
                li.appendChild(listProfile);
                li.appendChild(itemBody);
                chattingList.appendChild(li);
            }
            
            // 페이지 버튼 업데이트
            updatePageButtons();
            // 상단 페이지 상태 표시 업데이트
            updatePageStatus();
            
            // 이벤트 리스너 재할당
            roomListAddEvent();
        })
        .catch(err => console.log(err))
}

/**
 * 읽음 상태 업데이트
 */
function updateReadFlag() {
    if(!selectChattingNo || !loginMemberNo) return;
    
    fetch('/chatting/updateReadFlag', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            'memberNo': loginMemberNo,
            'chattingNo': selectChattingNo
        })
    })
        .then(resp => resp.text())
        .then(result => {
            console.log('읽음 처리:', result)
        })
        .catch(err => console.log(err))
}


// ========== 메세지 관리 ==========

/**
 * 비동기로 메세지 목록을 조회하는 함수
 */
function selectMessageList() {
    if(!selectChattingNo || !loginMemberNo) {
        console.log('채팅방 또는 로그인 정보가 없습니다.');
        return;
    }
    
    fetch("/chatting/selectMessageList?chattingNo=" + selectChattingNo + "&memberNo=" + loginMemberNo)
        .then(resp => resp.json())
        .then(messageList => {
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

                    // 상대 프로필 
                    const img = document.createElement('img')
                    img.setAttribute('src', selectTargetProfile)

                    const div = document.createElement('div')

                    // 상대 이름 
                    const b = document.createElement('b')
                    b.innerText = selectTargetName
                    
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
            
            // 스크롤 맨 아래로 이동
            scrollToBottom();
        })
        .catch(err => console.log(err))
}

/**
 * 스크롤을 메시지 맨 아래로 이동
 */
function scrollToBottom() {
    const ul = document.querySelector('.display-chatting');
    if(ul) {
        // requestAnimationFrame으로 DOM 업데이트 완료 후 스크롤
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
        if(!selectChattingNo) {
            alert('채팅방을 선택해주세요.');
            return;
        }
        
        const messageContent = chattingInput.value;
        
        const obj = {
            senderNo: loginMemberNo,
            targetNo: selectTargetNo,
            chattingNo: selectChattingNo,
            messageContent: messageContent
        }

        chattingSock.send(JSON.stringify(obj));
        
        // 채팅 알림 보내기
        const url = `${location.pathname}?chat-no=${selectChattingNo}`;        
        sendNotification('chatting', url, selectTargetNo, messageContent)

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

    if (selectChattingNo == msg.chattingNo) {

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

            // 상대 프로필
            const img = document.createElement('img');
            img.setAttribute('src', selectTargetProfile)

            const div = document.createElement('div')

            // 상대 이름 
            const b = document.createElement('b')
            b.innerText = selectTargetName;

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
    }

    // 목록 업데이트 (새로운 메시지가 왔을 때)
    selectRoomList();
}


// ========== 채팅 상대 검색 ==========

const targetInput = document.getElementById('targetInput')
const resultArea = document.getElementById('resultArea')

targetInput.addEventListener('input', e => {
    const query = e.target.value.trim();

    // 입력값이 없을 경우
    if (query.length == 0) {
        resultArea.innerHTML = '';
        resultArea.classList.remove('active');
        return;
    }

    // 입력값이 있을 경우
    fetch('/chatting/selectTarget?query=' + query)
        .then(resp => resp.json())
        .then(list => {
            resultArea.innerHTML = '';

            // 일치하는 회원이 없는 경우
            if (list.length == 0) {
                const li = document.createElement('li')
                li.classList.add('result-row');
                li.innerText = '일치하는 회원이 없습니다.'
                resultArea.appendChild(li)
                resultArea.classList.add('active');
                return;
            }

            // 일치하는 회원이 있는 경우 
            for (let member of list) {
                const li = document.createElement('li')
                li.classList.add('result-row')
                li.setAttribute('data-id', member.memberNo)

                const img = document.createElement('img')
                img.classList.add('result-row-img')

                // 프로필 이미지 여부에 따라 src 속성 지정
                if (member.profileImage == null) {
                    img.setAttribute('src', '/resources/images/user.png')
                } else {
                    img.setAttribute('src', member.profileImage);
                }

                let nickname = member.memberNickname;
                let email = member.memberEmail;

                const span = document.createElement('span')
                span.innerHTML = `${nickname} ${email}`.replaceAll(query, `<mark>${query}</mark>`)

                li.appendChild(img);
                li.appendChild(span);
                resultArea.appendChild(li);

                li.addEventListener('click', chattingEnter);
            }
            
            resultArea.classList.add('active');
        })
        .catch(err => console.log(err))
})

/**
 * 채팅방 입장
 */
function chattingEnter(e) {
    const targetNo = e.currentTarget.getAttribute('data-id');

    fetch('/chatting/enter?targetNo=' + targetNo)
        .then(resp => resp.json())
        .then(chattingNo => {

            // 채팅방 목록 조회
            selectRoomList();

            // 채팅방 목록내 해당 채팅방 존재여부 확인 
            setTimeout(() => {
                const itemList = document.querySelectorAll('.chatting-item');
                for (let item of itemList) {

                    if (chattingNo == item.getAttribute('chat-no')) {
                        targetInput.value = '';
                        resultArea.innerHTML = '';
                        resultArea.classList.remove('active');

                        item.click();
                        return;
                    }
                }

            }, 100)
        })
        .catch(err => console.log(err))
}