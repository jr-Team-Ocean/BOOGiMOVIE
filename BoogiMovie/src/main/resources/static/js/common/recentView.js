// 최근 본 콘텐츠 모달로 띄우기
const btnRecent = document.getElementById("btnRecent");
const panel = document.querySelector(".view-contanier");
const floatUI = document.querySelector(".floating-ui");

function setPanelPosition() {
    // floating-ui 실제 너비를 CSS 변수로 전달
    const w = floatUI.getBoundingClientRect().width;
    panel.style.setProperty("--float-w", `${w}px`);
}

function toggleRecentPanel() {
    setPanelPosition();
    panel.classList.toggle("open");
}

btnRecent.addEventListener("click", toggleRecentPanel);

// 창 크기 바뀌면 위치 다시 계산
window.addEventListener("resize", () => {
    if (panel.classList.contains("open")) setPanelPosition();
});

// 바깥 클릭하면 닫기
document.addEventListener("click", (e) => {
    if (!panel.classList.contains("open")) return;

    const isInsidePanel = panel.contains(e.target);
    const isClickBtn = btnRecent.contains(e.target);

    if (!isInsidePanel && !isClickBtn) {
    panel.classList.remove("open");
    }
});

// 전체, 도서, 영화 클릭시 해당 콘텐츠 보여주기
const contentBtn = document.querySelectorAll('.recent-btn')
const recentAll = document.querySelector('.recent-all')
const recentBook = document.querySelector('.recent-book')
const recentMovie = document.querySelector('.recent-movie')

const sections = [recentAll, recentBook, recentMovie];

contentBtn.forEach((btn, i)=>{
    btn.addEventListener('click', ()=>{
        
        // 버튼 select 처리
        contentBtn.forEach(b => b.classList.remove('select'))
        btn.classList.add('select')

        // 전부 숨김
        sections.forEach(c => c.classList.add('colse'))

        // 클릭한 것만 보여주기
        sections[i].classList.remove('colse')
    })
})