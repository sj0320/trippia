    document.addEventListener('DOMContentLoaded', function() {
        let selectedThemes = []; // 선택된 테마들을 저장할 배열

        // 테마 선택 시 호출되는 함수
        window.selectTheme = function(button) {
            const selectedThemeId = button.getAttribute('data-theme-id');
            const selectedThemeName = button.getAttribute('data-theme');

            // 이미 선택된 테마가 배열에 있는지 확인
            const existingThemeIndex = selectedThemes.findIndex(theme => theme.id === selectedThemeId);
            if (existingThemeIndex === -1) {
                selectedThemes.push({ id: selectedThemeId, name: selectedThemeName });
                button.classList.add('selected'); // 선택된 버튼에 색상 추가
            } else {
                // 이미 선택된 테마일 경우, 배열에서 제거
                selectedThemes.splice(existingThemeIndex, 1);
                button.classList.remove('selected'); // 선택 해제된 버튼에서 색상 제거
            }

            // 선택된 테마들을 텍스트로 입력 필드에 보여줌
            const themeInput = document.getElementById('themeInput');
            if (themeInput) {
                themeInput.value = selectedThemes.map(theme => theme.name).join(', '); // 선택된 테마 이름들을 쉼표로 구분
            }
        };

        function createHiddenThemeIdsInput() {
            let existingInput = document.querySelector('input[name="themeIds"]');
            if (existingInput) {
                existingInput.remove(); // 기존 input 제거
            }

            const themeIdsInput = document.createElement('input');
            themeIdsInput.setAttribute('type', 'hidden');
            themeIdsInput.setAttribute('name', 'themeIds');

            // 선택된 테마 ID들을 콤마로 구분하여 저장
            const themeIds = selectedThemes.map(theme => theme.id).join(',');
            themeIdsInput.setAttribute('value', themeIds);

            // 🛠 기존 body에 추가하던 것을 form 내부에 추가하도록 변경
            const form = document.querySelector('form');
            form.appendChild(themeIdsInput);
        }

        // 모달 닫기 함수
        window.closeModal = function() {
            const modal = document.getElementById('newThemeModal');
            if (modal) {
                modal.style.display = 'none'; // 모달을 숨김
            }
        };

        // 모달 열기 함수 (버튼 클릭 시 모달을 보여줌)
        document.getElementById('openThemeModal').onclick = function() {
            const modal = document.getElementById('newThemeModal');
            if (modal) {
                modal.style.display = 'block'; // 모달을 보여줌
            }
        };

        // 확인 버튼을 클릭했을 때 선택된 테마 정보를 업데이트
        window.submitSelectedThemes = function() {
            // 선택된 테마가 없을 경우 예외 처리
            if (selectedThemes.length === 0) {
                alert('여행 유형을 선택해주세요.');
                return;
            }

            // 선택된 테마 ID들을 입력 필드에 업데이트
            const themeInput = document.getElementById('themeInput');
            themeInput.value = selectedThemes.map(theme => theme.name).join(', '); // 선택된 테마 이름들을 쉼표로 구분

            // 모달 닫기
            closeModal();
        };

        // 폼 제출 시 처리
        const form = document.querySelector('form');
        form.addEventListener('submit', function() {
            if (selectedThemes.length > 0) {
                // 폼이 제출될 때 선택된 테마들을 숨겨진 input으로 전달
                createHiddenThemeIdsInput();
            }
        });
    });