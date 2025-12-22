const chattingContainer = document.querySelector(".chatting_container"); // 채팅 영역
const writeChatting = document.querySelector("#write-chatting"); // 채팅 input
const sendBtn = document.querySelector(".write-chatting-area img"); // 보내기 버튼

function sendMessage() {
    const userMessage = writeChatting.value.trim()

    /* 메세지 없으면 실행 X */
    if(userMessage === "" || userMessage.length === 0) return;

    /* 사용자 메세지 */
    addMessage("user", userMessage);

    /* 채팅 보내면 입력창 비우기 */
    writeChatting.value = "";
    writeChatting.focus();

    /* 임시 */
    setTimeout(() => {
        const botMessage = "테스트으으으";
        /* 챗봇 메세지 */
        addMessage("bot", botMessage);
    }, 1000);
}

/* 화면에 말풍선 그리기 (user / bot) */
function addMessage(who, text) {
    let html = '';

    /* 사용자 메세지 */
    if(who == "user") {
        html = `
            <div class="user-chatting">
                <p class="user-text font-16px">${text}</p>
            </div>
        `;

    } else {
        /* 챗봇 메세지 */
        html = `
            <div class="boogi-chatting">
                <div class="boogi-info">
                    <img src="../../static/images/common/only-logo.png" alt="">
                    <div class="font-18px">부기봇</div>
                </div>
                <p class="bot-text font-16px">${text}</p>
            </div>
        `;
    }

    /* 채팅 영역에 말풍선 추가하기 */
    chattingContainer.innerHTML += html;

    /* 스크롤 항상 하단으로 */
    chattingContainer.scrollTop = chattingContainer.scrollHeight;
}

/* 메세지 전송 버튼 클릭시 */
sendBtn.addEventListener("click", sendMessage); // 메세지 전송

/* 또는 엔터키 입력 시 (그냥 Enter) */
writeChatting.addEventListener("keydown", e => {
    if(e.key === "Enter") {
        e.preventDefault();
        sendMessage();
    }
});