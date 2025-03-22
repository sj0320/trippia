function showAuthCodeInput(purpose) {
    let emailInput = document.getElementById("email");
    let emailValue = emailInput.value;  // 이메일 값을 여기서 얻어옴

    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; // 이메일 정규식

    if (!emailPattern.test(emailValue)) {
        alert("올바른 이메일 형식이 아닙니다. 다시 확인해주세요.");
        return;
    }

    // 인증 코드 입력 필드 표시
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

    // 서버로 이메일 인증 요청 전송
    fetch('/users/email/send-code', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: emailValue,
            purpose: purpose // REGISTER 또는 RESET_PASSWORD
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
        }
        return response.json(); // JSON 형식으로 응답 받기
    })
    .then(data => {
        if (data.status === "success") {
            // 성공적인 응답을 받으면 고정된 메시지 표시
            alert("인증 메일이 발송되었습니다. 이메일로 받은 인증 코드를 입력해주세요!");
        }
    })
    .catch(error => {
        console.error("서버 응답 에러:", error);
        alert("이메일 인증 코드 전송에 실패했습니다.");
    });

    // 버튼 비활성화
    document.getElementById("authButton").disabled = true;
}

function verifyAuthCode(email, purpose) {
    let authCodeInput = document.getElementById("authCode");
    let authCode = authCodeInput.value;

    if (!authCode) {
        alert("인증 코드를 입력해주세요.");
        return;
    }

    fetch('/users/email/verify-code', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: email,
            code: authCode,
            purpose: purpose // REGISTER 또는 RESET_PASSWORD
        })
    })
    .then(response => response.json())  // JSON 형식으로 응답 받기
    .then(data => {
        if (data.status === "success") {
            alert("인증이 성공적으로 완료되었습니다.");
            // 인증 성공 후 인증 코드 입력 필드를 읽기 전용으로 설정
            document.getElementById("email").setAttribute("readonly", "true");
            document.getElementById("authCode").setAttribute("readonly", "true");  // 입력 필드를 읽기 전용으로 설정
        } else {
            // 서버에서 반환한 메시지 사용
            alert(data.message);  // 예외 메시지를 클라이언트에 표시
        }
    })
    .catch(error => {
        console.error("서버 응답 에러:", error);
        alert("인증 코드 확인에 실패했습니다.");
    });
}

// 페이지 로드 시 이벤트 추가
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