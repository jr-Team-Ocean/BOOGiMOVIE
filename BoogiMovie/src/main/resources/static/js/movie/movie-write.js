console.log('movie.js loaded....')

const form = document.getElementById("writeFrm");
const fileInput = document.getElementById("movieImg");
const previewImg = document.getElementById("add-img");

document.addEventListener("DOMContentLoaded", () => {

    // 이미지 선택 시 미리보기 + 파일 체크
    fileInput.addEventListener("change", () => {
        const file = fileInput.files?.[0];
        if (!file) return;

    // 타입 검사
    if (!file.type.startsWith("image/")) {
        alert("이미지 파일만 업로드할 수 있어요.");
        fileInput.value = "";
        previewImg.src = "/svg/ImageAdd.svg"; // 기본 이미지로 복귀
        return;
    }

    // (선택) 용량 제한 5MB
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
        alert("이미지는 5MB 이하만 업로드할 수 있어요.");
        fileInput.value = "";
        previewImg.src = "/svg/ImageAdd.svg";
        return;
    }

    // 미리보기
    const reader = new FileReader();
    reader.onload = (e) => {
        previewImg.src = e.target.result;
    };
    reader.readAsDataURL(file);
});

    // 제출 전 검증(이미지 필수 포함)
    form.addEventListener("submit", (e) => {
        // 1) 이미지 필수
        const file = fileInput.files?.[0];
        if (!file) {
            alert("대표이미지는 필수입니다.");
            e.preventDefault();
            return;
        }
        if (!file.type.startsWith("image/")) {
            alert("이미지 파일만 업로드할 수 있어요.");
            e.preventDefault();
            return;
        }

        // 2) 제목
        const title = form.querySelector('input[name="productTitle"]')?.value?.trim();
        console.log(title);
        if (!title) {
            alert("영화제목은 필수입니다.");
            e.preventDefault();
            title.focus();
            return;
        }

        // 3) 개봉일
        const productDate = form.querySelector('input[name="productDate"]')?.value;
        if (!productDate) {
            alert("개봉일은 필수입니다.");
            e.preventDefault();
            productDate.focus();
            return;
        }

        // 4) 카테고리 선택(movieType/genreType)
        const movieType = form.querySelector('select[name="movieType"]')?.value;
        const genreType = form.querySelector('select[name="genreType"]')?.value;
        if (!movieType) {
            alert("국내/해외 카테고리를 선택해주세요.");
            e.preventDefault();
            movieType.focus();
            return;
        }
        if (!genreType) {
            alert("장르를 선택해주세요.");
            e.preventDefault();
            genreType.focus();
            return;
        }

        // 5) 상영시간
        const movieTimeStr = form.querySelector('input[name="movieTime"]')?.value;
        const movieTime = Number(movieTimeStr);
        if (!movieTimeStr || Number.isNaN(movieTime) || movieTime <= 0) {
            alert("상영시간(분)을 1 이상으로 입력해주세요.");
            e.preventDefault();
            movieTimeStr.focus();
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
            e.preventDefault();
            priceStr.focus();
            return;
        }
    });
});
