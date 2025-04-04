document.addEventListener("DOMContentLoaded", function () {
    const openCityModalButton = document.getElementById("openCityModal");
    const cityModal = document.getElementById("cityModal");

    if (openCityModalButton && cityModal) {
        // 모달 열기
        openCityModalButton.addEventListener("click", function () {
            cityModal.style.display = "flex"; // display를 flex로 변경하여 중앙에 배치
        });

        // 모달 닫기
        function closeModal() {
            cityModal.style.display = "none";
        }

        // 도시 선택
        window.selectCity = function(button) {
            const cityName = button.getAttribute("data-city");
            const cityId = button.getAttribute("data-id"); // data-id 속성에서 cityId 값을 가져옵니다.

            // 선택한 도시 이름을 입력폼에 설정
            document.getElementById("location").value = cityName;

            // 선택한 cityId를 hidden 필드에 설정
            document.getElementById("cityId").value = cityId;

            closeModal(); // 모달 닫기
        }

        // 모달 닫기 버튼 클릭 이벤트 추가
        const closeButton = document.querySelector('.close');
        if (closeButton) {
            closeButton.addEventListener('click', closeModal);
        }
    } else {
        console.log("필요한 요소를 찾을 수 없습니다.");
    }
});