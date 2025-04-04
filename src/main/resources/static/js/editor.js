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
                img.style.maxWidth = "60%"; // 이미지 크기 제한

                // 커서 위치에 이미지 삽입
                insertImageAtCursor(img);

                adjustEditorHeight();
                imageUpload.value = ''; // 파일 입력 초기화
            };
            reader.readAsDataURL(file);
        }
    });

document.querySelector("form").addEventListener("submit", function (event) {
    const editor = document.getElementById("editor");
    const contentInput = document.getElementById("contentInput");

    // HTML 내용이 잘 저장되는지 확인
    contentInput.value = editor.innerHTML;
    console.log("전송되는 content 값:", contentInput.value); // 디버깅용
    alert("폼 제출 직전 content 값:\n" + contentInput.value); // 팝업으로 값 확인
    // content 값이 정상적으로 들어가는지 확인 후 서버로 전송
});

    // 에디터 높이 자동 증가
    function adjustEditorHeight() {
        editor.style.height = "auto";
        editor.style.height = editor.scrollHeight + "px";
    }

    // 커서 위치에 이미지를 삽입하는 함수
    function insertImageAtCursor(img) {
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);

        // 커서가 에디터 안에 있을 때
        if (editor.contains(selection.anchorNode)) {
            range.deleteContents();
            range.insertNode(img);

            // 삽입 후 커서를 이미지 뒤로 이동
            const br = document.createElement("br");
            range.insertNode(br);

            // 커서 위치를 줄바꿈 뒤로 이동
            const newRange = document.createRange();
            newRange.setStartAfter(br);
            newRange.setEndAfter(br);
            selection.removeAllRanges();
            selection.addRange(newRange);
        } else {
            // 커서가 에디터 외부에 있을 때는 에디터의 마지막 위치에 삽입
            const br = document.createElement("br");
            editor.appendChild(br); // 마지막에 줄바꿈 추가
            editor.appendChild(img); // 이미지 삽입
            editor.appendChild(br); // 이미지 뒤에 줄바꿈 추가

            // 삽입 후 커서를 줄바꿈 뒤로 이동
            const newRange = document.createRange();
            newRange.setStartAfter(br); // 줄바꿈 뒤로 커서 이동
            newRange.setEndAfter(br);
            const selection = window.getSelection();
            selection.removeAllRanges();
            selection.addRange(newRange);
        }
    }

    // 텍스트 포맷 함수
    window.formatText = function (command, value = null) {
        document.execCommand(command, false, value);
    };
});