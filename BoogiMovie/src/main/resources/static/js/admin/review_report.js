console.log('review_report.js')

document.addEventListener('DOMContentLoaded', function() {
    
    // 1. 전체 선택 / 해제 기능
    const selectAllCheckbox = document.getElementById('selectAll');
    const itemCheckboxes = document.querySelectorAll('input[name="reportIds"]');

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            itemCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });
    }

    // 2. 검색 기간 퀵 버튼 기능 (당일, 1개월 등)
    const dateButtons = document.querySelectorAll('fieldset > div:not(.date)');
    const startDateInput = document.querySelector('.front_date');
    const endDateInput = document.querySelector('.rear_date');

    dateButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const range = this.textContent;
            console.log(range + " 범위 검색 설정");
            
            // 실제 구현 시에는 여기서 날짜 계산 로직을 넣어 
            // startDateInput.textContent 값을 변경하거나 클래스를 추가하여 시각적 피드백을 줍니다.
            dateButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // 3. '삭제' 및 '유지' 버튼 처리 (이벤트 위임 방식)
    const tableBody = document.querySelector('tbody');

    tableBody.addEventListener('click', function(e) {
        const target = e.target;
        
        // 삭제 버튼 클릭 시
        if (target.classList.contains('btn-delete')) {
            if (confirm('해당 리뷰를 삭제 처리하시겠습니까?')) {
                const reportRow = target.closest('tr');
                const reportId = reportRow.querySelector('input[name="reportIds"]').value;
                
                // 처리 로직 호출 (예: fetch API를 이용한 서버 전송)
                processReport(reportId, 'DELETE', reportRow);
            }
        }

        // 유지 버튼 클릭 시
        if (target.classList.contains('btn-keep')) {
            if (confirm('해당 신고를 기각하고 리뷰를 유지하시겠습니까?')) {
                const reportRow = target.closest('tr');
                const reportId = reportRow.querySelector('input[name="reportIds"]').value;
                
                processReport(reportId, 'KEEP', reportRow);
            }
        }
    });

    // 4. 전체 삭제 버튼 기능
    const totalDeleteBtn = document.querySelector('.total_delete button');
    totalDeleteBtn.addEventListener('click', function() {
        const checkedItems = document.querySelectorAll('input[name="reportIds"]:checked');
        
        if (checkedItems.length === 0) {
            alert('삭제할 항목을 선택해 주세요.');
            return;
        }

        if (confirm(`선택한 ${checkedItems.length}개의 항목을 모두 삭제하시겠습니까?`)) {
            // 체크된 ID들을 배열로 수집
            const ids = Array.from(checkedItems).map(cb => cb.value);
            console.log("삭제할 ID 리스트:", ids);
            // 서버로 일괄 삭제 요청 로직 추가 필요
        }
    });
});

/**
 * 신고 처리 서버 통신 함수 (예시)
 */
function processReport(id, type, rowElement) {
    console.log(`신고번호 ${id}를 ${type} 상태로 처리합니다.`);
    
    // 실제 서버 통신 코드가 들어갈 자리입니다.
    // 성공 시 화면에서 행을 제거하거나 상태를 변경합니다.
    // rowElement.remove(); // 예: 화면에서 즉시 제거
    alert('처리가 완료되었습니다.');
    location.reload(); // 또는 리스트 새로고침
}