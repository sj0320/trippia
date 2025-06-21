function loadRecommendations(cityIds, query = '', containerId = 'recommendationList') {
  const queryParam =
    cityIds.map(id => `cityIds=${id}`).join('&') +
    (query ? `&query=${encodeURIComponent(query)}` : '');

  fetch(`/api/places/recommend?${queryParam}`)
    .then(res => res.json())
    .then(places => {
      const list = document.getElementById(containerId);
      list.innerHTML = '';
      if (places.length === 0) {
        list.innerHTML = '<p>추천 장소가 없습니다.</p>';
        return;
      }
      places.forEach(place => {
        const div = document.createElement('div');
        div.className = 'recommend-item';
        div.style.padding = '15px';
        div.style.marginBottom = '10px';
        div.style.backgroundColor = '#fff';
        div.style.borderRadius = '8px';
        div.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
        div.style.display = 'flex';
        div.style.justifyContent = 'space-between';
        div.style.alignItems = 'center';
        div.style.transition = 'transform 0.3s ease';

        // ✅ 장소 카드 클릭 시 handlePlaceClick 호출
        div.onclick = () => handlePlaceClick(place.placeId);

        // 좌측 영역
        const leftContainer = document.createElement('div');
        leftContainer.style.display = 'flex';
        leftContainer.style.flexDirection = 'column';

        const titleDiv = document.createElement('div');
        titleDiv.style.fontWeight = 'bold';
        titleDiv.style.fontSize = '1.1rem';
        titleDiv.style.color = '#333';
        titleDiv.innerText = `${place.placeName}`;
        leftContainer.appendChild(titleDiv);

        const addressDiv = document.createElement('div');
        addressDiv.style.fontSize = '0.9rem';
        addressDiv.style.color = '#666';
        addressDiv.style.marginTop = '5px';
        addressDiv.innerText = `${place.address}`;
        leftContainer.appendChild(addressDiv);

        if (place.themes && place.themes.length > 0) {
          const tagContainer = document.createElement('div');
          tagContainer.style.marginTop = '8px';
          tagContainer.style.display = 'flex';
          tagContainer.style.flexWrap = 'wrap';

          place.themes.forEach(theme => {
            const tag = document.createElement('span');
            tag.innerText = theme;
            tag.style.backgroundColor = '#e0f7fa';
            tag.style.color = '#00796b';
            tag.style.fontSize = '0.85rem';
            tag.style.padding = '5px 10px';
            tag.style.marginRight = '8px';
            tag.style.marginBottom = '5px';
            tag.style.borderRadius = '16px';
            tag.style.border = '1px solid #00796b';
            tag.style.transition = 'background-color 0.2s ease';
            tag.style.cursor = 'pointer';

            tag.onmouseenter = () => tag.style.backgroundColor = '#00796b';
            tag.onmouseleave = () => tag.style.backgroundColor = '#e0f7fa';
            tagContainer.appendChild(tag);
          });
          leftContainer.appendChild(tagContainer);
        }

        div.appendChild(leftContainer);

        // 우측 상세보기 버튼
        const detailBtn = document.createElement('button');
        detailBtn.innerText = '상세보기';
        detailBtn.style.padding = '6px 12px';
        detailBtn.style.fontSize = '0.9rem';
        detailBtn.style.backgroundColor = '#00796b';
        detailBtn.style.color = '#fff';
        detailBtn.style.border = 'none';
        detailBtn.style.borderRadius = '4px';
        detailBtn.style.cursor = 'pointer';
        detailBtn.style.flexShrink = '0';

        detailBtn.onclick = (event) => {
          event.stopPropagation(); // 부모 div 클릭 방지
          window.open(`/travel/places/${place.placeId}`, '_blank');
        };

        div.appendChild(detailBtn);
        list.appendChild(div);
      });
    });
}