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