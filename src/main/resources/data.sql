-- 테마 데이터
INSERT INTO theme (theme_id, name) VALUES (1, '힐링·휴양');
INSERT INTO theme (theme_id, name) VALUES (2, '액티비티');
INSERT INTO theme (theme_id, name) VALUES (3, '문화');
INSERT INTO theme (theme_id, name) VALUES (4, '맛집 탐방');
INSERT INTO theme (theme_id, name) VALUES (5, '자연경관');
INSERT INTO theme (theme_id, name) VALUES (6, '역사 탐방');
INSERT INTO theme (theme_id, name) VALUES (7, '쇼핑');
INSERT INTO theme (theme_id, name) VALUES (8, '스포츠');
INSERT INTO theme (theme_id, name) VALUES (9, '사진 촬영');
INSERT INTO theme (theme_id, name) VALUES (10, '드라이브');
INSERT INTO theme (theme_id, name) VALUES (11, '캠핑');
INSERT INTO theme (theme_id, name) VALUES (12, '호캉스');
INSERT INTO theme (theme_id, name) VALUES (13, '레저 활동');
INSERT INTO theme (theme_id, name) VALUES (14, '테마파크');




INSERT INTO country (country_id, name) VALUES (1, '일본');
INSERT INTO country (country_id, name) VALUES (2, '한국');
INSERT INTO country (country_id, name) VALUES (3, '중국');
INSERT INTO country (country_id, name) VALUES (4, '프랑스');
INSERT INTO country (country_id, name) VALUES (5, '영국');
INSERT INTO country (country_id, name) VALUES (6, '미국');
INSERT INTO country (country_id, name) VALUES (7, '브라질');
INSERT INTO country (country_id, name) VALUES (8, '호주');
INSERT INTO country (country_id, name) VALUES (9, '인도');
INSERT INTO country (country_id, name) VALUES (10, '남아프리카공화국');
INSERT INTO country (country_id, name) VALUES (11, '중동');
INSERT INTO country (country_id, name) VALUES (12, '스페인');
INSERT INTO country (country_id, name) VALUES (13, '이탈리아');
INSERT INTO country (country_id, name) VALUES (14, '캐나다');
INSERT INTO country (country_id, name) VALUES (15, '아르헨티나');
INSERT INTO country (country_id, name) VALUES (16, '러시아');
INSERT INTO country (country_id, name) VALUES (17, '터키');
INSERT INTO country (country_id, name) VALUES (18, '네덜란드');
INSERT INTO country (country_id, name) VALUES (19, '스웨덴');
INSERT INTO country (country_id, name) VALUES (20, '노르웨이');
INSERT INTO country (country_id, name) VALUES (21, '사우디아라비아');
INSERT INTO country (country_id, name) VALUES (22, '필리핀');
INSERT INTO country (country_id, name) VALUES (23, '말레이시아');
INSERT INTO country (country_id, name) VALUES (24, '태국');
INSERT INTO country (country_id, name) VALUES (25, '싱가포르');
INSERT INTO country (country_id, name) VALUES (26, '베트남');
INSERT INTO country (country_id, name) VALUES (27, '인도네시아');
INSERT INTO country (country_id, name) VALUES (28, '포르투갈');
INSERT INTO country (country_id, name) VALUES (29, '체코');
INSERT INTO country (country_id, name) VALUES (30, '독일');
INSERT INTO country (country_id, name) VALUES (31, '멕시코');


-- JAPAN (country_id = 1)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (1, '도쿄', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (2, '오사카', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (3, '후쿠오카', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (4, '시즈오카', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (5, '나고야', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (6, '삿포로', 1, 'JAPAN');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (7, '오키나와', 1, 'JAPAN');

-- KOREA (country_id = 2)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (8, '서울', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (9, '부산', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (10, '인천', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (11, '대구', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (12, '대전', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (13, '광주', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (14, '강릉·속초', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (15, '제주', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (16, '춘천', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (17, '울산', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (18, '전주', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (19, '통영', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (20, '경주', 2, 'KOREA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (21, '여수', 2, 'KOREA');

-- CHINA (country_id = 3)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (22, '베이징', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (23, '상하이', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (24, '홍콩', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (25, '충칭', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (26, '광저우', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (27, '항저우', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (28, '텐진', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (29, '청두', 3, 'CHINA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (30, '우한', 3, 'CHINA');

-- EUROPE (country_id = 4, 12, 13, 18, 19, 20)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (31, '파리', 4, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (32, '마르세유', 4, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (33, '런던', 5, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (34, '맨체스터', 5, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (35, '바르셀로나', 12, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (36, '마드리드', 12, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (37, '로마', 13, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (38, '밀라노', 13, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (39, '모스크바', 16, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (40, '이스탄불', 17, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (41, '앙카라', 17, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (42, '암스테르담', 18, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (43, '스톡홀름', 19, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (44, '예테보리', 19, 'EUROPE');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (45, '오슬로', 20, 'EUROPE');

-- NORTH AMERICA (country_id = 6, 14)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (46, '뉴욕', 6, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (47, '로스앤젤레스', 6, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (48, '괌', 6, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (49, '샌프란시스코', 6, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (50, '토론토', 14, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (51, '밴쿠버', 14, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (52, '멕시코시티', 31, 'NORTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (53, '칸쿤', 31, 'NORTH_AMERICA');

-- SOUTH AMERICA (country_id = 7, 15)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (54, '상파울루', 7, 'SOUTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (55, '리우데자네이루', 7, 'SOUTH_AMERICA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (56, '부에노스아이레스', 15, 'SOUTH_AMERICA');

-- OCEANIA (country_id = 8)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (57, '시드니', 8, 'OCEANIA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (58, '멜버른', 8, 'OCEANIA');

-- SOUTHEAST ASIA (country_id = 9, 22, 24, 25)
INSERT INTO city (city_id, name, country_id, city_type) VALUES (59, '보라카이', 22, 'SOUTHEAST_ASIA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (60, '마닐라', 22, 'SOUTHEAST_ASIA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (61, '방콕', 24, 'SOUTHEAST_ASIA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (62, '푸켓', 24, 'SOUTHEAST_ASIA');
INSERT INTO city (city_id, name, country_id, city_type) VALUES (63, '하노이', 25, 'SOUTHEAST_ASIA');
