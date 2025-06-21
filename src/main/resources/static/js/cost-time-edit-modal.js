let currentEditingItem = null;
let currentDayCard = null;

// ✅ 지출·시간 버튼 클릭 시
document.addEventListener('click', (event) => {
  if (event.target.matches('.btn-cost-time')) {
    const entryRow = event.target.closest('.entry-row');
    const dayCard = event.target.closest('.day-card');
    const contentItem = entryRow?.querySelector('.entry-item');

    if (!contentItem || !entryRow || !dayCard) return;

    currentEditingItem = {
      id: entryRow.dataset.id,
      expectedCost: contentItem.dataset.expectedCost || '0',
      executionTime: entryRow.dataset.executionTime || ''
    };
    currentDayCard = dayCard;

    // 값 세팅
    const rawCost = parseInt(String(currentEditingItem.expectedCost).replace(/[^\d]/g, '')) || 0;
    document.getElementById('costInput').value = rawCost.toLocaleString();
    document.getElementById('timeInput').value = currentEditingItem.executionTime;

    // 모달 열기
    document.getElementById('costTimeEditModal').classList.add('active');
  }
});

// ✅ 지출 입력 포맷 적용
document.getElementById('costInput')?.addEventListener('input', (e) => {
  const val = e.target.value.replace(/[^\d]/g, '');
  e.target.value = val ? Number(val).toLocaleString() : '';
});

// ✅ 저장 버튼 클릭 시
document.getElementById('costTimeSaveBtn')?.addEventListener('click', () => {
  if (!currentEditingItem || !currentDayCard) return;

  const formattedCost = document.getElementById('costInput').value;
  const numericCost = parseInt(formattedCost.replace(/[^\d]/g, ''), 10) || 0;
  const timeValue = document.getElementById('timeInput').value;

  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  fetch(`/api/travel/schedule-item/${currentEditingItem.id}/meta`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify({
      expectedCost: numericCost,
      executionTime: timeValue
    })
  }).then(res => {
    if (res.ok) {
      const scheduleId = currentDayCard?.dataset?.scheduleId;
      if (scheduleId) {
        return fetch(`/api/travel/schedule-items?scheduleId=${scheduleId}`);
      }
    } else {
      alert('지출·시간 저장에 실패했습니다.');
      throw new Error();
    }
  }).then(res => res.json())
    .then(data => {
      refreshDayCards(data, currentDayCard);
      calculateTotalCost();
      document.getElementById('costTimeEditModal').classList.remove('active');
    });
});

// ✅ 취소 버튼 클릭 시
document.getElementById('costTimeCancelBtn')?.addEventListener('click', () => {
  document.getElementById('costTimeEditModal').classList.remove('active');
});