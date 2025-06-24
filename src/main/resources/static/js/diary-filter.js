function goToFilteredUrl(event) {
    event.preventDefault();
    const target = event.target;
    const theme = target.getAttribute('data-theme');
    const country = target.getAttribute('data-country');
    const city = target.getAttribute('data-city');

    const meta = document.getElementById('diary-meta');
    let sort = meta.getAttribute('data-sort');
    let keyword = meta.getAttribute('data-keyword');
    let currentTheme = meta.getAttribute('data-theme');
    let currentCountry = meta.getAttribute('data-country');
    let currentCity = meta.getAttribute('data-city');

    if (theme !== null) currentTheme = theme;
    if (country !== null) currentCountry = country;
    if (city !== null) currentCity = city;

    const params = new URLSearchParams();
    if (keyword && keyword !== 'null' && keyword.trim() !== '') params.append('keyword', keyword);
    if (sort && sort !== 'null') params.append('sort', sort);
    if (currentTheme && currentTheme !== 'null') params.append('themeName', currentTheme);
    if (currentCountry && currentCountry !== 'null') params.append('countryName', currentCountry);
    if (currentCity && currentCity !== 'null' && currentCity.trim() !== '') params.append('cityName', currentCity);

    window.location.href = `/diary/list?${params.toString()}`;
}

function removeFilter(type) {
    const url = new URL(window.location.href);
    url.searchParams.delete(type);
    url.searchParams.delete('cursor');
    window.location.href = url.toString();
}