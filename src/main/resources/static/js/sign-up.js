function showAuthCodeInput(purpose) {
    let emailInput = document.getElementById("email");
    let emailValue = emailInput.value;  // 이메일 값을 여기서 얻어옴

    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!emailPattern.test(emailValue)) {
        alert("올바른 이메일 형식이 아닙니다. 다시 확인해주세요.");
        return;
    }

    let authDiv = document.getElementById("auth-code-container");

    if (!authDiv) {
        let emailDiv = document.getElementById("email-container");
        authDiv = document.createElement("div");
        authDiv.id = "auth-code-container";
        authDiv.innerHTML = `
            <label for="authCode">인증 코드</label>
            <input type="text" id="authCode" name="authCode" required>
            <button type="button" id="verifyAuthCodeButton" onclick="verifyAuthCode('${emailValue}', '${purpose}')">확인</button>
        `;
        emailDiv.appendChild(authDiv);
    }

    authDiv.style.display = "block";

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/api/email/send-code', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken
        },
        body: new URLSearchParams({
            email: emailValue,
            purpose: purpose
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.status === "success") {
            alert("인증 메일이 발송되었습니다. 이메일로 받은 인증 코드를 입력해주세요!");
        }
    })
    .catch(error => {
        console.error("서버 응답 에러:", error);
        alert("이메일 인증 코드 전송에 실패했습니다.");
    });

    document.getElementById("authButton").disabled = true;
}

function verifyAuthCode(email, purpose) {
    let authCodeInput = document.getElementById("authCode");
    let authCode = authCodeInput.value;

    if (!authCode) {
        alert("인증 코드를 입력해주세요.");
        return;
    }

    // CSRF 메타 태그에서 읽어오기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/api/email/verify-code', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken // CSRF 토큰 추가
        },
        body: new URLSearchParams({
            email: email,
            code: authCode,
            purpose: purpose
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            alert("인증이 성공적으로 완료되었습니다.");
            document.getElementById("email").setAttribute("readonly", "true");
            document.getElementById("authCode").setAttribute("readonly", "true");
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error("서버 응답 에러:", error);
        alert("인증 코드 확인에 실패했습니다.");
    });
}

document.addEventListener('DOMContentLoaded', function () {
    let registerButton = document.getElementById("registerAuthButton");
    let resetPasswordButton = document.getElementById("resetPasswordAuthButton");

    if (registerButton) {
        registerButton.addEventListener('click', function () {
            showAuthCodeInput('REGISTER');
        });
    }

    if (resetPasswordButton) {
        resetPasswordButton.addEventListener('click', function () {
            showAuthCodeInput('RESET_PASSWORD');
        });
    }
});