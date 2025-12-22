document.addEventListener("DOMContentLoaded", function () {
    const mainCategory = document.querySelector(".category-type");
    const subCategory = document.querySelector(".category-type-sub");

    mainCategory.addEventListener("change", function () {
        if (this.value === "novel") {
            subCategory.style.display = "inline-block";
        } else {
            subCategory.style.display = "none";
            subCategory.selectedIndex = -1;
        }
    });
});