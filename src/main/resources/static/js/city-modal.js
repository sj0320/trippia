document.addEventListener("DOMContentLoaded", function () {
    const openCityModalButton = document.getElementById("openCityModal");
    const cityModal = document.getElementById("cityModal");

    const cityIdInput = document.getElementById("cityId");
    const cityNameInput = document.getElementById("cityName");

    if (cityNameInput && cityIdInput && cityNameInput.value && !cityIdInput.value) {
        const buttons = document.querySelectorAll('[data-id][data-city]');
        buttons.forEach(button => {
            if (button.getAttribute("data-city") === cityNameInput.value) {
                cityIdInput.value = button.getAttribute("data-id");
                button.classList.add("selected"); // 초기 선택된 도시 버튼에 selected 클래스 추가
            }
        });
    }

    if (openCityModalButton && cityModal) {
        openCityModalButton.addEventListener("click", function () {
            cityModal.style.display = "flex";

            // 🔁 모달이 열릴 때 현재 선택된 도시 반영
            const selectedCityId = cityIdInput.value;
            const buttons = document.querySelectorAll('[data-id][data-city]');
            buttons.forEach(button => {
                const buttonCityId = button.getAttribute("data-id");
                button.classList.toggle("selected", buttonCityId === selectedCityId);
            });
        });

        function closeModal() {
            cityModal.style.display = "none";
        }

        // 도시 선택
        window.selectCity = function (button) {
            const cityName = button.getAttribute("data-city");
            const cityId = button.getAttribute("data-id");

            document.getElementById("cityName").value = cityName;
            document.getElementById("cityId").value = cityId;

            // ✅ 모든 버튼의 .selected 제거 후 선택한 버튼에만 추가
            const allButtons = document.querySelectorAll('[data-id][data-city]');
            allButtons.forEach(btn => btn.classList.remove("selected"));
            button.classList.add("selected");

            closeModal();
        }

        // 모달 닫기 버튼
        const closeButton = document.querySelector('.close');
        if (closeButton) {
            closeButton.addEventListener('click', closeModal);
        }
    } else {
        console.log("필요한 요소를 찾을 수 없습니다.");
    }
});