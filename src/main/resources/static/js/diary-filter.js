function goToFilteredUrl(event) {
    event.preventDefault();
    const target = event.target;
    const themeName = target.getAttribute('data-theme');
    const themeId = target.getAttribute('data-theme-id');
    const countryName = target.getAttribute('data-country');
    const countryId = target.getAttribute('data-country-id');
    const city = target.getAttribute('data-city');

    const meta = document.getElementById('diary-meta');
    let sort = meta.getAttribute('data-sort');
    let keyword = meta.getAttribute('data-keyword');
    let currentTheme = meta.getAttribute('data-theme');
    let currentThemeId = meta.getAttribute('data-theme-id');
    let currentCountryId = meta.getAttribute('data-country-id');
    let currentCountry = meta.getAttribute('data-country');
    let currentCity = meta.getAttribute('data-city');

    if (themeId !== null) currentThemeId = themeId;
    if (themeName !== null) currentTheme = themeName;
    if (countryId !== null) currentCountryId = countryId;
    if (countryName !== null) currentCountry = countryName;
    if (city !== null) currentCity = city;

    const params = new URLSearchParams();
    if (keyword && keyword !== 'null' && keyword.trim() !== '') params.append('keyword', keyword);
    if (sort && sort !== 'null') params.append('sort', sort);
    if (currentTheme && currentTheme !== 'null') params.append('themeName', currentTheme);
    if (currentThemeId && currentThemeId !== 'null') params.append('themeId', currentThemeId);
    if (currentCountryId && currentCountryId !== 'null') params.append('countryId', currentCountryId); // 전달
    if (currentCountry && currentCountry !== 'null') params.append('countryName', currentCountry);
    if (currentCity && currentCity !== 'null' && currentCity.trim() !== '') params.append('cityName', currentCity);

    window.location.href = `/diary/list?${params.toString()}`;
}

function removeFilter(type) {
    const url = new URL(window.location.href);
    url.searchParams.delete(type);

    if (type === 'countryName') {
        url.searchParams.delete('countryId');
    } else if (type === 'themeName') {
        url.searchParams.delete('themeId');
    }

    // 커서 정보도 삭제
    url.searchParams.delete('cursor');

    window.location.href = url.toString();
}