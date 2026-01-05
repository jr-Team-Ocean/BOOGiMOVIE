document.addEventListener("DOMContentLoaded", () => {

    const heart = document.querySelector(".heart");
    const emptyHeart = heart.querySelector(".fa-regular");
    const fullHeart = heart.querySelector(".fa-solid");
    const countSpan = heart.nextElementSibling;

    let liked = false; // 현재 좋아요 상태

    heart.addEventListener("click", () => {

        if (!liked) {
            // 좋아요 ON
            emptyHeart.style.display = "none";
            fullHeart.style.display = "inline-block";
            countSpan.textContent = Number(countSpan.textContent) + 1;
        } else {
            // 좋아요 OFF
            emptyHeart.style.display = "inline-block";
            fullHeart.style.display = "none";
            countSpan.textContent = Number(countSpan.textContent) - 1;
        }

        liked = !liked; // 상태 반전
    });
});