console.log('chatting_user.js 로드됨 (사용자용)');

let chattingSock; 
let selectChattingNo;      // 채팅방 번호
let selectTargetNo;        // 관리자 번호
let unreadCount = 0;
let isInitialLoad = true; 
let isAutoScroll = true; 

let fixedLastReadIndex = null;

if (typeof SockJS !== 'undefined') {
    chattingSock = new SockJS("/chattingSock"); 

    // 1. 메시지 수신 시 처리 로직 수정
    chattingSock.onmessage = function(e) {
    try {
        const msg = JSON.parse(e.data);
        const receivedNo = msg.chattingNo || msg.chatting_no || msg.chattingRoomId;
        
        if (selectChattingNo && Number(selectChattingNo) === Number(receivedNo)) {
            // 안 읽은 상태가 시작될 때 인덱스를 고정하기 위해 unreadCount 체크
            if (document.visibilityState === 'hidden' || !document.hasFocus() || !isAutoScroll) {
                unreadCount++;
                updateUnreadUI();
            }
            selectMessageList(); 
        } else {
            unreadCount++;
            updateUnreadUI();
        }
    } catch(err) { console.error(err); }
};


    chattingSock.onopen = () => console.log("웹소켓 서버 연결 성공");
}


// 2. UI 업데이트 함수
// 2. UI 업데이트 함수
function updateUnreadUI() {
    // 1) 페이지 내의 모든 배지(클래스 기반)와 헤더 특정 배지(ID 기반) 호출
    const badges = document.querySelectorAll('.notread_img');
    const headerBadge = document.getElementById('headerUnreadBadge');

    // 2) 클래스로 찾은 모든 요소 업데이트 (채팅창 내부 등)
    badges.forEach(badge => {
        badge.innerText = unreadCount;
        badge.style.display = unreadCount > 0 ? 'inline-block' : 'none';

        // [채팅창 내부 전용] '안읽음' 버튼 처리 로직
        const parentButton = badge.closest('.notread');
        if (parentButton) {
            parentButton.onclick = () => {
                if (unreadCount > 0) {
                    isAutoScroll = true;
                    selectMessageList();
                    setTimeout(() => {
                        const line = document.querySelector('.unread-line');
                        if (line) line.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        updateReadFlag();
                        unreadCount = 0;
                        updateUnreadUI();
                    }, 150); 
                }
            };
        }
    });

    // 3) 헤더 배지 업데이트 (ID 기반으로 한 번 더 확실하게 처리)
    // 위 forEach에서 이미 처리되었을 수도 있지만, ID가 다를 경우를 대비한 안전장치입니다.
    if (headerBadge) {
        headerBadge.innerText = unreadCount;
        headerBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
    }
}

// 창을 다시 볼 때 처리
window.onfocus = function() {
    if (!isInitialLoad && selectChattingNo && unreadCount > 0 && isAutoScroll) {
        updateReadFlag();
        getUnreadCount();
    }
};

document.addEventListener('DOMContentLoaded', () => { console.log('테스트0')
    enterAdminChat();
    setTimeout(() => { isInitialLoad = false; }, 1500);

    const sendBtn = document.getElementById('sendBtn');
    if (sendBtn) sendBtn.addEventListener('click', sendMessage);        

    const chattingInput = document.getElementById('chattingInput');        

    if (chattingInput) {
        chattingInput.addEventListener('keydown', (e) => {
            
            if (e.key === 'Enter' && !e.isComposing && !e.shiftKey) {
                e.preventDefault();
                console.log(e.key)
                sendMessage();                    
                
            }
        });
    }

    const attachButton = document.querySelector('.attach_button');
    if (attachButton) attachButton.addEventListener('click', triggerFileInput);

    const chatDisplay = document.querySelector('.display-chatting');
    if (chatDisplay) {
        // ✅ [수정] 클릭 시 읽음 처리 및 안내선 로직
        chatDisplay.addEventListener('click', () => {
            if (unreadCount > 0) {
                // 1. 현재 목록 다시 그려서 "여기까지 읽었습니다" 선을 물리적으로 생성
                selectMessageList(); 
                
                // 2. 서버 및 UI 읽음 처리
                updateReadFlag();
                
                // 3. 카운트 초기화 (잠깐 유지 후 초기화하고 싶다면 순서 조절 가능)
                unreadCount = 0;
                updateUnreadUI();
                
                // 4. 이후 오는 메시지는 다시 자동 스크롤 되도록 설정
                isAutoScroll = true;
            }
        });

        // [유지] 스크롤 감지 (새 메시지 올 때 아래로 튕기지 않게 하는 기준점)
        chatDisplay.addEventListener('scroll', () => {
            const isAtBottom = chatDisplay.scrollHeight - chatDisplay.scrollTop <= chatDisplay.clientHeight + 80;
            isAutoScroll = isAtBottom; 
        });
    }
});

// ========== 관리자 채팅방 입장 ==========
function enterAdminChat() {
    fetch("/chatting/senter") 
        .then(resp => resp.json())
        .then(data => {
            if (data.chattingNo > 0) {
                selectChattingNo = data.chattingNo;
                selectTargetNo = data.targetNo; 
                selectMessageList();
                getUnreadCount();
            }
        });
}

// ========== 안읽음 개수 가져오기 ==========
function getUnreadCount() {
    if (!selectChattingNo) return;
    
    fetch(`/chatting/unreadCount?chattingNo=${selectChattingNo}`)
        .then(resp => resp.text())
        .then(count => {
            unreadCount = Number(count);
            updateUnreadUI();
        })
        .catch(err => console.error("안읽음 개수 조회 실패:", err));
}

// ========== 메시지 목록 조회 (안내선 추가 및 10초 후 삭제) ==========
function selectMessageList() {
    if (!selectChattingNo || selectChattingNo === 'undefined') return;
    
    fetch(`/chatting/selectMessageList?chattingNo=${selectChattingNo}`)
        .then(resp => resp.json())
        .then(messageList => {
            const ul = document.querySelector('.display-chatting');
            if (!ul) return;

            const currentScrollTop = ul.scrollTop;

            if (unreadCount > 0 && fixedLastReadIndex === null) {
                fixedLastReadIndex = messageList.length - unreadCount;
            } else if (unreadCount === 0) {
                fixedLastReadIndex = null;
            }

            ul.innerHTML = ''; 
            if(!Array.isArray(messageList)) return;

            messageList.forEach((msg, index) => {
                if (unreadCount > 0 && index === fixedLastReadIndex && isAutoScroll) {
                    const hr = document.createElement('li');
                    hr.classList.add('unread-line'); 
                    hr.innerHTML = `<span>여기까지 읽으셨습니다</span>`;
                    ul.appendChild(hr);

                    setTimeout(() => {
                        hr.classList.add('fade-out');
                        setTimeout(() => { if(hr.parentNode === ul) ul.removeChild(hr); }, 1000);
                    }, 10000);
                }

                const sNo = msg.senderNo || msg.sender_no || msg.senderId;
                const content = msg.message_content || msg.messageContent || "";
                const imgPath = msg.imgPath; // ⭐ 이미지 경로 변수 추가
                const sentAt = msg.sent_at || ""; 
                
                const li = document.createElement('li');
                const isMyMessage = Number(sNo) === Number(loginMemberNo);
                li.classList.add(isMyMessage ? 'my-chat' : 'target-chat');

                // ⭐ 이미지 여부에 따라 출력할 HTML 생성
                let chatContent = "";
                if(imgPath) {
                    // 이미지 경로가 있으면 img 태그 생성 (텍스트 [이미지] 대신 실제 사진 출력)
                    chatContent = `<img src="${imgPath}" class="chat-img" style="max-width: 250px; border-radius: 10px; cursor: pointer;" onclick="window.open(this.src)">`;
                } else {
                    // 이미지 경로가 없으면 기존처럼 텍스트 출력
                    chatContent = `<p class="chat">${content}</p>`;
                }

                if (isMyMessage) {
                    li.innerHTML = `
                        <span class="chatDate">${sentAt}</span>
                        ${chatContent}
                    `;
                } else {
                    li.innerHTML = `
                        <img src="/svg/person.svg">
                        <div>
                            <b>관리자</b>
                            <div style="display:flex; gap:8px; align-items:flex-end;">
                                ${chatContent}
                                <span class="chatDate">${sentAt}</span>
                            </div>
                        </div>
                    `;
                }
                ul.appendChild(li);
            });
            
            if (isAutoScroll) {
                ul.scrollTop = ul.scrollHeight;
            } else {
                ul.scrollTop = currentScrollTop;
            }
        });
}

// ========== 메시지 전송 ==========
function sendMessage() {
    console.log('오비제이before')
    console.log(selectChattingNo)
    console.log(selectTargetNo)
    
    const input = document.getElementById('chattingInput');
    console.log(input.value)
    if (!input?.value.trim() || !selectChattingNo || !selectTargetNo) return;
    
    const obj = { 
        senderNo: loginMemberNo, 
        targetNo: selectTargetNo, 
        chattingNo: selectChattingNo, 
        messageContent: input.value.trim() 
    };

    console.log('오비제이')
    console.log(obj)

    if (chattingSock?.readyState === 1) {
        chattingSock.send(JSON.stringify(obj));
        input.value = '';
        input.focus();
    }


}

// ========== 읽음 처리 (방어 코드 추가) ==========
function updateReadFlag() {
    if(!selectChattingNo || selectChattingNo === 'undefined') return;

    fetch('/chatting/updateReadFlag', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
            memberNo: loginMemberNo, 
            chattingNo: Number(selectChattingNo)
        })
    }).catch(err => console.error("읽음 처리 중 오류:", err));
}

function triggerFileInput() {
    const fileInput = document.getElementById('imageInput');

    if (!fileInput) return;
    
    fileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        
        const reader = new FileReader();
        reader.onload = (ev) => {
            const obj = { 
                senderNo: loginMemberNo, 
                targetNo: selectTargetNo, 
                chattingNo: selectChattingNo, 
                messageContent: '[이미지]', 
                fileData: ev.target.result, 
                isFile: true 
            };
            if (chattingSock?.readyState === 1) chattingSock.send(JSON.stringify(obj));
        };
        reader.readAsDataURL(file);
    };
    
    fileInput.click();
}

// 검색 
// ========== 검색 관련 변수 ==========
let searchResults = []; // 검색된 결과 요소들을 담을 배열
let currentSearchIndex = -1; // 현재 보고 있는 검색 결과 위치

// 1. 메시지 검색 함수
function searchMessage() {
    const input = document.getElementById('targetInput');
    const keyword = input.value.trim();
    const chatDisplay = document.querySelector('.display-chatting');
    const searchCountSpan = document.getElementById('searchCount');

    // 초기화
    searchResults = [];
    currentSearchIndex = -1;
    
    // 모든 메시지에서 기존 하이라이트 제거
    const allMessages = chatDisplay.querySelectorAll('.chat');
    allMessages.forEach(msg => msg.style.backgroundColor = '');

    if (keyword === "") {
        searchCountSpan.innerText = "0 / 0";
        return;
    }

    // 키워드가 포함된 메시지 찾기
    allMessages.forEach(msg => {
        if (msg.innerText.includes(keyword)) {
            searchResults.push(msg);
        }
    });

    if (searchResults.length > 0) {
        currentSearchIndex = searchResults.length - 1; // 가장 최근 메시지부터 표시
        updateSearchUI();
        scrollToSearchResult();
    } else {
        searchCountSpan.innerText = "0 / 0";
        alert("검색 결과가 없습니다.");
    }
}

// 2. 결과 위치 표시 업데이트
function updateSearchUI() {
    const searchCountSpan = document.getElementById('searchCount');
    if (searchResults.length > 0) {
        searchCountSpan.innerText = `${currentSearchIndex + 1} / ${searchResults.length}`;
    }
}

// 3. 해당 위치로 스크롤 및 하이라이트
function scrollToSearchResult() {
    if (currentSearchIndex < 0 || currentSearchIndex >= searchResults.length) return;

    // 전체 하이라이트 초기화 후 현재 결과만 표시
    searchResults.forEach(el => el.style.backgroundColor = '');
    const target = searchResults[currentSearchIndex];
    target.style.backgroundColor = '#ffff00aa'; // 노란색 하이라이트
    
    // 부드럽게 이동
    target.scrollIntoView({ behavior: 'smooth', block: 'center' });
    
    // 검색 중에는 자동 스크롤 방지
    isAutoScroll = false; 
    updateSearchUI();
}

// 4. 이전 결과 (위로)
function prevSearchResult() {
    if (searchResults.length === 0) return;
    currentSearchIndex--;
    if (currentSearchIndex < 0) currentSearchIndex = searchResults.length - 1; // 순환
    scrollToSearchResult();
}

// 5. 다음 결과 (아래로)
function nextSearchResult() {
    if (searchResults.length === 0) return;
    currentSearchIndex++;
    if (currentSearchIndex >= searchResults.length) currentSearchIndex = 0; // 순환
    scrollToSearchResult();
}