console.log('chatting_manager.js')

// 버튼 이벤트용 변수 
const sendBtn = document.getElementById('sendBtn');

document.addEventListener('DOMContentLoaded', () =>{

    // 문서 로딩시 3가지 수행 (목록, 채팅방, 채팅 알림 클릭)
    roomListAddEvent();

    sendBtn.addEventListener('click', sendMessage)

    const params = URLSearchParams(location.search)
    const chatNo = params.get('chat-no')
    const chattingItem = document.querySelector('.chatting-item')
    if(chatNo !=null){
    chattingItem.forEach( item => {
        if(item.getAttribute('chat-no') == chatNo){
            item.click()
            return;
        }
    })

    return;
    }

})

// 전역변수 선언 
let selectChattingNo;
let selectTargetNo;
let selectTargetName;
let selectTargetProfile;


// 목록 로딩 및 이벤트

function RoomListAddEvent(){
    const chattingItemList = document.getElementsByClassName('chatting-item');

    for (let item of chattingItemList) {
        item.addEventListener('click', e=> {

            selectChattingNo = item.getAttribute('chat-no') 
            selectTargetNo = item.getAttribute('target-no')
            selectTargetProfile = item.children[0].children[0].getAttribute('src')
            selectTargetName = item.childrend[1].childrend[0].childrend[1].innerText;

            // 알림이 있는 경우 삭제 
            if (item.childrend[1].children[1].childrend[1] != undefined) {
               item.childrend[1].children[1].children[1].remove();
            }

            // 채팅방에서 해당된 부분만 select 추가 
            item.classList.add('select')
            for (let it of chattingItemList) it.classList.remove('select')

            // 채팅방 목록 조회
            selectRoomList();

            // 비동기로 메세지 목록 조회
            selectmessageList();
        })
    }

}

// 비동기로 채팅방 목록 조회

function selectRoomList {
    fetch('/chatting/roomList')
        .then(resp => resp.JSON)
        .then(roomList => {
            const chattingList = document.querySelector('.chatting_list')
            chattingList.innerHTML = '';

            for (let room of roomList) {
                const li = document.createElement('li')
                li.classList.add('chatting-item')
                li.setAttribute('chat-no', room.chattingNo)

                if (room.chattingNo = selectChattingNo) {
                    li.classList.add('select')
                }
                
                // item-header 부분

                const itemHeader = document.createElement('div')
                itemHeader.classList.add('item-header')

                const listProfile = document.createElement('img')
                listProfile.classList.add('list-profile')

                if (room.targetProfile == undefined) {
                    listProfile.setAttribute('src', '/resources/images/user.png')
                }else {
                    listProfile.setAttribute('src', room.targetProfile);
                }

                itemHeader.append(listProfile)

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

                p.append(targetName, recentSendTime)

                const div = document.createElement('div')

                const recentMessage = document.createElement('p')
                recentMessage.classList.add('recent-message')

                if (room.lastMessage != undefined) {
                    recentMessage.innerHTML = room.lastMessage
                }

                div.append(recentmessage);

                itemBody.append(p, div);

                // 보고 있는 채팅방과 아닌 채팅방을 구분하여 메세지 왔을때 개수 표시
                if (room.notReadCount > 0 && room.chattingNo != selectChattingNo){
                    const notReadCount = document.createElement('p')
                    notReadCount.classList.add('not-read-count')
                    notReadCount.innerText = room.notReadCount;
                }else {
                    setTimeout(() => {
                        fetch('/chatting/updateReadFlag', {
                            method: 'PUT',
                            headers: {'content-Type': 'application/json'},
                            body: JSON.stringify({
                                'memberNo': loginMemberNo,
                                'chattingNo' : selectChattingNo
                            })
                        })
                            .then(resp => resp.text())
                            .then(result => {
                                console.log(result)
                            })
                            .catch(err => {console.log(err)})
                    }, 200)
                }
                li.append(itemHeader, itemBody)
                chattingList.append(li)


            }
            roomListAddEvent();
        })
        .catch(err => console.log(err))

}

// 비동기로 메세지 목록을 조회하는 함수
function selectMessageList() {
    fetch("/chatting/selectMessageList?chattingNo=" + selectChattingNo + "&memberNo" + loginMemberNo)
        .then(resp => resp.json())
        .then(messageList => {
            const ul = document.querySelector('display-chatting')  
            ul.innerHTML = '';

            for (let msg of messageList){

                const li = document.createElement('li')

                // 보낸 시간 
                const span = document.createElement('span') 
                span.classList.add('chatDate')
                span.innerText = msg.sendTime

                // 메세지 내용
                const p = document.createElement('p')
                p.classList.add('chat')
                p.innerText = msg.messageContent;

                // 누구 발송인지 확인
                if (loginMemberNo == msg.senderNo) {
                    li.classList.add('my-chat')
                    li.append(span, p)

                }else {
                    li.classList.add('target-chat')

                    // 상대  프로필 
                    const img = document.createElement('img')
                    img.setAttribute('src', selectTargetProfile)

                    const div = document.createElement('div')

                    // 상대 이름 
                    const b = document.createElement('b')
                    b.innerText = selectTargetName
                    
                    const br = document.createElement('br')

                    div.append(b, br, p, span)
                    li.append(img, div)
                }
                ul.append(li);

                ul.scrollTop = ul.scrollHeight;
            }
        })
        .catch(err => {console.log(err)})
}



// WebSocket 객체 생성 

let chattingSock;

if (loginMemberNo != '') {
    chattingSock = new SockJS("/chattingSock")
}

// 채팅 입력 

const rightChatBox = document.getElementById('rightChatBox')
const chattingInput = document.getElementById('chattingInput')

const sendMessage = () => {

    if(chattingInput.value.trim().length == 0){
        alert('채팅을 입력해주세요')
        chattingInput.value = '';
    }

    if(chattingInput.value.trim().length > 0 ) {
        var obj = {
            senderNo: loginMemberNo,
            targetNo: selectTargetNo,
            chattingNo: selectChattingNo,
            messageContent: chattingInput.value
        }

        chattingSock.send(JSON.stringify(obj));
        
        // 채팅 알림 보내기 구현

        const url = `${location.pathname}?chat-no=${selectChattingNo}`;        

        sendNotification('chatting', url, selectTargetNo, content)

        chattingInput.value = '';
    }
}

// 엔터 입력시 
sendBtn.addEventListener('keydown', (e)=> {
   
    if(window.e.key=='Enter'){
        if(!e.shiftKey){
            e.preventDefault()
            sendMessage();
        }
    }
})

// 서버에서 메세지 전달 받으면 자동실행 콜백 함수 (채팅창에 표시)

chattingSock.onmessage = e => {

    const msg = JSON.parse(e.data)

    if (selectChattingNo == msg.chattingNo) {

        // 큰틀 
        const ul = document.querySelector('.display-chatting')

        // 개별 메시지 틀 
        const li = document.createElement('li');

        // 개별 메시지 내용 
        const p = document.createElement('span')
        p.innerHTML = msg.messageContent
        p.classList.add('chatContent')

        // 보낸 시간 
        const span = document.createElement('span')
        span.innerText = msg.sendTime;
        span.classList.add('sendTime'); // 채팅 css 구현
        
        // 내가 작성한 메시지의 경우 
        if (loginMemberNo == msg.senderNo){
            li.classList.add('my-chat') // 채팅 css 구현 발송 메시지

            li.append(span, p)
        }else {
            li.classList.add('target-chat') // 채팅 css 구현 수신 메시지

            // 상대 프로필
            const img = document.createElement('img');
            img.setAttribute('src', selectTargetProfile)

            const div = document.createElement('div')

            // 상대 이름 
            const b = document.createElement('b')
            b.innerText = selectTargetName;

            const br = document.createElement('br');

            div.append(b, br, p, span)
            li.append(img, div)
        }

        ul.append(li)

        ul.scrollTop = ul.scrollHeight;
    }

    selectRoomList();
}


// 채팅상대 검색 (ajax)
const targetInput = document.getElementById('targetInput')

targetInput.addEventListener('input', e => {
    const query = e.target.value.trim();

    // 입력값이 없을 경우
    if (query.length == 0){
        resultArea.innerHTML = '';
        return;
    }

    // 입력값이 있을 경우
    fetch('/chatting/selectTarget?query=' + query)
        .then(resp => resp.json())
        .then(list => {
            resultArea.innerHTML ='';

            // 일치하는 회원이 없는 경우
            if (list.length ==0) {
                const li = document.createElement('li')
                li.classList.add('result-row');
                li.innerText = '일치하는 회원이 없습니다.'
                resultArea.append(li)
                return;
            }

            // 일치하는 회원이 있는 경우 

            for (let member of list) {

                const li = document.createElement('li')
                li.classList.add('result-row')
                li.setAttribute('data-id', member.memberNo)

                const img = document.createElement
                img.setAttribute('result-row-img')

                // 프로필 이미지 여부에 따라 src 속성 지정
                if (member.profileImage == null) img.setAttribute('src'), '/resources/images/user.png'
                else img.setAttribute('src', member.profileImage);

                let nickname = member.memberNickname;
                let email = member.memberEmail;

                const span = document.createElement('span')

                span.innerHTML = `${nickname} ${email}`.replaceAll(query, `<mark>${query}</mark`)

                li.append(img, span)
                resultArea.append(li)

                li.addEventListener('click', chattingEnter);
            }

        })
        .catch(err => console.log(err))
})

// 채팅방 입장
function chattingEnter(e) {
    const targetNo = e.currentTarget.getAttribute('data-id');

    fetch('/chatting/enter?targetNo=' + targetNo)
        .then(resp => resp.json())
        .then(chattingNo => {

            // 채팅방 목록 조회
            selectRoomList();

            // 채팅방 목록내 해당 채팅방 존재여부 확인 
            setTimeout(() => {
                const itemList = document.getElementsByClassName('chatting-item');
                for (let item of itemList) {

                    if (chattingNo == item.getAttribute('chat-no')) {
                        
                        targetInput.value = '';
                        resultArea.innerHTML = '';

                        item.click();
                        return;
                    }
                }

            })

        })
}
