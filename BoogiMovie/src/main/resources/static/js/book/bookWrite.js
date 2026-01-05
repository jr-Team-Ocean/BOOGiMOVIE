console.log("boardDetail.js loaded");
document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("writeFrm");
    
    // 제목
    const title = document.querySelector(".book-title");
    
    // 작가
    const writer = document.querySelector(".book-writer");
    
    // 출판사
    const maker = document.querySelector(".book-maker");
    
    // 출간일
    const date = document.querySelector(".book-date");

    // 가격
    const price = document.querySelector(".book-price");
    
    // 재고
    const stock = document.querySelector(".book-stock");
    
    // 소개
    const content = document.querySelector(".book-info");

    // 이미지
    const imageInput = document.querySelector(".book-img");
    const imagePreview = document.getElementById("add-img");



    const mainCategory = document.querySelector(".category-type");
    const subCategory = document.querySelector(".category-type-sub");
    
    // 실제 전달될 부분
    const categoryId = document.getElementById("categoryId");

    // 이미지 처리
    imageInput.addEventListener("change", function (e) {
        
        // 파일 1개
        const file = e.target.files[0];

        // 파일 선택한 경우
        if (file != undefined) {

            const reader = new FileReader();
            reader.readAsDataURL(file);

            reader.onload = function (e) {
                imagePreview.setAttribute("src", e.target.result);
            };

        } 
        // 취소
        else {
            imagePreview.removeAttribute("src");
            imageInput.value = "";
        }
    });






    // 서브 카테고리 선택부분 
    mainCategory.addEventListener("change", function () {
        if (this.value === "11") {
            subCategory.style.display = "inline-block";
        } else {
            subCategory.style.display = "none";
            subCategory.selectedIndex = -1;
        }
    });

    // 유효성? 검사
    form.addEventListener("submit", (e) => {

        // 카테고리 담길 값 부분
        if (!mainCategory.value) {
            alert("카테고리를 선택해주세요.");
            mainCategory.focus();
            e.preventDefault();
        return;
        }

        if (mainCategory.value === "11") {

            if (!subCategory.value) {
            alert("세부 카테고리를 선택해주세요.");
            subCategory.focus();
            e.preventDefault();
            return;
            }

            categoryId.value = subCategory.value;
        } else {
            categoryId.value = mainCategory.value;
        }

        // 제목 미작성
        if (title.value.trim() ==="") {
            alert("도서명을 입력해주세요.");
            title.focus();
            e.preventDefault();
            title.value = "";
            return;
        }

        // 작가
        if (writer.value.trim() ==="") {
            alert("작가(저자, 지은이, 글쓴이 등등)를 입력해주세요.");
            writer.focus();
            e.preventDefault();
            writer.value = "";
            return;
        }

        // 출판사
        if (maker.value.trim() ==="") {
            alert("출판사를 입력해주세요.");
            maker.focus();
            e.preventDefault();
            maker.value = "";
            return;
        }

        // 출간일
        if (date.value ==="") {
            alert("출간일을 선택해주세요.");
            date.focus();
            e.preventDefault();

            return;
        }

        // 가격
        if (price.value ==="" || Number(price.value) <= 0) {
            alert("정상적인 가격을 입력해주세요. (0원 이하는 불가능 합니다.)");
            price.focus();
            e.preventDefault();
            price.value = "";
            return;
        }

        // 재고 (공란이면 0으로 보정)
        if (stock.value === "") {
            stock.value = 0;
        } else if (Number(stock.value) < 0) {
            alert("재고는 0 이상이어야 합니다.");
            stock.focus();
            e.preventDefault();
            stock.value = "";
            return;
        }

        // 소개
        if (content.value.trim() === "") {
            alert("책 소개를 입력해주세요.");
            content.focus();
            e.preventDefault();
            content.value = "";
            return;
        }

        // 이미지 필수
        if (imageInput.files.length === 0) {
            alert("대표 이미지를 등록해주세요.");
            e.preventDefault();
            return;
        }



    });

});
