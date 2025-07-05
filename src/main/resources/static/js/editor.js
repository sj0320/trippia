tinymce.init({
    language: "ko_KR",
    selector: '#editor',
    height: 600,
    plugins: [
        'image', 'media', 'code', 'lists', 'link', 'table', 'autolink',
        'advlist', 'preview', 'fullscreen', 'help', 'wordcount'
    ],
    toolbar: `
        undo redo | blocks fontselect fontsizeselect |
        bold italic underline forecolor backcolor |
        alignleft aligncenter alignright alignjustify |
        bullist numlist outdent indent |
        link image media | table | code fullscreen help
    `,
    menubar: 'file edit view insert format tools table help',
    automatic_uploads: true,
    file_picker_types: 'image',

    images_upload_handler: function (blobInfo, success, failure) {
        console.log("[UPLOAD] 이미지 업로드 시작");

        const file = blobInfo.blob();
        console.log("[UPLOAD] 업로드할 파일:", file);

        const formData = new FormData();
        formData.append('upload', file);

        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        console.log("[UPLOAD] CSRF 설정 완료", csrfHeader, csrfToken);

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/file/upload', true);
        xhr.setRequestHeader(csrfHeader, csrfToken);

        xhr.onload = function () {
            console.log("[UPLOAD] 서버 응답 수신 상태코드:", xhr.status);
            console.log("[UPLOAD] 응답 내용:", xhr.responseText);

            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    console.log("[UPLOAD] 파싱된 응답 객체:", response);
                    console.log("[UPLOAD] 파싱된 응답 객체2:", response.url);
                    success(response.url);  // 여기가 포인트입니다.
                } catch (e) {
                    console.error("[UPLOAD] JSON 파싱 오류:", e);
                    failure('Invalid JSON response');
                }
            } else {
                console.error("[UPLOAD] 서버 에러:", xhr.status);
                failure(`Upload failed: ${xhr.status}`);
            }
        };

        xhr.onerror = function () {
            console.error("[UPLOAD] 네트워크 오류 발생");
            failure('Upload failed due to a network error.');
        };

        console.log("[UPLOAD] 요청 전송 시작");
        xhr.send(formData);
    },

    content_style: `
        img {
            max-width: 100%;
            height: auto;
        }
    `
});