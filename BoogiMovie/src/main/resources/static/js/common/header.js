document.addEventListener("DOMContentLoaded", () => {
    const searchList = document.querySelector(".popular_search_list");
    let items = searchList.querySelectorAll("li");

    const itemHeight = 20; // li 하나 높이
    let currentIdx = 0;

    const firstItemClone = items[0].cloneNode(true);
    searchList.appendChild(firstItemClone);

    setInterval(() => {
        currentIdx++;

        searchList.style.transition = "transform 0.5s ease-in-out";
        searchList.style.transform = `translateY(-${currentIdx * itemHeight}px)`;

        if (currentIdx === items.length) {

            setTimeout(() => {
                searchList.style.transition = "none"; // 애니메이션 끄기
                searchList.style.transform = "translateY(0)"; // 처음으로 이동
                currentIdx = 0; // 인덱스 초기화
            }, 500);
        }
    }, 3000); // 3초마다 실행

    /* ============================================================================ */
    /* ============================================================================ */

    /* 통합검색 */

    const searchInput = document.querySelector('.search_input input'); // 검색바
    const searchDropdown = document.querySelector('.search_dropdown'); // 결과 박스

    searchInput.addEventListener('input', function (e) {
        const keyword = e.target.value.trim();

        /* 검색어 없으면 결과 박스 숨김 */
        if (keyword.length === 0) {
            searchDropdown.style.display = 'none';
            return;
        }

        fetch(`search?query=${keyword}`)
            .then(response => response.json())
            .then(data => {
                renderSearchData(data);

                searchDropdown.style.display = 'flex';
            })
            .catch(error => {
                console.error('검색 실패', error);
            });
    });


    /* ============================================================================ */
    /* ============================================================================ */

    function renderSearchData(data) {
        searchDropdown.innerHTML = '';
        let hasResult = false; // 결과가 하나라도 있는지 체크용

        /* 도서가 있는 경우 */
        if (data.books && data.books.length > 0) {
            const categoryHtml = makeCategoryHtml('도서', data.books);
            searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
            hasResult = true;
        }

        // /* 중고 도서가 있는 경우 */
        // if (data.usedBooks && data.usedBooks.length > 0) {
        //     const categoryHtml = makeCategoryHtml('중고도서', data.usedBooks);
        //     searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
        //     hasResult = true;
        // }

        /* 영화가 있는 경우 */
        if (data.movies && data.movies.length > 0) {
            const categoryHtml = makeCategoryHtml('영화', data.movies);
            searchDropdown.insertAdjacentHTML('beforeend', categoryHtml);
            hasResult = true;
        }

        // 데이터가 없는 경우
        if (!hasResult) {
            searchDropdown.innerHTML = '<div style="padding:20px; text-align:center;">검색 결과가 없습니다.</div>';
        }
    }

    function makeCategoryHtml(categoryTitle, items) {
        let itemsHtml = '';
        items.forEach(item => {
            itemsHtml += `
            <a href="#" class="category_a">
                <div class="dropdown_item">
                    <div class="dropdown_img">
                        <img src="${item.img}" alt="이미지">
                    </div>
                    <div class="dropdown_item_name">${item.title}</div>
                </div>
            </a>
        `;
        });

        return `
        <div class="search_category">
            <div class="dropdown_category bold font-18px">${categoryTitle}</div>
            <div class="search_result_container">
                ${itemsHtml}
            </div>
        </div>
    `;
    }

});

