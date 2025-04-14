document.addEventListener('DOMContentLoaded', function () {
    let selectedThemes = [];

    // 페이지 로드 시 모달 숨기기
    const modal = document.getElementById('newThemeModal');
    if (modal) {
        modal.style.display = 'none'; // 기본적으로 모달 숨김
    }

    // 테마 선택 시 호출되는 함수
    window.selectTheme = function (button) {
        const selectedThemeId = button.getAttribute('data-theme-id');
        const selectedThemeName = button.getAttribute('data-theme');

        const existingThemeIndex = selectedThemes.findIndex(theme => theme.id === selectedThemeId);
        if (existingThemeIndex === -1) {
            selectedThemes.push({ id: selectedThemeId, name: selectedThemeName });
            button.classList.add('selected');
        } else {
            selectedThemes.splice(existingThemeIndex, 1);
            button.classList.remove('selected');
        }

        updateThemeInput();
    };

    function updateThemeInput() {
        const themeInput = document.getElementById('themeInput');
        if (themeInput) {
            themeInput.value = selectedThemes.map(theme => theme.name).join(', ');
        }
    }

function createHiddenThemeIdsInput() {
    const themeIdsInput = document.getElementById('themeIdsInput');
    if (themeIdsInput) {
        const themeIds = selectedThemes.map(theme => theme.id).join(',');
        themeIdsInput.value = themeIds;
    }
}

    window.closeModal = function () {
        const modal = document.getElementById('newThemeModal');
        if (modal) {
            modal.style.display = 'none';
        }
    };

    // 여행 테마 모달을 여는 함수
    document.getElementById('openThemeModal').onclick = function () {
        const modal = document.getElementById('newThemeModal');
        if (modal) {
            modal.style.display = 'flex'; // 모달을 띄울 때만 'flex'로 설정
            document.querySelectorAll('#newThemeModal button[data-theme-id]').forEach(button => {
                const themeId = button.getAttribute('data-theme-id');
                const isSelected = selectedThemes.some(theme => theme.id === themeId);
                button.classList.toggle('selected', isSelected);
            });
        }
    };

    window.submitSelectedThemes = function () {
        if (selectedThemes.length === 0) {
            alert('여행 유형을 선택해주세요.');
            return;
        }

        updateThemeInput();
        createHiddenThemeIdsInput(); // 'themeIds' 값을 'hidden' input에 반영
        closeModal();
    };

    const form = document.querySelector('form');
    form.addEventListener('submit', function () {
        if (selectedThemes.length > 0) {
            createHiddenThemeIdsInput(); // 폼 제출 전에 'themeIds' 업데이트
        }
    });

    // ✅ 서버에서 전달된 기존 선택된 themeNames가 있을 경우 초기화
    const themeInput = document.getElementById('themeInput');
    if (themeInput && themeInput.value.trim() !== '') {
        const selectedNames = themeInput.value.split(',').map(name => name.trim());
        const themeButtons = document.querySelectorAll('#newThemeModal button[data-theme-id]');
        themeButtons.forEach(button => {
            const themeName = button.getAttribute('data-theme');
            if (selectedNames.includes(themeName)) {
                selectedThemes.push({
                    id: button.getAttribute('data-theme-id'),
                    name: themeName
                });
            }
        });
    }
});