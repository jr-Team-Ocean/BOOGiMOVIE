console.log('movie-list loaded....');

/* =========================
 * URL 파라미터 유틸
 * ========================= */
function getParams() {
    return new URLSearchParams(window.location.search);
}

function normalizeParams(params) {
    if (!params.get("sort")) params.set("sort", "latest");
    if (!params.get("page")) params.set("page", "1");

    // category가 0이면 제거(전체 탭 의미)
    const cat = params.get("category");
    if (!cat || cat === "0") params.delete("category");

    // categoryIds 공백이면 제거
    const cids = params.get("categoryIds");
    if (!cids || cids.trim() === "") params.delete("categoryIds");

    const q = params.get("query");
    if (!q || q.trim() === "") params.delete("query");

    return params;
}

function buildUrl(params) {
    normalizeParams(params);
    
    // 현재 페이지의 경로 유지 ("/movies"로 고정X)
    const qs = params.toString();
    return qs ? `${location.pathname}?${qs}` : `${location.pathname}`;
}

function goWith(params) {
    window.location.href = buildUrl(params);
}

/* =========================
 * 기존 UI용 변수
 * ========================= */
const tabs = document.querySelectorAll('.nav-item');
const genreList = document.querySelector('.genre-list');

/* =========================
 * 1) 탭(국내/해외/전체) 클릭:
 *    - UI 전환도 하고
 *    - sort/query 유지 + page=1 + category 변경 이동
 * ========================= */
tabs.forEach((tabEl) => {
    tabEl.addEventListener('click', (e) => {
        const nation = e.currentTarget.dataset.nation;
        const params = getParams();

        // 탭 변경 시 전체+장르 IN 검색 흔적 제거
        params.delete("categoryIds");

        // 전체 탭이면 category=0(=삭제 처리됨), 국내/해외면 100/200
        params.set("category", nation);
        params.set("page", "1");
        goWith(params);
    });
});


/* =========================
 * 2) 장르 클릭: sort/query 유지 + page=1 + category 변경
 * ========================= */
const genreBtns = document.querySelectorAll(".genre-btn");

genreBtns.forEach(btn => {
    btn.addEventListener("click", () => {
        const params = getParams();

        const currentCategory = params.get("category") || "0";
        const hasCategoryIds = !!params.get("categoryIds");
        const isAllTab = (currentCategory === "0") || hasCategoryIds;

        const catNum = Number(currentCategory);
        const isForeignTab = !isAllTab && catNum >= 200 && catNum < 300;


        const domesticId = btn.dataset.domestic; // 101~119
        const foreignId  = btn.dataset.foreign;  // 201~219

        if (!domesticId || !foreignId) return;

        // 전체 탭: 국내+해외 장르 둘 다
        if (isAllTab) {
            params.delete("category");
            params.set("categoryIds", `${domesticId},${foreignId}`);
            params.set("page", "1");
            console.log("ALL =>", params.toString());
            goWith(params);
            return;
        }

        // 국내/해외 탭: 해당 탭에 맞는 장르 1개만
        const nextCategory = isForeignTab ? foreignId : domesticId;
        // if (!nextCategory) return;

        params.delete("categoryIds"); // IN 조건 제거
        params.set("category", nextCategory);
        params.set("page", "1");

        console.log("TAB =>", params.toString());
        goWith(params);
    });
});

/* =========================
 * 3) 정렬 변경: category/query 유지 + page=1
 * ========================= */
const sortSelect = document.getElementById("list-sort");
if (sortSelect) {
    sortSelect.addEventListener("change", () => {
        const params = getParams();
        params.set("sort", sortSelect.value);
        params.set("page", "1");
        goWith(params);
    });
}

/* =========================
 * 4) 검색: category/sort 유지 + page=1
 * ========================= */
const searchInput = document.getElementById("mini-search-input");
const searchBtn = document.getElementById("mini-search-btn");

function doSearch() {
    const params = getParams();
    const q = (searchInput?.value || "").trim();

    if (q) params.set("query", q);
    else params.delete("query");

    params.set("page", "1");
    goWith(params);
}

if (searchBtn) searchBtn.addEventListener("click", doSearch);
if (searchInput) {
    searchInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") doSearch();
    });
}

/* =========================
 * 5) 페이지네이션 클릭: page만 변경 + 나머지 유지
 * ========================= */
document.addEventListener("click", (e) => {
    const a = e.target.closest(".pagination-link");
    if (!a) return;

    if (a.getAttribute("href") === "#") e.preventDefault();

    const page = a.dataset.page;
    if (!page) return;

    const params = getParams();
    params.set("page", page);
    goWith(params);
});

/* =========================
 * 6) 글쓰기 버튼 이동 (기존)
 * ========================= */
const writeBtn = document.getElementById('write-btn');
    
writeBtn.addEventListener('click', () => {
    location.href = '/movies/write';
});

