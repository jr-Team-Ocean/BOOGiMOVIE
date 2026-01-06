document.querySelectorAll('textarea').forEach(textarea => {
            textarea.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';

                
            });
        });


        
const bookStatus_Guide = document.querySelector('.bookStatus_Guide')
const rightArea = document.querySelector('.right_area')
const arrow_up = document.querySelector('.arrow_up')
const arrow_down = document.querySelector('.arrow_down')
console.log(rightArea)

rightArea.addEventListener('click', function() {

    
    
    bookStatus_Guide.classList.toggle('active');

    arrow_up.classList.toggle('active')
    arrow_down.classList.toggle('active')
    

})


const selectCategory = document.getElementById('usedBook_category')
const selectSmallCategory = document.querySelector('.select_small_category')

selectCategory.addEventListener('change', function() {

    const selectedOption = this.value

    if(selectedOption == 'drama'){
        selectSmallCategory.classList.add('active')

        console.log('active')
    } else {
        selectSmallCategory.classList.remove('active')
    }
})


// 취소 시 이전페이지로 이동
document.querySelector('.cancel_btn').addEventListener('click', () => {
    history.back(); // 이전 페이지로
});
