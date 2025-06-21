document.getElementById("submit-comment").addEventListener("click", function () {
    const content = document.getElementById("comment-content").value.trim();
    const diaryId = document.getElementById("diary-id").value;

    if (!content) {
        alert("댓글을 입력해주세요.");
        return;
    }

    fetch(`/api/diary/${diaryId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector("meta[name='_csrf']").getAttribute("content")
        },
        body: JSON.stringify({ content })
    })
    .then(res => {
        if (res.status === 401) {
            showLoginModal();
            throw new Error("로그인 필요");
        }

        if (!res.ok) throw new Error("네트워크 오류");
        return res.json();
    })
    .then(newComment => {
        appendCommentToList(newComment);
        document.getElementById("comment-content").value = "";
    })
    .catch(err => {
        console.error(err);
    });
});

function appendCommentToList(comment) {
    const commentList = document.querySelector(".comment-list");

    // 빈 댓글 안내 메시지 제거
    const emptyCommentMsg = document.getElementById("no-comment-message");
    if (emptyCommentMsg) {
        emptyCommentMsg.remove();
    }

    const commentElement = document.createElement("li");
    commentElement.className = "comment-item";
    commentElement.innerHTML = `
        <div class="comment-header">
            <div class="comment-author-info">
                <img class="profile-image" src="${comment.authorProfile}" alt="프로필 이미지">
                <strong>${comment.authorNickname}</strong>
            </div>
            <span class="comment-date">${formatDateTime(comment.createdAt)}</span>
        </div>
        <p>${comment.content}</p>
    `;

    // 최신 댓글이 위로 오게
    commentList.prepend(commentElement);
}

function formatDateTime(dateTimeStr) {
    const date = new Date(dateTimeStr);
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
         + `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function pad(n) {
    return n.toString().padStart(2, '0');
}