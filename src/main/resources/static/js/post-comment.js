document.getElementById("submit-comment").addEventListener("click", async function (e) {
    e.preventDefault();

    const postSection = document.querySelector(".comment-section");
    const postId = postSection.getAttribute("data-post-id");
    const content = document.getElementById("comment-content").value.trim();

    if (!content) {
        alert("댓글을 입력하세요.");
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    fetch(`/api/companion-post/${postId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken,
        },
        body: JSON.stringify({ content }),
    })
    .then(response => {
        if (response.ok) {
            location.reload();
            return;
        } else if (response.status === 401) {
            showLoginModal();
            throw new Error("로그인 필요");
        } else {
            return response.json().then(error => {
                throw new Error(error.error || "알 수 없는 오류");
            });
        }
    })
    .catch(err => {
        console.error("댓글 작성 중 오류 발생:", err);
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