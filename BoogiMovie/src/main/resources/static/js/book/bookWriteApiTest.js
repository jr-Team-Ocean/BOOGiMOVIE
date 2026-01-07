document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("writeFrm");
    const isbnInput = document.querySelector(".book-isbn");

    form.addEventListener("submit", e => {e.preventDefault();

        const isbn = isbnInput.value.trim();
        if (!isbn) {
            alert("ISBN을 입력해주세요.");
            return;
        }

        fetch(`/books/write/api?isbn=${encodeURIComponent(isbn)}`, {
            method: "POST"
        })
        .then(resp => {

            // 이미 등록된 ISBN
            if (resp.status === 409) {
                alert("이미 등록된 도서입니다.");
                return;
            }

            if (!resp.ok) {
                alert("도서 등록에 실패했습니다.");
                return;
            }

            alert("도서 등록이 완료되었습니다.");
            isbnInput.value = "";
        })
        .catch(() => alert("요청 처리 중 오류가 발생했습니다."));
    });

});
