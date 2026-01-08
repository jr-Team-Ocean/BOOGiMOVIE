
document.addEventListener('DOMContentLoaded', function() {
    
    
    
    
    const bookStatus_Guide = document.querySelector('.bookStatus_Guide')
    const rightArea = document.querySelector('.right_area')
    const arrow_up = document.querySelector('.arrow_up')
    const arrow_down = document.querySelector('.arrow_down')

    
    const form = document.querySelector('#enrollFrm')
    const selectCategory = document.getElementById('usedBook_category')
    const selectSmallCategory = document.querySelector('.select_small_category')
    const categoryId = document.querySelector('#categoryId')
    
    const ubookTitle = document.getElementById('title')
    const writer = document.getElementById('writerName')
    const publisher = document.getElementById('publisherName')
    const nbprice = document.getElementById('nprice')
    const ubprice = document.getElementById('uprice')
    const uContent = document.getElementById('ubookIntro')
    const ubIndex = document.getElementById('uIndex')
    const auIntro = document.getElementById('aIntro')
    
    const preview = document.getElementById('preview')
    const inputImage = document.getElementById('thumbnailImage')
    const deleteImg = document.getElementById('deleteImage')
    
    


    
    document.querySelectorAll('textarea').forEach(textarea => {
                textarea.addEventListener('input', function() {
                    this.style.height = 'auto';
                    this.style.height = this.scrollHeight + 'px';
    
                    
                });
            });
    
    
    rightArea.addEventListener('click', function() {
        
        
        
        bookStatus_Guide.classList.toggle('active');
        
        arrow_up.classList.toggle('active')
        arrow_down.classList.toggle('active')
        
    
    })
    
    if(selectCategory){


        selectCategory.addEventListener('change', function() {
        
            const selectedOption = this.value
        
            // 세부카테고리 펼치기
            if(selectedOption === "11"){
                selectSmallCategory.classList.add('active')
        
                categoryId.value = selectedOption;
        
                console.log('active')
        
        
        
            } else {
                selectSmallCategory.classList.remove('active')
                
                categoryId.value = ''
            }
        })

    }
    

    

    if(preview && inputImage) {

        inputImage.addEventListener('change', e=>{
            const file = e.target.files[0];
    
            if(file != undefined){
                const reader = new FileReader();
    
                reader.readAsDataURL(file);
    
                reader.onload = e => {
                    preview.setAttribute("src", e.target.result)
                }
            }
        })
    }


    if(preview && inputImage && deleteImg){

        deleteImg.addEventListener('click', e=> {
            if(preview.getAttribute("src") != ''){
                preview.src = ''
    
                inputImage.value = '';
            }
        })

    }


    // js 재사용시 대비
    if(form) {


        form.addEventListener('submit', e => {
    
            // 세부 카테고리 선택
            if(selectCategory.value === "11"){
    
                if(!selectSmallCategory.value){
    
                    alert("세부카테고리를 선택해주세요!");
                    
                    e.preventDefault();
                    
                    return;
                    
                }
    
                categoryId.value = selectSmallCategory.value
    
                console.log("1번 카테 : " + categoryId.value)
    
                }else{
    
                    categoryId.value = selectCategory.value
        
                    console.log(categoryId.value)
    
            }
    
    
            if(ubookTitle.value.trim()===''){
                alert("제목을 입력하세요.");
                e.preventDefault();
                ubookTitle.focus();
                ubookTitle.value = "";
                return
    
            }

            if(!inputImage.files || inputImage.files.length === 0){
                alert("이미지를 삽입하세요.")
                e.preventDefault();
                inputImage.focus();
                
                return
            }

    
            if(writer.value.trim()===''){
                alert("작가명을 입력하세요.");
                e.preventDefault();
                writer.focus();
                writer.value = "";
                return
    
            }
    
            if(publisher.value.trim()===''){
                alert("출판사명을 입력하세요.");
                e.preventDefault();
                publisher.focus();
                publisher.value = "";
                return
    
            }
    
            if(nbprice.value.trim()===''){
                alert("정가를 입력하세요.");
                e.preventDefault();
                nbprice.focus();
                nbprice.value = "";
                return
    
            }
    
            if(ubprice.value.trim()===''){
                alert("판매가 입력하세요.");
                e.preventDefault();
                ubprice.focus();
                ubprice.value = "";
                return
    
            }
    
            if(uContent.value.trim()===''){
                alert("책소개를 입력하세요.");
                e.preventDefault();
                uContent.focus();
                uContent.value = "";
                return
    
            }
    
            if(ubIndex.value.trim()===''){
                alert("목차를 입력하세요.");
                e.preventDefault();
                ubIndex.focus();
                ubIndex.value = "";
                return
    
            }
    
            
            if(auIntro.value.trim()===''){
                alert("저자소개를 입력하세요.");
                e.preventDefault();
                auIntro.focus();
                auIntro.value = "";
                return
    
            }
    
    
    
        })

    }



    




})



// 취소 시 이전페이지로 이동
document.querySelector('.cancel_btn').addEventListener('click', () => {
    history.back(); // 이전 페이지로
});
