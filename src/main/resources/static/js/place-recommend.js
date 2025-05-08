function loadRecommendations(cityIds, query = '') {
            const queryParam =
                cityIds.map(id => `cityIds=${id}`).join('&') +
                (query ? `&query=${encodeURIComponent(query)}` : '');

            fetch(`/travel/places/recommend?${queryParam}`)
                .then(res => res.json())
                .then(places => {
                    const list = document.getElementById('recommendationList');
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
                        div.style.cursor = 'pointer';
                        div.style.transition = 'transform 0.3s ease';
                        div.style.display = 'flex';
                        div.style.flexDirection = 'column';

                        const titleDiv = document.createElement('div');
                        titleDiv.style.fontWeight = 'bold';
                        titleDiv.style.fontSize = '1.1rem';
                        titleDiv.style.color = '#333';
                        titleDiv.innerText = `${place.placeName}`;
                        div.appendChild(titleDiv);

                        const addressDiv = document.createElement('div');
                        addressDiv.style.fontSize = '0.9rem';
                        addressDiv.style.color = '#666';
                        addressDiv.style.marginBottom = '10px';
                        addressDiv.innerText = `${place.address}`;
                        div.appendChild(addressDiv);

                        if (place.themes && place.themes.length > 0) {
                            const tagContainer = document.createElement('div');
                            tagContainer.style.marginBottom = '8px';
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

                            div.appendChild(tagContainer);
                        }

                        div.onclick = () => handlePlaceClick(place.placeId);
                        list.appendChild(div);
                    });
                });
        }