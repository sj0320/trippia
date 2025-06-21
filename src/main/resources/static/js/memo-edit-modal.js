let currentMemoItemId = null;
let currentMemoDayCard = null;

// 수정 버튼 클릭 시
document.addEventListener('click', (event) => {
  if (event.target.matches('.btn-edit')) {
    const entryRow = event.target.closest('.entry-row');
    const memoTextSpan = entryRow.querySelector('.memo-text');
    const dayCard = event.target.closest('.day-card');

    currentMemoItemId = entryRow?.dataset?.id;
    currentMemoDayCard = dayCard;
    document.getElementById('memoEditTextarea').value = memoTextSpan.textContent.trim();

    document.getElementById('memoEditModal').classList.add('active');
  }
});

// 저장 버튼 클릭 시
document.getElementById('memoEditSaveBtn')?.addEventListener('click', () => {
  const content = document.getElementById('memoEditTextarea').value.trim();
  if (!content || !currentMemoItemId || !currentMemoDayCard) return;

  const scheduleId = currentMemoDayCard.dataset.scheduleId;
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  fetch(`/api/travel/memo/${currentMemoItemId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify({ content })
  }).then(res => {
    if (res.ok) {
      return fetch(`/api/travel/schedule-items?scheduleId=${scheduleId}`);
    } else {
      alert('메모 수정에 실패했습니다.');
      throw new Error();
    }
  }).then(res => res.json())
    .then(data => {
      refreshDayCards(data, currentMemoDayCard);
      calculateTotalCost();
      document.getElementById('memoEditModal').classList.remove('active');
    });
});

// 취소 버튼 클릭 시
document.getElementById('memoEditCancelBtn')?.addEventListener('click', () => {
  document.getElementById('memoEditModal').classList.remove('active');
});