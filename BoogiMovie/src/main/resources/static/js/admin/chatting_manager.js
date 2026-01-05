console.log('chatting_manager.js 로드됨 (순환 배분 관리자용)');

let chattingSock; 
let selectChattingNo;      // 선택된 채팅방 번호
let selectTargetNo;        // 선택된 채팅방의 '사용자' 번호
let selectTargetName;      // 사용자 닉네임
let selectTargetProfile;   // 사용자 프로필
let unreadCount = 0;

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

            selectRoomList();
        } catch(err) {
            console.error("수신 데이터 파싱 오류:", err);
        }
    };

    chattingSock.onopen = () => console.log("웹소켓 서버 연결 성공");
}

// ========== 전체 안읽음 개수 조회 (모든 방 합계) ==========
function getTotalUnreadCount() {
    fetch(`/chatting/totalUnreadCount?memberNo=${loginMemberNo}`)
        .then(resp => resp.text())
        .then(count => {
            unreadCount = Number(count);
            updateUnreadUI();
        })
        .catch(err => console.error("전체 안읽음 개수 조회 실패:", err));
}

// ========== 안읽음 UI 업데이트 ==========
function updateUnreadUI() {
    const badges = document.querySelectorAll('.notread_img'); // 화면 내 모든 배지 선택
    
    badges.forEach(badge => {
        badge.innerText = unreadCount;
        // ✅ 0이면 숨김, 1 이상이면 inline-flex로 표시
        if (unreadCount > 0) {
            badge.style.display = 'inline-flex';
        } else {
            badge.style.display = 'none';
        }
    });
}

let currentPage = 1;
let totalPages = 1;
let filterType = 'all';

document.addEventListener('DOMContentLoaded', () => {
    selectRoomList();
    getUnreadCount();

    // 1. 기존 공통 로직
    const sendBtn = document.getElementById('sendBtn');
    if (sendBtn) sendBtn.addEventListener('click', sendMessage);

    const filterButtons = document.querySelectorAll('.header_nav_button > button');
    filterButtons.forEach(btn => btn.addEventListener('click', handleFilterClick));

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
    console.log('이미지 전송' ,attachButton)

    if (attachButton) attachButton.addEventListener('click', triggerFileInput);

    // 2. [수정됨] 검색창 로직 - 객체 키값 매핑 수정
    const targetInput = document.getElementById('targetInput');
    const resultArea = document.getElementById('resultArea');

    if (targetInput && resultArea) {
        targetInput.addEventListener('input', e => {
            const query = e.target.value.trim();
            
            if (query.length === 0) {
                resultArea.innerHTML = '';
                resultArea.style.display = 'none';
                return;
            }

            fetch(`/chatting/selectTarget?query=${query}`)
                .then(resp => resp.json())
                .then(list => {
                    resultArea.innerHTML = '';
                    
                    if (!list || list.length === 0) {
                        const li = document.createElement('li');
                        li.innerHTML = '<span style="padding:15px; display:block; color:#999; text-align:center;">검색 결과가 없습니다.</span>';
                        resultArea.appendChild(li);
                    } else {
                        list.forEach(member => {
                            const li = document.createElement('li');
                            li.classList.add('result-row');
                            
                            const memberNo = member.target_no || member.memberNo || member.member_no;
                            const name     = member.target_name || member.memberName || member.member_name || '이름없음';
                            const nickname = member.target_nick_name || member.memberNickname || member.member_nickname || '별칭없음';
                            const email    = member.member_email || member.memberEmail || '이메일 정보 없음';
                            
                            li.setAttribute('data-id', memberNo);
                            li.style.cssText = 'cursor:pointer; border-bottom:1px solid #f0f0f0; transition: background 0.2s;';
                            
                            li.innerHTML = `
                                <div style="padding:10px 15px;">
                                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:3px;">
                                        <strong style="font-size:14px; color:#333;">${name}</strong>
                                        <span style="font-size:12px; color:#666; background:#f5f5f5; padding:2px 6px; border-radius:4px;">${nickname}</span>
                                    </div>
                                    <div style="font-size:12px; color:#888;">${email}</div>
                                </div>
                            `;
                            
                            li.onmouseenter = () => li.style.backgroundColor = '#f9f9f9';
                            li.onmouseleave = () => li.style.backgroundColor = 'transparent';
                            
                            li.onclick = function() {
                                const targetNo = this.getAttribute('data-id');
                                selectRoomByMemberNo(targetNo); 
                                resultArea.innerHTML = '';
                                resultArea.style.display = 'none';
                                targetInput.value = '';
                            };
                            resultArea.appendChild(li);
                        });
                    }
                    resultArea.style.display = 'block';
                })
                .catch(err => console.error("검색 중 오류 발생:", err));
        });
    }
});

// 3. [추가] 특정 회원 번호를 가진 채팅방을 찾아 클릭해주는 함수
function selectRoomByMemberNo(memberNo) {
    // 1. 현재 화면 왼쪽 리스트에 해당 유저의 방이 있는지 확인
    const targetRoom = document.querySelector(`.chatting-item[data-user-no="${memberNo}"]`);

    if (targetRoom) {
        // 리스트에 있으면 바로 클릭
        targetRoom.click();
        targetRoom.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } else {
        // 2. 리스트에 없다면 서버에 채팅방 번호 요청 (없으면 생성)
        fetch("/chatting/enter?targetNo=" + memberNo)
            .then(resp => resp.text()) // 보통 방 번호를 숫자로 반환함
            .then(chattingNo => {
                if (chattingNo && chattingNo > 0) {
                    // 전역 변수 세팅 후 메시지 목록 불러오기
                    selectChattingNo = chattingNo;
                    selectTargetNo = memberNo;
                    
                    // 목록을 새로고침하여 해당 방이 리스트에 나타나게 함
                    selectRoomList(); 
                    
                    // 약간의 지연 후 메시지 리스트 호출 (목록이 그려질 시간 확보)
                    setTimeout(() => {
                        selectMessageList();
                    }, 200);
                } else {
                    alert("채팅방 정보를 불러올 수 없습니다.");
                }
            })
            .catch(err => console.error("방 입장 중 오류:", err));
    }
}

// ========== 채팅방 목록 조회 (수정 완료) ==========
// ========== 채팅방 목록 조회 (수정 완료) ==========
function selectRoomList() {
    fetch(`/chatting/roomList?page=${currentPage}&filter=${filterType}`)
        .then(resp => resp.json())
        .then(data => {
            const chattingList = document.querySelector('.chatting_list');
            if (!chattingList) return;

            let roomList = data.roomList || (Array.isArray(data) ? data : []);
            chattingList.innerHTML = '';
            
            let totalUnread = 0;

            roomList.forEach(room => {
                console.log("서버에서 온 방 데이터:", room);

                const roomId = room.chatting_room_id || room.chattingRoomId; 
                const userNo = room.target_no || room.targetNo;
                const notReadCount = room.not_read_count || room.notReadCount || 0;
                
                totalUnread += notReadCount;

                const li = document.createElement('li');
                li.classList.add('individual_chatter', 'chatting-item');
                
                if (roomId) {
                    li.setAttribute('data-chat-no', roomId);
                    li.setAttribute('data-user-no', userNo);
                } else {
                    console.error("이 데이터에는 방 번호가 없습니다 ->", room);
                }

                if (selectChattingNo && Number(roomId) === Number(selectChattingNo)) {
                    li.classList.add('select');
                }

                li.innerHTML = `
                    <img class="list-profile" src="${room.target_profile || room.targetProfile || '/svg/person.svg'}">
                    <div class="item-body">
                        <p>
                            <span class="target-name">${room.target_name || '이름없음'} (${room.target_nick_name || room.target_nickname || '닉네임없음'})</span>
                        </p>
                        <div>
                            <p class="recent-message">${room.last_message || room.lastMessage || '메시지 없음'}</p>
                            ${notReadCount > 0 ? `<span class="not-read-count">${notReadCount}</span>` : ''}
                            <button class="delete-room-btn" onclick="deleteChattingRoom('${roomId}', event)">&times;</button>
                        </div>
                    </div>
                `;
                chattingList.appendChild(li);

                const deleteBtn = li.querySelector('.delete-room-btn');
                if(deleteBtn) {
                    deleteBtn.style.cssText = 'display:flex !important; align-items:center !important; justify-content:center !important; opacity:1 !important; visibility:visible !important; background-color:#ff4444 !important; position:static !important; width:22px !important; height:22px !important; color:white !important; border:none !important; border-radius:50% !important; cursor:pointer !important; z-index:9999 !important; font-size:16px !important; line-height:1 !important; flex-shrink:0 !important; margin-left:15px !important;';
                }
            });
            
            // ⭐ 목록 조회 후 전체 안읽음 개수 업데이트
            unreadCount = totalUnread;
            updateUnreadUI();
            
            roomListAddEvent(); 
        });
}

// ========== 목록 클릭 이벤트 (수정 완료) ==========
function roomListAddEvent() {
    const items = document.querySelectorAll('.chatting-item');
    items.forEach(item => {
        item.onclick = function() {
            // [수정] getAttribute로 안전하게 데이터 추출
            const chatNo = this.getAttribute('data-chat-no');
            const userNo = this.getAttribute('data-user-no');

            // [방어 코드] 값이 비어있거나 'undefined' 문자열이면 실행 중단
            if (!chatNo || chatNo === 'undefined' || chatNo === 'null') {
                console.error("채팅방 번호가 유효하지 않습니다. (값:", chatNo, ")");
                return;
            }

            selectChattingNo = chatNo;
            selectTargetNo = userNo;
            selectTargetName = this.querySelector('.target-name')?.innerText;
            selectTargetProfile = this.querySelector('.list-profile')?.src;

            document.querySelectorAll('.chatting-item').forEach(li => li.classList.remove('select'));
            this.classList.add('select');

            selectMessageList(); 
            updateReadFlag();
            getUnreadCount();
        };
    });
}

// ========== 안읽음 개수 가져오기 ==========
function getUnreadCount() {
    if (!selectChattingNo) {
        // 선택된 방이 없으면 전체 안읽음 개수 조회
        getTotalUnreadCount();
        return;
    }
    
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
            
            if(!Array.isArray(messageList)) return;

            messageList.forEach(msg => {
                const sNo = msg.senderId || msg.senderNo || msg.sender_no;
                const content = msg.messageContent || msg.message_content || "";
                
                // ✅ 날짜 키값을 사용자용과 동일하게 sent_at으로 수정
                const sentAt = msg.sent_at || msg.sendTime || msg.send_time || "";
                const imgPath = msg.imgPath || msg.img_path;

                const li = document.createElement('li');
                const isMyMessage = Number(sNo) === Number(loginMemberNo);
                li.classList.add(isMyMessage ? 'my-chat' : 'target-chat');
                
                // 메시지 내용 (이미지 또는 텍스트)
                const messageHTML = imgPath 
                    ? `<img src="${imgPath}" style="max-width: 200px; border-radius: 8px;">` 
                    : `<p class="chat">${content}</p>`;
                
                if (isMyMessage) {
                    // 관리자 본인이 보낸 메시지
                    li.innerHTML = `
                        <span class="chatDate">${sentAt}</span>
                        ${messageHTML}
                    `;
                } else {
                    // 사용자가 보낸 메시지
                    li.innerHTML = `
                        <img src="${selectTargetProfile || '/svg/person.svg'}">
                        <div>
                            <b>${selectTargetName}</b>
                            <div style="display:flex; gap:8px; align-items:flex-end;">
                                ${messageHTML}
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

function handleFilterClick(e) {
    document.querySelectorAll('.header_nav_button > button').forEach(btn => btn.classList.remove('active'));
    e.target.classList.add('active');
    filterType = e.target.classList.contains('notread') ? 'notread' : 'all';
    currentPage = 1;
    selectRoomList();
}

function triggerFileInput() {
    let fileInput = document.getElementById('chattingFile') || document.createElement('input');
    console.log('이미지 가져오기')
    console.log('fileInput 상태:', fileInput.id ? '기존 존재' : '새로 생성');
    
    if (!fileInput.id) {
        fileInput.type = 'file'; 
        fileInput.id = 'chattingFile'; 
        fileInput.accept = 'image/*'; 
        fileInput.style.display = 'none';
        document.body.appendChild(fileInput);
    }
    
    fileInput.onchange = (e) => {
        const file = e.target.files[0];
        console.log('선택된 파일:', file);
        
        const reader = new FileReader();        

        reader.onload = (ev) => {
            console.log('파일 읽기 완료, 크기:', ev.target.result.length);
            const obj = { 
                senderNo: loginMemberNo, 
                targetNo: selectTargetNo, 
                chattingNo: selectChattingNo, 
                messageContent: '[이미지]', 
                fileData: ev.target.result, 
                isFile: true 
            };
            console.log('전송할 객체:', obj);
            
            if (chattingSock?.readyState === 1) {
                chattingSock.send(JSON.stringify(obj));
                console.log('웹소켓으로 전송 완료');
            } else {
                console.error('웹소켓 연결 안됨:', chattingSock?.readyState);
            }
        };
        reader.readAsDataURL(file);
    };
    
    fileInput.click();
}

// ========== 채팅방 목록에서만 삭제 (프론트엔드) ==========
function deleteChattingRoom(roomId, event) {
    event.stopPropagation(); // 부모 li 클릭 이벤트 방지
    
    if (!confirm('목록에서 이 채팅방을 삭제하시겠습니까?')) {
        return;
    }
    
    // 해당 채팅방 li 요소 찾기
    const roomElement = document.querySelector(`[data-chat-no="${roomId}"]`);
    
    if (roomElement) {
        // DOM에서 제거
        roomElement.remove();
        
        // 삭제된 방이 현재 선택된 방이면 초기화
        if (selectChattingNo === roomId) {
            selectChattingNo = null;
            selectTargetNo = null;
            document.querySelector('.display-chatting').innerHTML = '';
        }
    }
}

// ========== 관리자용 메시지 검색 로직 (수정 완료) ==========
let searchResults = [];      // 검색된 결과 요소들
let currentSearchIndex = -1; // 현재 위치

// 1. 메시지 검색 함수
function searchMessage() {
    // HTML의 ID에 맞춰 수정 (messageSearchInput)
    const input = document.getElementById('messageSearchInput');
    const keyword = input.value.trim();
    const chatDisplay = document.querySelector('.display-chatting');
    const searchCountSpan = document.getElementById('searchCount');

    // 초기화
    searchResults = [];
    currentSearchIndex = -1;
    
    if (!chatDisplay) return;

    // 모든 메시지에서 기존 하이라이트 초기화
    const allMessages = chatDisplay.querySelectorAll('.chat');
    allMessages.forEach(msg => {
        msg.style.backgroundColor = '';
        msg.style.color = ''; 
        msg.style.boxShadow = '';
    });

    if (keyword === "") {
        searchCountSpan.innerText = "0 / 0";
        return;
    }

    // 키워드가 포함된 메시지 찾기 (중요: .chat 클래스를 가진 요소만)
    allMessages.forEach(msg => {
        if (msg.innerText.includes(keyword)) {
            searchResults.push(msg);
        }
    });

    if (searchResults.length > 0) {
        // 검색 결과가 있으면 첫 번째 결과(가장 오래된 메시지) 혹은 마지막 결과 선택
        // 여기서는 가장 최근 메시지(마지막 인덱스)부터 보여주도록 설정
        currentSearchIndex = searchResults.length - 1; 
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
    if (searchCountSpan) {
        if (searchResults.length > 0) {
            searchCountSpan.innerText = `${currentSearchIndex + 1} / ${searchResults.length}`;
        } else {
            searchCountSpan.innerText = "0 / 0";
        }
    }
}

// 3. 해당 위치로 스크롤 및 하이라이트
function scrollToSearchResult() {
    if (currentSearchIndex < 0 || currentSearchIndex >= searchResults.length) return;

    // 이전 강조 효과 제거 (선택된 것만 더 진하게 표시하기 위해)
    searchResults.forEach(el => {
        el.style.backgroundColor = '#fff3cd'; // 일반 검색 결과 배경 (연한 노랑)
        el.style.color = '#333';
        el.style.boxShadow = 'none';
    });

    const target = searchResults[currentSearchIndex];
    
    // 현재 선택된 타겟 강조 (강한 노랑)
    target.style.backgroundColor = '#ffc107'; 
    target.style.color = '#000';
    target.style.boxShadow = '0 0 8px rgba(0,0,0,0.2)';
    
    // 해당 위치로 부드럽게 이동
    target.scrollIntoView({ behavior: 'smooth', block: 'center' });
    
    updateSearchUI();
}

// 4. 이전 결과 (위로 이동 = 인덱스 감소)
function prevSearchResult() {
    if (searchResults.length === 0) return;
    currentSearchIndex--;
    if (currentSearchIndex < 0) currentSearchIndex = searchResults.length - 1; 
    scrollToSearchResult();
}

// 5. 다음 결과 (아래로 이동 = 인덱스 증가)
function nextSearchResult() {
    if (searchResults.length === 0) return;
    currentSearchIndex++;
    if (currentSearchIndex >= searchResults.length) currentSearchIndex = 0; 
    scrollToSearchResult();
}


