console.log('movie-list loaded....')

const tabs = document.querySelectorAll('.nav-link')
const items = document.querySelectorAll('.nav-item')
const lists = document.querySelectorAll('.movie-list')

tabs.forEach(tab => {
    tab.addEventListener('click', ()=>{
        
        // tab active 처리
        tabs.forEach(t => t.classList.remove('active'))
        tab.classList.add('active')

        // 콘텐츠 전환
        lists.forEach(list => list.classList.remove('active'))
        document.getElementById(tab.dataset.target).classList.add('active')
    })
})

items.forEach(item => {
    item.addEventListener('click', ()=>{
        items.forEach(i => i.classList.remove('active'))
        item.classList.add('active')
    })
})
