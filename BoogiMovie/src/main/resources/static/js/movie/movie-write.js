console.log('movie.js loaded....')

// tag 입력(수정, 등록)
const tagifyNation  = createTagify('input[name=nation]',  ["대한민국", "미국", "일본"]);
const tagifyActor   = createTagify('input[name=actor]',   ["샘 워싱턴", "조 샐다나", "시고니 위버", "스티븐 랭"]);
const tagifyCreator = createTagify('input[name=creator]', ["박찬욱", "류승완", "나홍진", "봉준호", "제임스 카메론"]);
const tagifyCompany = createTagify('input[name=company]', ["롯데", "CJ", "월트디즈니 컴퍼니 코리아"]);

function createTagify(selector, whitelist){
    const input = document.querySelector(selector)
    return new Tagify(input, {
        enforceWhitelist: true,
        whitelist: whitelist
    })
}

[tagifyNation, tagifyActor, tagifyCreator, tagifyCompany].forEach(tagify => {
    tagify
        .on('add', onAddTag)
        .on('remove', onRemoveTag)
        .on('input', onInput)
        .on('edit', onTagEdit)
        .on('invalid', onInvalidTag)
        .on('click', onTagClick)
        .on('focus', onTagifyFocusBlur)
        .on('blur', onTagifyFocusBlur)
        .on('dropdown:hide dropdown:show', e => console.log(e.type))
        .on('dropdown:select', onDropdownSelect)
        .settings.maxTags = 3
})

// tag added callback
function onAddTag(e){
    console.log("onAddTag: ", e.detail);
}

// tag remvoed callback
function onRemoveTag(e){
    console.log("onRemoveTag:", e.detail, "tagify instance value:", tagify.value)
}

// on character(s) added/removed (user is typing/deleting)
function onInput(e){
    const tagify = this;
    const root = tagify.settings.originalInput.closest('.tagify');

    root.classList.add('tagify--loading');

    mockAjax()
        .then(result => {
            tagify.settings.whitelist = result;
        })
        .catch(() => {
            root.classList.remove('tagify--loading');
        });
}

function onTagEdit(e){
    console.log("onTagEdit: ", e.detail);
}

// invalid tag added callback
function onInvalidTag(e){
    console.log("onInvalidTag: ", e.detail);
}

// invalid tag added callback
function onTagClick(e){
    console.log(e.detail);
    console.log("onTagClick: ", e.detail);
}

function onTagifyFocusBlur(e){
    console.log(e.type, "event fired")
}

function onDropdownSelect(e){
    console.log("onDropdownSelect: ", e.detail)
}

// ======================================================
// 이미지 등록
const addImg = document.getElementById('add-img')

addImg.addEventListener('click', ()=>{
    
})