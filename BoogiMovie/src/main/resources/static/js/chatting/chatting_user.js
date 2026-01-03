console.log('chatting_user.js 로드됨 (사용자용)');

let chattingSock; 
let selectChattingNo;      // 채팅방 번호
let selectTargetNo;        // 관리자 번호
let unreadCount = 0;
let isInitialLoad = true; 
let isAutoScroll = true; 

if (typeof SockJS !== 'undefined') {
    chattingSock = new SockJS("/chattingSock"); 

    chattingSock.onmessage = function(e) {
        try {
            const msg = JSON.parse(e.data);
            const receivedNo = msg.chattingNo || msg.chatting_no || msg.chattingRoomId;
            
            if (selectChattingNo && Number(selectChattingNo) === Number(receivedNo)) {
                selectMessageList(); 
            } else {
                unreadCount++;
                updateUnreadUI();
            }
        } catch(err) {
            console.error("수신 데이터 파싱 오류:", err);
        }
    };

    chattingSock.onopen = () => console.log("웹소켓 서버 연결 성공");
}

// ========== 안읽음 UI 업데이트 ==========
function updateUnreadUI() {
    const badge = document.querySelector('.notread_img');
    if (badge) {
        badge.innerText = unreadCount;
        badge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
    }
}

// 창을 다시 볼 때 처리
window.onfocus = function() {
    if (!isInitialLoad && selectChattingNo && unreadCount > 0 && isAutoScroll) {
        updateReadFlag();
        getUnreadCount();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    enterAdminChat(); // 관리자 채팅방 입장
    setTimeout(() => { isInitialLoad = false; }, 1500);

    // 1. 기존 로직들
    const sendBtn = document.getElementById('sendBtn');
    if (sendBtn) sendBtn.addEventListener('click', sendMessage);

    const chattingInput = document.getElementById('chattingInput');
    if (chattingInput) {
        chattingInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.isComposing && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }

    const attachButton = document.querySelector('.attach_button');
    if (attachButton) attachButton.addEventListener('click', triggerFileInput);

    const chatDisplay = document.querySelector('.display-chatting');
    if (chatDisplay) {
        // 클릭 시 읽음 처리
        chatDisplay.addEventListener('click', () => {
            if (unreadCount > 0) {
                updateReadFlag();
                unreadCount = 0;
                updateUnreadUI();
                isAutoScroll = true;
            }
        });

        // 스크롤 감지
        chatDisplay.addEventListener('scroll', () => {
            const isAtBottom = chatDisplay.scrollHeight - chatDisplay.scrollTop <= chatDisplay.clientHeight + 50;
            if (isAtBottom) {
                isAutoScroll = true; 
            } else {
                isAutoScroll = false;
            }
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

// ========== 메시지 목록 조회 (방어 코드 추가) ==========
function selectMessageList() {
    if (!selectChattingNo || selectChattingNo === 'undefined') return;
    
    fetch(`/chatting/selectMessageList?chattingNo=${selectChattingNo}`)
        .then(resp => {
            if(!resp.ok) throw new Error("메시지 조회 실패");
            return resp.json();
        })
        .then(messageList => {
            const ul = document.querySelector('.display-chatting');
            if (!ul) return;
            ul.innerHTML = '';
            
            // messageList가 배열인지 확인
            if(!Array.isArray(messageList)) return;

            messageList.forEach(msg => {
                const sNo = msg.senderId || msg.senderNo || msg.sender_no;
                const content = msg.messageContent || msg.message_content || "";
                const sentAt = msg.sendTime || msg.send_time || msg.sentAt || "";

                const li = document.createElement('li');
                const isMyMessage = Number(sNo) === Number(loginMemberNo);
                li.classList.add(isMyMessage ? 'my-chat' : 'target-chat');
                
                if (isMyMessage) {
                    li.innerHTML = `<p class="chat">${content}</p><span class="chatDate">${sentAt}</span>`;
                } else {
                    li.innerHTML = `
                        <img src="/svg/person.svg">
                        <div>
                            <b>관리자</b>
                            <div style="display:flex; gap:8px; align-items:flex-end;">
                                <p class="chat">${content}</p>
                                <span class="chatDate">${sentAt}</span>
                            </div>
                        </div>`;
                }
                ul.appendChild(li);
            });
            ul.scrollTop = ul.scrollHeight;
        })
        .catch(err => console.error(err));
}

// ========== 메시지 전송 ==========
function sendMessage() {
    const input = document.getElementById('chattingInput');
    if (!input?.value.trim() || !selectChattingNo || !selectTargetNo) return;
    
    const obj = { 
        senderNo: loginMemberNo, 
        targetNo: selectTargetNo, 
        chattingNo: selectChattingNo, 
        messageContent: input.value.trim() 
    };

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