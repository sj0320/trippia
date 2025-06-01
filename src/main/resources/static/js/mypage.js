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

    function scrollSlider(dir) {
        scrollSliderById("diarySlider", dir);
    }

    function scrollPlanSlider(dir) {
        scrollSliderById("planSlider", dir);
    }

    function scrollPastPlanSlider(dir) {
        scrollSliderById("pastPlanSlider", dir);
    }

    window.scrollSlider = scrollSlider;
    window.scrollPlanSlider = scrollPlanSlider;
    window.scrollPastPlanSlider = scrollPastPlanSlider;

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

    setTimeout(() => {
        updateSliderButtons("diarySlider", "diaryPrevBtn", "diaryNextBtn");
        updateSliderButtons("planSlider", "planPrevBtn", "planNextBtn");
        updateSliderButtons("pastPlanSlider", "pastPlanPrevBtn", "pastPlanNextBtn");
    }, 100);

    window.addEventListener("resize", () => {
        updateSliderButtons("diarySlider", "diaryPrevBtn", "diaryNextBtn");
        updateSliderButtons("planSlider", "planPrevBtn", "planNextBtn");
        updateSliderButtons("pastPlanSlider", "pastPlanPrevBtn", "pastPlanNextBtn");
    });

    // 프로필 편집 모달 열기
    function openProfileEditModal() {
        const profileDataDiv = document.getElementById("profileData");
        if (!profileDataDiv) return;

        const profileData = {
            nickname: profileDataDiv.dataset.nickname,
            introduction: profileDataDiv.dataset.introduction,
            profileImageUrl: profileDataDiv.dataset.profileImageUrl,
        };

        const modal = document.getElementById("editProfileModal");
        if (modal) modal.classList.remove("hidden");

        document.getElementById("editNickname").value = profileData.nickname || "";
        document.getElementById("editIntro").value = profileData.introduction || "";
        document.getElementById("previewProfileImg").src = profileData.profileImageUrl || "/images/default_profile.png";
        document.getElementById("editProfileImage").value = ""; // 파일 초기화
    }

    // ✅ 프로필 수정 모달 닫기
    function closeProfileEditModal() {
        const modal = document.getElementById("editProfileModal");
        if (modal) modal.classList.add("hidden");
    }

    // ✅ 이미지 미리보기
    const profileImgInput = document.getElementById("editProfileImage");
    if (profileImgInput) {
        profileImgInput.addEventListener("change", function (event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    document.getElementById("previewProfileImg").src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // ✅ 프로필 수정 요청 보내기 (FormData 사용)
    function submitProfileEdit() {
        const nickname = document.getElementById("editNickname").value;
        const bio = document.getElementById("editIntro").value;
        const profileImageFile = document.getElementById("editProfileImage").files[0];

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        const formData = new FormData();
        formData.append("nickname", nickname);
        formData.append("bio", bio);
        if (profileImageFile) {
            formData.append("profileImage", profileImageFile);
        }

        fetch('/users/edit', {
            method: 'PATCH',
            headers: {
                [csrfHeader]: csrfToken
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            closeProfileEditModal();
            window.location.reload();
        })
        .catch(error => {
            alert(error.message);
        });
    }

    // ✅ 전역 접근 위해 window에 할당
    window.openProfileEditModal = openProfileEditModal;
    window.closeProfileEditModal = closeProfileEditModal;
    window.submitProfileEdit = submitProfileEdit;
});