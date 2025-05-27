document.addEventListener("DOMContentLoaded", function () {
    // 공통 스크롤 함수
    function scrollSliderById(sliderId, direction) {
        const slider = document.getElementById(sliderId);
        const scrollAmount = 200;
        slider.scrollBy({
            left: direction * scrollAmount,
            behavior: 'smooth'
        });
    }

    window.scrollSlider = (dir) => scrollSliderById("diarySlider", dir);
    window.scrollPlanSlider = (dir) => scrollSliderById("planSlider", dir);
    window.scrollPastPlanSlider = (dir) => scrollSliderById("pastPlanSlider", dir);

    function updateSliderButtons(sliderId, prevBtnId, nextBtnId) {
        const slider = document.getElementById(sliderId);
        const prevBtn = document.getElementById(prevBtnId);
        const nextBtn = document.getElementById(nextBtnId);

        if (slider.scrollWidth > slider.clientWidth + 5) {
            prevBtn.style.display = "inline";
            nextBtn.style.display = "inline";
        } else {
            prevBtn.style.display = "none";
            nextBtn.style.display = "none";
        }
    }

    // 초기 로딩 시 버튼 표시 판단
    setTimeout(() => {
        updateSliderButtons("diarySlider", "diaryPrevBtn", "diaryNextBtn");
        updateSliderButtons("planSlider", "planPrevBtn", "planNextBtn");
        updateSliderButtons("pastPlanSlider", "pastPlanPrevBtn", "pastPlanNextBtn");
    }, 100);

    // 리사이즈 대응
    window.addEventListener("resize", () => {
        updateSliderButtons("diarySlider", "diaryPrevBtn", "diaryNextBtn");
        updateSliderButtons("planSlider", "planPrevBtn", "planNextBtn");
        updateSliderButtons("pastPlanSlider", "pastPlanPrevBtn", "pastPlanNextBtn");
    });
});