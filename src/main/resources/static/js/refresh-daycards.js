function refreshDayCards(data, dayCard) {
  const entryList = dayCard.querySelector('.entry-list');
  const originalItems = [];

  // 데이터 기반으로 아이템 처리
  data.forEach((item) => {
    if (item.type === 'PLACE') {
      originalItems.push({
        id: item.id,
        type: 'place',
        latitude: item.latitude,
        longitude: item.longitude,
        name: item.name || '이름 없음',
        address: item.address || '',
        expectedCost: item.expectedCost,
        executionTime: item.executionTime || ''
      });
    } else {
      originalItems.push({
        id: item.id,
        type: 'memo',
        content: item.content || '내용 없음',
        expectedCost: item.expectedCost,
        executionTime: item.executionTime || ''
      });
    }
  });

  entryList.innerHTML = '';
  let prevPlace = null;
  let placeCount = 0;

  originalItems.forEach(item => {
    const row = document.createElement('div');
    row.className = item.type === 'place' ? 'entry-row place-row' : 'entry-row memo-row';
    row.dataset.id = item.id;
    row.dataset.executionTime = item.executionTime || '';

    const circle = document.createElement('div');
    circle.className = item.type === 'place' ? 'circle-number' : 'circle-memo';
    circle.innerText = item.type === 'place' ? ++placeCount : '🟡';

    const contentItem = document.createElement('div');
    contentItem.className = item.type === 'place' ? 'entry-item place-entry' : 'entry-item';

    if (item.type === 'place') {
      contentItem.innerText = `📍 ${item.name} (${item.address})`;
      contentItem.dataset.latitude = item.latitude;
      contentItem.dataset.longitude = item.longitude;
      contentItem.dataset.id = item.id;
      contentItem.dataset.expectedCost = item.expectedCost;

      const costEl = document.createElement('p');
      costEl.className = 'item-cost';
      costEl.textContent = item.expectedCost?.toLocaleString() || '0';
      contentItem.appendChild(costEl);
    } else {
      contentItem.innerHTML = `✏️ <span class="memo-text">${item.content}</span><br>`;
      contentItem.dataset.id = item.id;
      contentItem.dataset.expectedCost = item.expectedCost;

      const costEl = document.createElement('p');
      costEl.className = 'item-cost';
      costEl.textContent = item.expectedCost?.toLocaleString() || '0';
      contentItem.appendChild(costEl);
    }

    // 편집 버튼 영역
    const editArea = document.createElement('div');
    editArea.className = 'edit-area';

    const editToggle = document.createElement('span');
    editToggle.className = 'edit-toggle';
    editToggle.innerText = '편집';

   const buttonWrapper = document.createElement('div');
   buttonWrapper.className = 'edit-buttons hidden';

   // 삭제 버튼
   const deleteBtn = document.createElement('button');
   deleteBtn.className = 'btn-delete';
   deleteBtn.innerText = '삭제';
   buttonWrapper.appendChild(deleteBtn);

   // (조건) 수정 버튼 - memo 타입에만 추가
   if (item.type === 'memo') {
     const editBtn = document.createElement('button');
     editBtn.className = 'btn-edit';
     editBtn.innerText = '수정';
     buttonWrapper.appendChild(editBtn);
   }

   // 지출·시간 버튼 (모든 타입 공통)
   const costTimeBtn = document.createElement('button');
   costTimeBtn.className = 'btn-cost-time';
   costTimeBtn.innerText = '지출·시간';
   buttonWrapper.appendChild(costTimeBtn);

    editToggle.addEventListener('click', (e) => {
      e.stopPropagation();
      document.querySelectorAll('.edit-buttons').forEach(btn => btn.classList.add('hidden'));
      buttonWrapper.classList.remove('hidden');
    });


    editArea.appendChild(editToggle);
    editArea.appendChild(buttonWrapper);

    row.appendChild(circle);
    row.appendChild(contentItem);
    row.appendChild(editArea);
    entryList.appendChild(row);

    if (item.type === 'place' && prevPlace) {
      const distanceDiv = document.createElement('div');
      distanceDiv.className = 'distance-item';
      calculateDistance(prevPlace, {
        latitude: item.latitude,
        longitude: item.longitude
      }, (distanceText) => {
        distanceDiv.innerText = `거리: ${distanceText}`;
      });
      entryList.insertBefore(distanceDiv, row);
    }

    if (item.type === 'place') {
      prevPlace = {
        latitude: item.latitude,
        longitude: item.longitude
      };
    }
  });
}