console.log('report_form.js')

document.addEventListener('DOMContentLoaded', function() {
    
    const reportForm = document.querySelector('.main_content');
    const textarea = document.querySelector('textarea[name="reportContent"]');
    const charCount = document.querySelector('.count');
    const reportTypes = document.querySelectorAll('.report_title > div');
    const submitBtn = document.querySelector('.submit');

    // 1. 신고 유형 선택 기능 (클릭 시 active 클래스 추가 및 체크 아이콘 표시 제어)
    reportTypes.forEach(type => {
        type.addEventListener('click', function() {
            // 다른 항목들의 선택 해제 (다중 선택 방지 시 사용)
            reportTypes.forEach(item => item.classList.remove('active'));
            
            // 현재 클릭한 항목 토글
            this.classList.add('active');
        });
    });

    // 2. 글자 수 제한 및 카운팅 (1000자 제한)
    textarea.addEventListener('input', function() {
        const length = this.value.length;
        charCount.textContent = `${length}/1000`;

        if (length > 1000) {
            this.value = this.value.substring(0, 1000);
            charCount.textContent = `1000/1000`;
            charCount.style.color = 'red';
        } else {
            charCount.style.color = '#666'; // 기본 색상
        }
    });

    // 3. 폼 제출 유효성 검사
    reportForm.addEventListener('submit', function(e) {
        const selectedType = document.querySelector('.report_title > div.active');
        const content = textarea.value.trim();

        // 신고 유형을 선택하지 않은 경우
        if (!selectedType) {
            e.preventDefault();
            alert('신고 유형을 선택해 주세요.');
            return;
        }

        // 내용을 입력하지 않은 경우 (또는 기본 문구 그대로인 경우)
        if (content === "" || content === "신고 상세 내용을 적어주세요") {
            e.preventDefault();
            alert('신고 상세 내용을 입력해 주세요.');
            textarea.focus();
            return;
        }

        // 확인 메시지
        if (!confirm('정말로 신고하시겠습니까?')) {
            e.preventDefault();
        }
    });
});