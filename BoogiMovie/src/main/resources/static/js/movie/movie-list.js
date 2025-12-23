console.log('movie-list loaded....')

const tabs = document.querySelectorAll('.nav-item')
const movieLists = document.querySelectorAll('.movie-list')
const genreList = document.querySelector('.genre-list')

tabs.forEach(tab => {
    tab.addEventListener('click', ()=>{

        // tab active 처리
        tabs.forEach(t => t.classList.remove('active'))
        tab.classList.add('active')
        
        // 장르 active 처리
        const type = tab.dataset.type;
        genreList.classList.toggle('active', type !== 'all');

        // 콘텐츠 전환
        movieLists.forEach(list => list.classList.remove('active'))
        document.getElementById(type).classList.add('active')

    })
})


