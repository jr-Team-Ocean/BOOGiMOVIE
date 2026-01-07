/* 인기검색어 비동기 요청 */
document.addEventListener("DOMContentLoaded", () => {
    fetch("/search/rank")
        .then(resp => resp.json())
        .then(keywords => {
            console.log(keywords);

            renderTicker(keywords);
        })
        .catch(e => console.log(e))

    function renderTicker(keywords) {
        const list = document.querySelector(".popular_search_list");

        // 데이터가 없으면 종료
        if (!keywords || keywords.length === 0) {
            list.innerHTML = "<li>데이터 없음</li>";
            return;
        }

        // 기존 내용 비우기
        list.innerHTML = "";

        // 리스트 만들기 (1. 자바, 2. 스프링...)
        keywords.forEach((word, index) => {
            const li = document.createElement("li");
            li.textContent = `${index + 1}. ${word}`; // 순위 표시

            list.appendChild(li);
        });

        // 데이터가 2개 이상일 때만 롤링 시작
        if (keywords.length > 1) {
            startRollingAnimation(list);
        }
    }

    /* 롤링 함수 */
    function startRollingAnimation(list) {
    const items = list.querySelectorAll("li");
    const itemHeight = 20;
    let currentIdx = 0;

    const clone = items[0].cloneNode(true);
    list.appendChild(clone);

    // 3초마다 움직이기
    setInterval(() => {
        currentIdx++;

        list.style.transition = "transform 0.5s ease-in-out";
        list.style.transform = `translateY(-${currentIdx * itemHeight}px)`;

        // 마지막(복사본)에 도달했으면 바로 맨 위로 위치 초기화
        if (currentIdx === items.length) {
            setTimeout(() => {
                list.style.transition = "none"; // 애니메이션 끄고
                list.style.transform = "translateY(0)"; // 0번 위치로 순간이동
                currentIdx = 0;
            }, 500); // 0.5초
        }
    }, 3000);
}
})