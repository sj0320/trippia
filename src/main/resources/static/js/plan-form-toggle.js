document.addEventListener("DOMContentLoaded", function () {
    const dropdownMenu = document.getElementById("dropdownMenu");
    const hamburgerButton = document.querySelector("button[onclick='toggleMenu()']");

    window.toggleMenu = function () {
        const isVisible = window.getComputedStyle(dropdownMenu).display !== "none";
        dropdownMenu.style.display = isVisible ? "none" : "block";
    };

    // 메뉴 외부 클릭 시 닫히도록
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
        dropdownMenu.style.display = "none"; // 메뉴도 함께 닫기
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
            modal.classList.add("active");  // 추가!
        }
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

        // CSRF 토큰 정보 가져오기
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