CREATE DATABASE IF NOT EXISTS trippia;

USE trippia;


CREATE TABLE user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) UNIQUE,
    role VARCHAR(50),
    login_type VARCHAR(50),
    profile_image_url VARCHAR(255),
    grade VARCHAR(50) DEFAULT 'BEGINNER',
    exp INT DEFAULT 0,
    bio TEXT,
    created_at DATETIME
);

CREATE TABLE notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    content VARCHAR(255),
    related_url VARCHAR(255),
    type VARCHAR(50),
    is_read TINYINT(1) DEFAULT 0,
    created_at DATETIME,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE country (
    country_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE city (
    city_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    country_id BIGINT,
    city_type VARCHAR(255),
    image_url VARCHAR(255),
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES country(country_id)
);

CREATE TABLE place (
    place_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE diary (
    diary_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    city_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    thumbnail VARCHAR(255),
    start_date DATE,
    end_date DATE,
    companion VARCHAR(50),
    rating INT,
    total_budget INT,
    view_count INT NOT NULL,
    like_count INT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_diary_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_diary_city FOREIGN KEY (city_id) REFERENCES city(city_id)
);

CREATE TABLE diary_comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    diary_id BIGINT,
    content TEXT,
    created_at DATETIME,
    CONSTRAINT fk_diary_comment_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_diary_comment_diary FOREIGN KEY (diary_id) REFERENCES diary(diary_id)
);

CREATE TABLE diary_place (
    diary_place_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT,
    place_id VARCHAR(255),
    CONSTRAINT fk_diary_place_diary FOREIGN KEY (diary_id) REFERENCES diary(diary_id),
    CONSTRAINT fk_diary_place_place FOREIGN KEY (place_id) REFERENCES place(place_id)
);

CREATE TABLE likes (
    likes_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT,
    user_id BIGINT,
    created_at DATETIME,
    CONSTRAINT fk_likes_diary FOREIGN KEY (post_id) REFERENCES diary(diary_id),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE theme (
    theme_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE diary_theme (
    diary_theme_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_id BIGINT,
    diary_id BIGINT,
    CONSTRAINT fk_diary_theme_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id),
    CONSTRAINT fk_diary_theme_diary FOREIGN KEY (diary_id) REFERENCES diary(diary_id)
);

CREATE TABLE companion_post (
    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    thumbnail_url VARCHAR(255),
    city_id BIGINT,
    start_date DATE,
    end_date DATE,
    gender_restriction VARCHAR(50),
    recruitment_count INT,
    view_count INT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_companion_post_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_companion_post_city FOREIGN KEY (city_id) REFERENCES city(city_id)
);

CREATE TABLE companion_post_comment (
    post_comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    post_id BIGINT,
    user_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_companion_post_comment_post FOREIGN KEY (post_id) REFERENCES companion_post(post_id),
    CONSTRAINT fk_companion_post_comment_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE plan (
    plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_email VARCHAR(255),
    title VARCHAR(255),
    start_date DATE,
    end_date DATE
);

CREATE TABLE schedule (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT,
    date DATE,
    CONSTRAINT fk_schedule_plan FOREIGN KEY (plan_id) REFERENCES plan(plan_id)
);

CREATE TABLE schedule_item (
    schedule_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT,
    expected_cost INT DEFAULT 0 NOT NULL,
    execution_time TIME,
    sequence INT NOT NULL,
    item_type VARCHAR(31),
    CONSTRAINT fk_schedule_item_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(schedule_id)
);

CREATE TABLE memo (
    schedule_item_id BIGINT PRIMARY KEY,
    content VARCHAR(255),
    created_at DATETIME,
    CONSTRAINT fk_memo_schedule_item FOREIGN KEY (schedule_item_id) REFERENCES schedule_item(schedule_item_id)
);

CREATE TABLE schedule_place (
    schedule_item_id BIGINT PRIMARY KEY,
    google_map_id VARCHAR(255),
    name VARCHAR(255),
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    category VARCHAR(255),
    CONSTRAINT fk_schedule_place_schedule_item FOREIGN KEY (schedule_item_id) REFERENCES schedule_item(schedule_item_id)
);

CREATE TABLE plan_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    plan_id BIGINT,
    role VARCHAR(255),
    status VARCHAR(255),
    CONSTRAINT fk_plan_participant_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_plan_participant_plan FOREIGN KEY (plan_id) REFERENCES plan(plan_id)
);

CREATE TABLE plan_city (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT,
    city_id BIGINT,
    CONSTRAINT fk_plan_city_plan FOREIGN KEY (plan_id) REFERENCES plan(plan_id),
    CONSTRAINT fk_plan_city_city FOREIGN KEY (city_id) REFERENCES city(city_id)
);