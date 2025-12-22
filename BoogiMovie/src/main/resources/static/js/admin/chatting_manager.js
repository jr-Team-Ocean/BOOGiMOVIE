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





// WebSocket 객체 생성 

let chattingSock;

if (loginMemberNo != '') {
    chattingSock = new SockJS("/chattingSock")
}

// 채팅 입력 

const rightChatBox = document.getElementById('rightChatBox')
const chattingInput = document.getElementById('chattingInput')
const sendBtn = document.getElementById('sendBtn')

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

        const url = ~~~~~~
        const content = ~~~~~~

        sendNotification('chatting', url, selectTargetNo, content)

        chattingInput.value = '';
    }
}

// 엔터 입력시 
sendBtn.addEventListener('keydown', (e)=> {
   
    if(window.e.key=='Enter'){
        if(!e.shiftKey){
            e.preventDefault()
            sendMessage()
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

