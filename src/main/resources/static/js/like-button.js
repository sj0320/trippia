document.addEventListener("DOMContentLoaded", function () {
    const likeButton = document.getElementById("like-button");
    const likeCount = document.getElementById("like-count");
    const diaryId = likeButton.dataset.diaryId;
    const isLiked = likeButton.dataset.liked === "true";

    // 초기 상태 세팅
    if (isLiked) {
        likeButton.classList.add("liked");
        likeButton.innerText = "❤️ 좋아요 취소";
    }

    likeButton.addEventListener("click", function () {
        const liked = likeButton.classList.contains("liked");
        const url = liked ? `/diary/${diaryId}/unlike` : `/diary/${diaryId}/like`;

        fetch(url, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        })
        .then(res => {
            if (res.status === 401) {
                showLoginModal(); // 커스텀 함수로 로그인 필요 모달 띄우기
                throw new Error("로그인 필요");
            }

            if (!res.ok) throw new Error('네트워크 오류');
            return res.json();
        })
        .then(data => {
            likeCount.textContent = data.likeCount;
            likeButton.classList.toggle("liked");
            likeButton.innerText = likeButton.classList.contains("liked") ? "❤️ 좋아요 취소" : "🤍 좋아요";
        })
        .catch(err => {
            console.error(err);
        });
    });
});

function showLoginModal() {
    const modal = document.getElementById("login-modal");
    modal.style.display = "block";

    const currentUrl = window.location.pathname + window.location.search;
    const encodedRedirect = encodeURIComponent(currentUrl);

    // 일반 로그인 이동
    document.getElementById("go-login").onclick = function () {
        window.location.href = `/users/login?redirect=${encodedRedirect}`;
    };

    // 소셜 로그인 쿠키 저장
    const providers = ['kakao', 'google', 'naver'];
    providers.forEach(provider => {
        const btn = document.getElementById(`social-${provider}`);
        if (btn) {
            btn.onclick = function () {
                document.cookie = `redirect_uri=${encodedRedirect}; path=/; max-age=600`;
                window.location.href = `/oauth2/authorization/${provider}`;
            };
        }
    });

    // 취소
    document.getElementById("cancel-login").onclick = function () {
        modal.style.display = "none";
    };
}