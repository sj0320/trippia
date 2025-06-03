window.inviteParticipant = function () {
    const nickname = document.getElementById("participantNickname").value;

    if (!nickname) {
        alert("닉네임을 입력해주세요.");
        return;
    }

    const dataDiv = document.getElementById("planData");
    const planId = dataDiv ? dataDiv.getAttribute("data-id") : null;

    if (!planId) {
        alert("플랜 ID를 찾을 수 없습니다.");
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    fetch(`/api/plan-participant/${planId}/invite?nickname=${encodeURIComponent(nickname)}`, {
        method: "POST",
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (response.ok) {
            alert("초대 성공!");
            location.reload();
        } else {
            response.text().then(text => {
                if (text.includes("이미 초대되었거나 참여중인 사용자입니다.")) {
                    alert("이미 초대했거나 참여 중인 사용자입니다.");
                } else {
                    alert("초대 실패: " + text);
                }
            });
        }
    })
    .catch(error => {
        alert("오류 발생: " + error.message);
    });
};

// ✅ 초기화 로직
document.addEventListener("DOMContentLoaded", function () {
    const dropdownMenu = document.getElementById("dropdownMenu");
    const hamburgerButton = document.querySelector("button[onclick='toggleMenu()']");

    window.toggleMenu = function () {
        const isVisible = window.getComputedStyle(dropdownMenu).display !== "none";
        dropdownMenu.style.display = isVisible ? "none" : "block";
    };

    document.addEventListener("click", function (event) {
        const isClickInsideMenu = dropdownMenu.contains(event.target);
        const isClickOnHamburger = hamburgerButton.contains(event.target);

        if (!isClickInsideMenu && !isClickOnHamburger) {
            dropdownMenu.style.display = "none";
        }
    });

    function openModal(id) {
        const modal = document.getElementById(id);
        if (modal) modal.classList.add("active");
        dropdownMenu.style.display = "none";
    }

    function closeModal(id) {
        const modal = document.getElementById(id);
        if (modal) modal.classList.remove("active");
    }

    window.openEditModal = function () {
        const modal = document.getElementById("editModal");
        const dataDiv = document.getElementById("planData");

        if (dataDiv) {
            const title = dataDiv.getAttribute("data-title");
            const start = dataDiv.getAttribute("data-start");
            const end = dataDiv.getAttribute("data-end");

            document.getElementById("editTitle").value = title;
            document.getElementById("editStartDate").value = start;
            document.getElementById("editEndDate").value = end;
        }

        if (modal) {
            modal.classList.remove("hidden");
            modal.classList.add("active");
        }
    };


    window.submitPlanEdit = function () {
        const dataDiv = document.getElementById("planData");
        if (!dataDiv) {
            alert("플랜 정보가 없습니다.");
            return;
        }

        const planId = dataDiv.getAttribute("data-id");
        if (!planId) {
            alert("플랜 ID가 없습니다.");
            return;
        }

        const title = document.getElementById("editTitle").value;
        const startDate = document.getElementById("editStartDate").value;
        const endDate = document.getElementById("editEndDate").value;

        if (!title || !startDate || !endDate) {
            alert("모든 값을 입력해주세요.");
            return;
        }

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        const payload = {
            title: title,
            startDate: startDate,
            endDate: endDate
        };

        fetch(`/api/travel/plan/${planId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) return;
            closeModal("editModal");
            window.location.reload();
        })
        .catch(error => {
            // 콘솔 로깅만 하고 사용자에게는 알리지 않음
            console.error("수정 중 오류 발생:", error.message);
        });
    };

    window.closeEditModal = () => closeModal("editModal");

    window.openParticipantModal = () => openModal("participantModal");
    window.closeParticipantModal = () => closeModal("participantModal");

    window.openDeleteModal = () => openModal("deleteModal");
    window.closeDeleteModal = () => closeModal("deleteModal");

    window.confirmDeletePlan = function () {
        const dataDiv = document.getElementById("planData");
        if (!dataDiv) {
            alert("플랜 정보가 없습니다.");
            return;
        }

        const planId = dataDiv.getAttribute("data-id");
        if (!planId) {
            alert("삭제할 플랜 ID가 없습니다.");
            return;
        }

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        fetch(`/api/travel/plan/${planId}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('삭제 실패');
            }
            alert("삭제 완료!");
            closeModal("deleteModal");
            window.location.href = "/";
        })
        .catch(error => {
            alert("삭제 중 오류 발생: " + error.message);
        });
    };
});