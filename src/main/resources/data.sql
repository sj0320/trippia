USE trippia;
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
INSERT INTO country (country_id, name) VALUES (32, '뉴질랜드');
INSERT INTO country (country_id, name) VALUES (33, '칠레');


-- JAPAN (country_id = 1)
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (1, '도쿄', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/tokyo.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (2, '오사카', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/osaka.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (3, '후쿠오카', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/hukuoka.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (4, '시즈오카', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/sizuoka.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (5, '나고야', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/nagoya.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (6, '삿포로', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/satporo.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (7, '오키나와', 1, 'JAPAN', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/japan/okinawa.jpg');

-- KOREA (country_id = 2)
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (8, '서울', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/seoul.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (9, '부산', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/busan.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (10, '인천', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/incheon.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (11, '대구', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/daegu.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (12, '대전', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/daejeon.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (13, '광주', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/kwangju.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (14, '강릉·속초', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/gangreung.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (15, '제주', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/jeju.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (16, '춘천', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/chuncheon.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (17, '울산', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/ulsan.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (18, '전주', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/junju.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (19, '통영', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/tongyung.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (20, '경주', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/kyungju.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (21, '여수', 2, 'KOREA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/yeosu.jpg');

-- CHINA (country_id = 3)
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (22, '베이징', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/korea/daejeon.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (23, '상하이', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/sanghai.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (24, '홍콩', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/hongkong.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (25, '충칭', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/chungching.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (26, '광저우', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/guangzhou.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (27, '항저우', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/hangzhou.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (28, '텐진', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/tenjin.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (29, '청두', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/chungdu.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (30, '우한', 3, 'CHINA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/china/wuhan.jpg');

-- EUROPE
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (31, '파리', 4, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/paris.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (32, '마르세유', 4, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/marseille.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (33, '런던', 5, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/london.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (34, '맨체스터', 5, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/manchester.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (35, '바르셀로나', 12, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/barcelona.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (36, '마드리드', 12, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/madrid.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (37, '로마', 13, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/rome.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (38, '밀라노', 13, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/milan.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (39, '모스크바', 16, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/moscow.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (40, '이스탄불', 17, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/istanbul.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (41, '앙카라', 17, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/ankara.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (42, '암스테르담', 18, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/amsterdam.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (43, '스톡홀름', 19, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/stockholm.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (44, '예테보리', 19, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/yeteboli.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (45, '오슬로', 20, 'EUROPE', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/europe/oslo.jpg');

-- NORTH AMERICA
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (46, '뉴욕', 6, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/newyork.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (47, '로스앤젤레스', 6, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/los_angeles.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (48, '괌', 6, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/guam.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (49, '샌프란시스코', 6, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/san_francisco.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (50, '토론토', 14, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/toronto.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (51, '밴쿠버', 14, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/vancouver.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (52, '멕시코시티', 31, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/mexico_city.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (53, '칸쿤', 31, 'NORTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/north_america/kancoon.jpg');

-- SOUTH AMERICA
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (54, '상파울루', 7, 'SOUTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/south_america/sao_paulo.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (55, '리우데자네이루', 7, 'SOUTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/south_america/rio_de_janeiro.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (56, '부에노스아이레스', 15, 'SOUTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/south_america/buenos_aires.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (66, '산티아고', 33, 'SOUTH_AMERICA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/south_america/santiago.jpg');

-- OCEANIA
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (57, '시드니', 8, 'OCEANIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/oceania/sydney.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (58, '멜버른', 8, 'OCEANIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/oceania/melbourne.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (64, '오클랜드', 32, 'OCEANIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/oceania/auckland.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (65, '퀸스타운', 32, 'OCEANIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/oceania/queenstown.jpg');

-- SOUTHEAST ASIA
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (59, '보라카이', 22, 'SOUTHEAST_ASIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/southeast_asia/boracay.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (60, '마닐라', 22, 'SOUTHEAST_ASIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/southeast_asia/manila.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (61, '방콕', 24, 'SOUTHEAST_ASIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/southeast_asia/bangkok.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (62, '푸켓', 24, 'SOUTHEAST_ASIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/southeast_asia/pucket.jpg');
INSERT INTO city (city_id, name, country_id, city_type, image_url) VALUES (63, '하노이', 26, 'SOUTHEAST_ASIA', 'https://trippia-bucket.s3.ap-northeast-2.amazonaws.com/southeast_asia/hanoi.jpg');