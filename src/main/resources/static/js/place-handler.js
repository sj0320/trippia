function openPlaceModal(button) {
    const dayCard = button.closest('.day-card');
    currentEntryList = dayCard.querySelector('.entry-list');

    document.getElementById('placeModal').style.display = 'block';

    const input = document.getElementById('autocomplete');
    const list = document.getElementById('recommendationList');

    input.value = '';
    list.innerHTML = '<p>🔄 장소 추천을 불러오는 중...</p>';
    list.style.display = 'block';

    loadRecommendations(cityIds);
}

function closeModal() {
    document.getElementById('placeModal').style.display = 'none';
}


function handlePlaceClick(placeId) {
  const service = new google.maps.places.PlacesService(document.createElement('div'));
  service.getDetails({ placeId: placeId }, (result, status) => {
    if (status !== google.maps.places.PlacesServiceStatus.OK) {
      alert("장소 정보를 가져올 수 없습니다.");
      return;
    }

    const scheduleId = currentEntryList.closest('.day-card')?.dataset?.scheduleId;
    if (!scheduleId) {
      alert("일정 ID를 찾을 수 없습니다.");
      return;
    }

    const placeEntry = {
      placeId: placeId,
      scheduleId: scheduleId,
      name: result.name,
      address: result.formatted_address || result.vicinity,
      latitude: result.geometry.location.lat(),
      longitude: result.geometry.location.lng(),
    };

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/api/travel/place', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify(placeEntry)
    })
      .then(response => {
        if (!response.ok) throw new Error("서버에 장소를 저장하지 못했습니다.");
        return response.json();
      })
      .then(data => {
        const scheduleItemId = data.scheduleItemId;

        const entries = currentEntryList.querySelectorAll('.entry-item[data-latitude]');
        let lastPlace = null;
        if (entries.length > 0) {
          const last = entries[entries.length - 1];
          lastPlace = {
            latitude: parseFloat(last.dataset.latitude),
            longitude: parseFloat(last.dataset.longitude),
          };
        }

        const placeCount = currentEntryList.querySelectorAll('.circle-number').length + 1;

        const addNumber = () => {
          const row = document.createElement('div');
          row.className = 'entry-row place-row';
          row.dataset.id = scheduleItemId;

          const circle = document.createElement('div');
          circle.className = 'circle-number';
          circle.innerText = placeCount;

          const placeDiv = document.createElement('div');
          placeDiv.className = 'entry-item place-entry';
          placeDiv.innerText = `📍 ${placeEntry.name} (${placeEntry.address})`;
          placeDiv.dataset.latitude = placeEntry.latitude;
          placeDiv.dataset.longitude = placeEntry.longitude;
          placeDiv.dataset.id = scheduleItemId;
          placeDiv.dataset.expectedCost = '0';

          const costEl = document.createElement('p');
          costEl.className = 'item-cost';
          costEl.textContent = '0';
          placeDiv.appendChild(costEl);

          // 편집 영역
          const editArea = document.createElement('div');
          editArea.className = 'edit-area';

          const editToggle = document.createElement('span');
          editToggle.className = 'edit-toggle';
          editToggle.innerText = '편집';

          const buttonWrapper = document.createElement('div');
          buttonWrapper.className = 'edit-buttons hidden';
          buttonWrapper.innerHTML = `
            <button class="btn-delete">삭제</button>
            <button class="btn-cost-time">지출·시간</button>
          `;

          editToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            document.querySelectorAll('.edit-buttons').forEach(btn => btn.classList.add('hidden'));
            buttonWrapper.classList.remove('hidden');
          });

          buttonWrapper.querySelector('.btn-cost-time').addEventListener('click', () => {
            const modal = document.getElementById('editModal');
            modal.classList.add('active');
            currentEditingItem = {
              id: scheduleItemId,
              expectedCost: 0,
              executionTime: ''
            };
            currentDayCard = currentEntryList.closest('.day-card');
            document.getElementById('modalCost').value = '';
            document.getElementById('modalTime').value = '';
          });

          editArea.appendChild(editToggle);
          editArea.appendChild(buttonWrapper);

          row.appendChild(circle);
          row.appendChild(placeDiv);
          row.appendChild(editArea);
          currentEntryList.appendChild(row);
        };

        if (lastPlace) {
          calculateDistance(lastPlace, placeEntry, (distanceText) => {
            const distanceDiv = document.createElement('div');
            distanceDiv.className = 'distance-item';
            distanceDiv.innerText = `거리: ${distanceText}`;
            currentEntryList.appendChild(distanceDiv);
            addNumber();
            closeModal();
          });
        } else {
          addNumber();
          closeModal();
        }
      })
      .catch(error => {
        console.error(error);
        alert("장소 저장 중 문제가 발생했습니다.");
      });
  });
}