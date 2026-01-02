console.log('chatting_manager.js 로드됨');

let chattingSock; 
if (typeof SockJS !== 'undefined') {
    chattingSock = new SockJS("/chattingSock"); 

    chattingSock.onmessage = function(e) {
        const msg = JSON.parse(e.data);
        const receivedNo = msg.chattingNo || msg.chatting_no || msg.chattingRoomId;
        if (selectChattingNo == receivedNo) {
            selectMessageList(); 
        }
        selectRoomList();
    };
}

let currentPage = 1;
let totalPages = 1;
let filterType = 'all';

let selectChattingNo;
let selectTargetNo;
let selectTargetName;
let selectTargetProfile;

document.addEventListener('DOMContentLoaded', () => {
    selectRoomList();

    // 전송 버튼
    const sendBtn = document.getElementById('sendBtn');
    if (sendBtn) sendBtn.addEventListener('click', sendMessage);

    // 필터 버튼
    const filterButtons = document.querySelectorAll('.header_nav_button > button');
    filterButtons.forEach(btn => btn.addEventListener('click', handleFilterClick));

    // 엔터키 전송
    const chattingInput = document.getElementById('chattingInput');
    if (chattingInput) {
        chattingInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.isComposing && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }

    // 파일 첨부
    const attachButton = document.querySelector('.attach_button');
    if (attachButton) attachButton.addEventListener('click', triggerFileInput);

    // [중요] 검색 입력창 이벤트 확인
    const targetInput = document.getElementById('targetInput');
    if (targetInput) {
        console.log('검색창 로드 완료'); // 디버깅용
        targetInput.addEventListener('input', e => {
            const query = e.target.value.trim();
            console.log('입력된 검색어:', query); // 키보드 입력 시마다 찍혀야 함
            if (query.length > 0) {
                searchTarget(query);
            } else {
                document.getElementById('resultArea').innerHTML = '';
            }
        });
    }
});

// ========== 상대방 검색 (실시간) ==========
function searchTarget(query) {
    fetch("/chatting/selectTarget?query=" + query)
        .then(resp => resp.json())
        .then(list => {
            const resultArea = document.getElementById('resultArea');
            resultArea.innerHTML = '';

            // 검색어가 없거나 리스트가 비었을 때 처리
            if (!list || list.length === 0) {
                resultArea.classList.remove('active'); // 결과창 숨기기
                return;
            }

            // 결과가 있으면 active 클래스 추가해서 보여주기
            resultArea.classList.add('active');

            list.forEach(member => {
                const nickname = member.member_nickname;
                const memberNo = member.member_no;
                const profile = member.profile_path || '/svg/person.svg';

                const li = document.createElement('li');
                li.classList.add('result-row');
                li.innerHTML = `
                    <img src="${profile}">
                    <span>${nickname}</span>
                `;
                
                li.onclick = () => {
                    chattingEnter(memberNo);
                    resultArea.classList.remove('active'); // 클릭 시 창 닫기
                    document.getElementById('targetInput').value = ''; // 입력창 비우기
                };
                resultArea.appendChild(li);
            });
        })
        .catch(err => console.error("검색 중 오류:", err));
}


// ========== 채팅방 입장 및 생성 ==========
function chattingEnter(targetNo) {
    fetch("/chatting/enter?targetNo=" + targetNo)
        .then(resp => resp.text())
        .then(chattingNo => {
            if (chattingNo > 0) {
                // 1. 입력창 및 검색 결과 초기화
                const resultArea = document.getElementById('resultArea');
                document.getElementById('targetInput').value = '';
                resultArea.innerHTML = '';
                resultArea.classList.remove('active');
                
                // 2. 현재 선택된 방 번호 업데이트
                selectChattingNo = chattingNo;

                // 3. 목록을 새로 불러온 후, 새로 만든 방을 바로 클릭한 효과 주기
                // 목록 조회(selectRoomList)가 끝난 시점에 로직이 돌아야 하므로 Promise나 콜백을 활용하는 게 좋지만,
                // 우선 가장 확실한 방법은 selectRoomList를 실행하고 잠시 후 메시지 리스트를 불러오는 것입니다.
                selectRoomList(); 

                // 약간의 시간차를 두어 목록이 렌더링된 후 메시지를 가져옵니다.
                setTimeout(() => {
                    selectMessageList();
                }, 300);
            } else {
                alert("채팅방 생성에 실패했습니다.");
            }
        })
        .catch(err => console.error("입장 중 오류:", err));
}

// ========== 채팅방 목록 조회 (왼쪽 리스트) ==========
function selectRoomList() {
    fetch(`/chatting/roomList?page=${currentPage}&filter=${filterType}`)
        .then(resp => resp.json())
        .then(data => {
            const chattingList = document.querySelector('.chatting_list');
            if (!chattingList) return;

            let roomList = data.roomList || (Array.isArray(data) ? data : []);
            if (data.totalPages) totalPages = data.totalPages;
            chattingList.innerHTML = '';

            if (roomList.length === 0) {
                chattingList.innerHTML = '<li class="individual_chatter empty">채팅 내역이 없습니다.</li>';
                return;
            }

            roomList.forEach(room => {
                const roomId = room.chatting_room_id || room.chattingRoomId;
                const targetNo = room.target_no || room.targetNo;
                const targetName = room.target_nickname || room.targetNickName || '알 수 없음';
                const targetProfile = room.target_profile || room.targetProfile || '/svg/person.svg';
                const sendTime = room.send_time || room.sendTime || '';
                const lastMsg = room.last_message || room.lastMessage || '대화 내용이 없습니다';
                const notReadCount = room.not_read_count || room.notReadCount || 0;

                const li = document.createElement('li');
                li.classList.add('individual_chatter', 'chatting-item');
                li.setAttribute('data-chat-no', roomId);
                li.setAttribute('data-target-no', targetNo);

                if (roomId == selectChattingNo) li.classList.add('select');

                li.innerHTML = `
                    <img class="list-profile" src="${targetProfile}">
                    <div class="item-body">
                        <p>
                            <span class="target-name">${targetName}</span>
                            <span class="recent-send-time">${sendTime}</span>
                        </p>
                        <div>
                            <p class="recent-message">${lastMsg}</p>
                            ${notReadCount > 0 ? `<p class="not-read-count">${notReadCount}</p>` : ''}
                        </div>
                    </div>
                `;
                chattingList.appendChild(li);
            });
            updatePageStatus();
            roomListAddEvent();
        })
        .catch(err => console.error('목록 조회 실패:', err));
}

function roomListAddEvent() {
    document.querySelectorAll('.chatting-item').forEach(item => {
        item.onclick = () => {
            selectChattingNo = item.getAttribute('data-chat-no');
            selectTargetNo = item.getAttribute('data-target-no');
            selectTargetName = item.querySelector('.target-name')?.innerText;
            selectTargetProfile = item.querySelector('.list-profile')?.src;

            document.querySelectorAll('.chatting-item').forEach(li => li.classList.remove('select'));
            item.classList.add('select');

            const badge = item.querySelector('.not-read-count');
            if (badge) badge.remove();

            selectMessageList();
            updateReadFlag();
        };
    });
}

function selectMessageList() {
    if (!selectChattingNo) return;
    fetch(`/chatting/selectMessageList?chattingNo=${selectChattingNo}`)
        .then(resp => resp.json())
        .then(messageList => {
            const ul = document.querySelector('.display-chatting');
            if (!ul) return;
            ul.innerHTML = '';
            messageList.forEach(msg => {
                const sNo = msg.senderNo || msg.sender_no;
                const content = msg.messageContent || msg.message_content;
                const sentAt = msg.sentAt || msg.sent_at || '';
                const li = document.createElement('li');
                const isMyMessage = sNo == loginMemberNo;
                li.classList.add(isMyMessage ? 'my-chat' : 'target-chat');
                if (isMyMessage) {
                    li.innerHTML = `<p class="chat">${content}</p><span class="chatDate">${sentAt}</span>`;
                } else {
                    li.innerHTML = `<img src="${selectTargetProfile}"><div><b>${selectTargetName}</b><div style="display:flex; gap:8px; align-items:flex-end;"><p class="chat">${content}</p><span class="chatDate">${sentAt}</span></div></div>`;
                }
                ul.appendChild(li);
            });
            ul.scrollTop = ul.scrollHeight;
        });
}

function sendMessage() {
    const input = document.getElementById('chattingInput');
    if (!input?.value.trim() || !selectChattingNo) return;
    const obj = { senderNo: loginMemberNo, targetNo: selectTargetNo, chattingNo: selectChattingNo, messageContent: input.value.trim() };
    if (chattingSock?.readyState === 1) {
        chattingSock.send(JSON.stringify(obj));
        input.value = '';
        input.focus();
    }
}

function updateReadFlag() {
    fetch('/chatting/updateReadFlag', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memberNo: loginMemberNo, chattingNo: selectChattingNo })
    });
}

function handleFilterClick(e) {
    document.querySelectorAll('.header_nav_button > button').forEach(btn => btn.classList.remove('active'));
    e.target.classList.add('active');
    filterType = e.target.classList.contains('notread') ? 'notread' : 'all';
    currentPage = 1;
    selectRoomList();
}

function updatePageStatus() {
    const status = document.querySelector('.page_status');
    if (status) status.innerText = ` ${currentPage} / ${totalPages}`;
}

function triggerFileInput() {
    let fileInput = document.getElementById('chattingFile') || document.createElement('input');
    if (!fileInput.id) {
        fileInput.type = 'file'; fileInput.id = 'chattingFile'; fileInput.accept = 'image/*'; fileInput.style.display = 'none';
        fileInput.onchange = (e) => {
            const file = e.target.files[0];
            const reader = new FileReader();
            reader.onload = (ev) => {
                const obj = { senderNo: loginMemberNo, targetNo: selectTargetNo, chattingNo: selectChattingNo, messageContent: '[이미지]', fileData: ev.target.result, isFile: true };
                if (chattingSock?.readyState === 1) chattingSock.send(JSON.stringify(obj));
            };
            reader.readAsDataURL(file);
        };
        document.body.appendChild(fileInput);
    }
    fileInput.click();
}