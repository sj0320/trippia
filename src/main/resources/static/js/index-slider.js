document.addEventListener('DOMContentLoaded', function () {
    // 슬라이더 상태 관리 객체
    const state = {
        slider1: { index: 0, appended: false },
        slider2: { index: 0, appended: false }
    };

    // 슬라이더 이동 함수
    function moveSlide(sliderId, direction) {
        const slider = document.getElementById(sliderId);
        const current = state[sliderId];
        const thumbnails = slider.querySelectorAll('.thumbnail');
        if (thumbnails.length === 0) return;

        const thumb = thumbnails[0];
        const thumbWidth = thumb.offsetWidth;
        const style = getComputedStyle(thumb);
        const thumbMargin = parseInt(style.marginLeft) + parseInt(style.marginRight);
        const slideWidth = thumbWidth + thumbMargin;
        const visibleSlides = Math.floor(slider.parentElement.offsetWidth / slideWidth);

        let totalSlides = thumbnails.length;

        if (!current.appended && current.index + visibleSlides >= totalSlides) {
            // 더보기 슬라이드 추가 (처음 한번만)
            const moreSlide = document.createElement('a');
            moreSlide.href = "/diary/list";
            moreSlide.className = "thumbnail";
            moreSlide.style.backgroundColor = "#e0e0e0";
            moreSlide.innerHTML = `<div class="overlay-text">더 많은 여행일지 보러가기 →</div>`;
            slider.appendChild(moreSlide);
            current.appended = true;
            totalSlides += 1;
        }

        const maxIndex = Math.max(totalSlides - visibleSlides, 0);
        let nextIndex = current.index + direction;
        if (nextIndex < 0) nextIndex = 0;
        if (nextIndex > maxIndex) nextIndex = maxIndex;

        current.index = nextIndex;
        slider.style.transform = `translateX(${-current.index * slideWidth}px)`;
    }

    // 버튼 클릭 이벤트 연결
    document.querySelectorAll('.arrow-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const sliderId = this.getAttribute('data-slider-id');
            const direction = parseInt(this.getAttribute('data-direction'));
            moveSlide(sliderId, direction);
        });
    });
});