document.querySelector('.category_case').addEventListener('click', function() {

            const miniCategory = document.querySelectorAll('.mini_category')

            //console.log(miniCategory)

            //console.log(miniCategory[0])

            for (i in miniCategory){
                if(i < miniCategory.length){

                    
                    miniCategory[i].classList.toggle('active')

                }
            }

        })