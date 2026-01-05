console.log('movie.js loaded....')

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateFrm");
    const fileInput = document.getElementById("movieImg");
    const previewImg = document.getElementById("add-img"); // 너가 label 안에 둔 img

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


    // // 이미지 선택 시 미리보기 + 파일 체크
    // fileInput.addEventListener("change", (e) => {
    //         const file = e.target.files[0];
    //         if (!file) return;

    //     // 타입 검사
    //     if (!file.type.startsWith("image/")) {
    //         alert("이미지 파일만 업로드할 수 있어요.");
    //         fileInput.value = "";
    //         previewImg.src = "/svg/ImageAdd.svg"; // 기본 이미지로 복귀
    //         return;
    //     }

    //     // (선택) 용량 제한 5MB
    //     const maxSize = 5 * 1024 * 1024;
    //     if (file.size > maxSize) {
    //         alert("이미지는 5MB 이하만 업로드할 수 있어요.");
    //         fileInput.value = "";
    //         previewImg.src = "/svg/ImageAdd.svg";
    //         return;
    //     }

    //     // 미리보기
    //     const reader = new FileReader();
    //     reader.onload = (e) => {
    //         previewImg.src = e.target.result;
    //     };
    //     reader.readAsDataURL(file);
    // });

    // // 제출 전 검증(이미지 필수 포함)
    // form.addEventListener("submit", (e) => {
    //     // 1) 이미지 필수
    //     const file = fileInput.files?.[0];
    //     if (!file) {
    //         alert("대표이미지는 필수입니다.");
    //         e.preventDefault();
    //         return;
    //     }
    //     if (!file.type.startsWith("image/")) {
    //         alert("이미지 파일만 업로드할 수 있어요.");
    //         e.preventDefault();
    //         return;
    //     }

    //     // 2) 제목
    //     const title = form.querySelector('input[name="movieTitle"]')?.value?.trim();
    //     if (!title) {
    //         alert("영화제목은 필수입니다.");
    //         form.querySelector('input[name="movieTitle"]').focus();
    //         e.preventDefault();
    //         return;
    //     }

    //     // 3) 개봉일
    //     const productDate = form.querySelector('input[name="productDate"]')?.value;
    //     if (!productDate) {
    //         alert("개봉일은 필수입니다.");
    //         form.querySelector('input[name="productDate"]').focus();
    //         e.preventDefault();
    //         return;
    //     }

    //     // 4) 카테고리 선택(movieType/genreType)
    //     const movieType = form.querySelector('select[name="movieType"]')?.value;
    //     const genreType = form.querySelector('select[name="genreType"]')?.value;
    //     if (!movieType) {
    //         alert("국내/해외 카테고리를 선택해주세요.");
    //         form.querySelector('select[name="movieType"]').focus();
    //         e.preventDefault();
    //         return;
    //     }
    //     if (!genreType) {
    //         alert("장르를 선택해주세요.");
    //         form.querySelector('select[name="genreType"]').focus();
    //         e.preventDefault();
    //         return;
    //     }

    //     // 5) 상영시간
    //     const movieTimeStr = form.querySelector('input[name="movieTime"]')?.value;
    //     const movieTime = Number(movieTimeStr);
    //     if (!movieTimeStr || Number.isNaN(movieTime) || movieTime <= 0) {
    //         alert("상영시간(분)을 1 이상으로 입력해주세요.");
    //         form.querySelector('input[name="movieTime"]').focus();
    //         e.preventDefault();
    //         return;
    //         }

    //     // 6) 관람등급
    //     const rating = form.querySelector('input[name="filmRating"]:checked')?.value;
    //     if (!rating) {
    //         alert("관람등급을 선택해주세요.");
    //         e.preventDefault();
    //         return;
    //     }

    //     // 7) 판매가
    //     const priceStr = form.querySelector('input[name="productPrice"]')?.value;
    //     const price = Number(priceStr);
    //     if (priceStr === "" || Number.isNaN(price) || price < 0) {
    //         alert("판매가는 0 이상으로 입력해주세요.");
    //         form.querySelector('input[name="productPrice"]').focus();
    //         e.preventDefault();
    //         return;
    //     }
    // });
});
