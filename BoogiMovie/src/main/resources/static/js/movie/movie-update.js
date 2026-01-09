console.log('movie.js loaded....')

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateFrm");
    const fileInput = document.getElementById("movieImg");
    const previewImg = document.getElementById("add-img"); // 너가 label 안에 둔 img

    const parentId = form.querySelector('select[name="parentId"]');   // 100/200
    const genreId  = form.querySelector('select[name="categoryId"]'); // 101~ / 201~

    // 이미지 처리
    fileInput.addEventListener("change", function (e) {
        
        // 파일 1개
        const file = e.target.files[0];

        // 파일 선택한 경우
        if (file != undefined) {

            const reader = new FileReader();
            reader.readAsDataURL(file);

            reader.onload = function (e) {
                previewImg.setAttribute("src", e.target.result);
            };

        } 
        // 취소
        else {
            previewImg.removeAttribute("src");
            previewImg.value = "";
        }
    });

    // 제출 전 검증(이미지 필수 포함)
    form.addEventListener("submit", (e) => {
        // 1) 이미지 필수
        if (fileInput.files.length === 0 && !previewImg.getAttribute("src")) {
            alert("대표 이미지를 등록해주세요.");
            e.preventDefault();
            return;
        }

        // 2) 제목
        const title = form.querySelector('input[name="productTitle"]')?.value?.trim();
        if (!title) {
            alert("영화제목은 필수입니다.");
            form.querySelector('input[name="movieTitle"]').focus();
            e.preventDefault();
            return;
        }

        // 3) 개봉일
        const productDate = form.querySelector('input[name="productDate"]');
        if (!productDate.value) {
            alert("개봉일은 필수입니다.");
            productDate.focus();
            e.preventDefault();
            return;
        }

        // 4) 카테고리 선택(movieType/genreType)
        
         // 1) 국내/해외
        if (!parentId.value) {
            alert("국내/해외 카테고리를 선택해주세요.");
            parentSel?.focus();
            e.preventDefault();
            return;
        }

        // 2) 장르
        if (!genreId.value) {
            alert("장르를 선택해주세요.");
            genreSel?.focus();
            e.preventDefault();
            return;
        }

        // 5) 상영시간
        const movieTimeStr = form.querySelector('input[name="movieTime"]')?.value;
        const movieTime = Number(movieTimeStr);
        if (!movieTimeStr || Number.isNaN(movieTime) || movieTime <= 0) {
            alert("상영시간(분)을 1 이상으로 입력해주세요.");
            form.querySelector('input[name="movieTime"]').focus();
            e.preventDefault();
            return;
            }

        // 6) 관람등급
        const rating = form.querySelector('input[name="filmRating"]:checked')?.value;
        if (!rating) {
            alert("관람등급을 선택해주세요.");
            e.preventDefault();
            return;
        }

        // 7) 판매가
        const priceStr = form.querySelector('input[name="productPrice"]')?.value;
        const price = Number(priceStr);
        if (priceStr === "" || Number.isNaN(price) || price < 0) {
            alert("판매가는 0 이상으로 입력해주세요.");
            form.querySelector('input[name="productPrice"]').focus();
            e.preventDefault();
            return;
        }
    });
});
