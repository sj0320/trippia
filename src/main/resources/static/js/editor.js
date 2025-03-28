document.addEventListener("DOMContentLoaded", function () {
    const editor = document.getElementById("editor");
    const editorPlaceholder = document.getElementById("editorPlaceholder");
    const imageUpload = document.getElementById("imageUpload");
    const contentInput = document.getElementById("contentInput");

    // 입력할 때마다 크기 증가
    editor.addEventListener("input", function () {
        adjustEditorHeight();
        if (editor.innerHTML.trim() !== "") {
            editorPlaceholder.style.display = "none";
        }
    });

    // 포커스가 에디터로 갔을 때 placeholder 숨기기
    editor.addEventListener("focus", function () {
        if (editor.innerHTML.trim() !== "") {
            editorPlaceholder.style.display = "none";
        }
    });

    // 포커스가 떠날 때 placeholder 보이기 (내용이 없을 경우)
    editor.addEventListener("blur", function () {
        if (editor.innerHTML.trim() === "") {
            editorPlaceholder.style.display = "block";
        }
    });

    // 이미지 업로드 핸들러
    imageUpload.addEventListener("change", function (event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const img = document.createElement("img");
                img.src = e.target.result;

                const selection = window.getSelection();
                const range = selection.getRangeAt(0);
                range.deleteContents();
                range.insertNode(img);

                adjustEditorHeight();
                imageUpload.value = '';
            };
            reader.readAsDataURL(file);
        }
    });

    // 폼 제출 시 내용 저장
    document.querySelector("form").addEventListener("submit", function () {
        contentInput.value = editor.innerHTML;
    });

    // 에디터 높이 자동 증가
    function adjustEditorHeight() {
        editor.style.height = "auto";
        editor.style.height = editor.scrollHeight + "px";
    }

    // 텍스트 포맷 함수
    window.formatText = function (command, value = null) {
        document.execCommand(command, false, value);
    };
});