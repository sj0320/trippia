let lastId = null;
let lastCreatedAt = null;
let lastLikeCount = null;
let lastViewCount = null;
let loading = false;
let hasNext = true;

document.addEventListener("DOMContentLoaded", () => {
    const cursor = document.getElementById('last-cursor');
    if (cursor) {
        lastId = cursor.dataset.id ? parseInt(cursor.dataset.id) : null;
        lastCreatedAt = cursor.dataset.createdAt || null;
        lastLikeCount = cursor.dataset.likeCount ? parseInt(cursor.dataset.likeCount) : null;
        lastViewCount = cursor.dataset.viewCount ? parseInt(cursor.dataset.viewCount) : null;
    }
    window.addEventListener("scroll", handleScroll);
});

function handleScroll() {
    const scrollY = window.scrollY;
    const viewportHeight = window.innerHeight;
    const fullHeight = document.documentElement.scrollHeight;

    // 바닥 근처면 로딩
    if (scrollY + viewportHeight >= fullHeight - 300 && !loading && hasNext) {
        loadMoreDiaries();
    }
}

function parseMetaValue(value) {
    return (!value || value === "null") ? null : value;
}

function loadMoreDiaries() {
    loading = true;

    const meta = document.getElementById('diary-meta');
    const container = document.querySelector('.diary-container');

    if (!meta || !container) {
        console.error("❌ diary-meta나 diary-container 요소가 없음!");
        loading = false;
        return;
    }

    const params = new URLSearchParams();

    const keyword = parseMetaValue(meta.dataset.keyword);
    const theme = parseMetaValue(meta.dataset.theme);
    const city = parseMetaValue(meta.dataset.city);
    const country = parseMetaValue(meta.dataset.country);
    const sort = parseMetaValue(meta.dataset.sort);


    // 검색 조건을 URL 파라미터로 추가
    if (keyword) params.append("keyword", keyword);
    if (theme) params.append("theme", theme);
    if (city) params.append("city", city);
    if (country) params.append("country", country);
    if (sort) params.append("sort", sort);

    // 커서 데이터 추가
    if (lastId !== null) {
        params.append("lastId", lastId);
        if (sort === "latest" && lastCreatedAt !== null) {
            params.append("lastCreatedAt", lastCreatedAt);
        } else if (sort === "likes" && lastLikeCount !== null) {
            params.append("lastLikeCount", lastLikeCount);
        } else if (sort === "views" && lastViewCount !== null) {
            params.append("lastViewCount", lastViewCount);
        }
    }

    const url = `/diary/list/data?${params.toString()}`;
    console.log("📡 Fetching:", url);

    fetch(url)
        .then(res => {
            if (!res.ok) throw new Error(`❌ 서버 응답 오류: ${res.status}`);
            return res.json();
        })
        .then(data => {
            if (!data || !data.content || data.content.length === 0) {
                console.log("⛔ 더 이상 데이터 없음");
                hasNext = false;
                return;
            }

            data.content.forEach(diary => {
                const item = document.createElement('div');
                item.classList.add('diary-item');
                item.innerHTML = `
                    <a href="/diary/${diary.id}">
                        <img src="${diary.thumbnail}" alt="Thumbnail" class="thumbnail">
                    </a>
                    <div class="diary-info">
                        <h3>${diary.title}</h3>
                        <div class="author-info">
                            <img src="${diary.authorProfile}" alt="Author Profile" class="author-profile">
                            <span class="nickname">${diary.authorNickname}</span>
                        </div>
                        <div class="stats">
                            <span>조회수: ${diary.viewCount}</span> |
                            <span>좋아요: ${diary.likeCount}</span>
                        </div>
                    </div>
                `;
                container.appendChild(item);
            });

            // 커서 업데이트
            const last = data.content[data.content.length - 1];
            lastId = last.id;
            if (sort === "latest") lastCreatedAt = last.createdAt;
            else if (sort === "likes") lastLikeCount = last.likeCount;
            else if (sort === "views") lastViewCount = last.viewCount;

            hasNext = !data.hasNext;
        })
        .catch(err => {
            console.error("🚨 로딩 중 오류 발생:", err);
        })
        .finally(() => {
            loading = false;
        });
}