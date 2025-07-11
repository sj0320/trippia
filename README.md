# TRIPPIA

> ✈️ **[TRIPPIA 바로가기](https://trippia.site)**
> 
> 여행을 함께할 사람을 모집하고, 일정 계획과 장소 추천을 통해 친구들과 손쉽게 여행을 준비할 수 있는 모임형 여행 플랫폼입니다.
> 
---

## 🧩 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [기술 스택](#-기술-스택)
3. [ERD & 아키텍처](#-erd--아키텍처)
4. [핵심 기능](#-핵심-기능)
5. [시연 영상](#-시연-영상)

---

## 📌 프로젝트 소개

- **프로젝트 개요**  
  여행을 좋아하는 사람이나 여행 초보자들이 다른 사람들의 여행일지를 참고해 여행을 시작하고, 함께할 동행자를 모집하며 체계적으로 여행 계획을 세울 수 있는 서비스입니다.

- **주요 기능 요약**
  - 여행일지 관리 기능 (CRUD)
  - 여행 동행자 게시판 관리 기능 (CRUD)
  - 여행일지 및 게시판에 대한 댓글 작성과 좋아요 및 조회수 기능
  - 여행 계획 생성 및 동행자 초대 기능
  - 여행지 추천 기능
  - 여행지 리뷰 조회 기능

---

## 🛠 기술 스택

| 분류            | 사용 기술                                      |
|-----------------|------------------------------------------------|
| **Backend**     | Java 17, Spring Boot, Spring Security, Spring Data Jpa, QueryDsl |
| **Database**    | MariaDB, Redis                                 |
| **Infra/배포**  | AWS EC2, S3, Docker, GitHub Actions             |

---

## 🗂 ERD & 아키텍처

### 🔹 ERD

> ![image](https://github.com/user-attachments/assets/70df8f47-6bca-4d8c-8df0-8776c365ad1c)


---

### 🔹 시스템 아키텍처

> ![image](https://github.com/user-attachments/assets/14e5f0c3-5bab-4568-b031-da0d309b96f0)


---

## 🚀 핵심 기능

| 번호 | 기능 명                     | 설명 |
|------|-----------------------------|------|
| 1    | 여행일지 관리          | 여행일지에 대한 생성, 조회, 수정, 삭제를 수행합니다.
| 2    | 여행 동행자 모집 포스팅    | 여행 모집글을 포스팅하고 같이 여행을 떠날 동행자를 모집합니다. |
| 3    | 여행 계획 생성 및 관리   | 여행 계획을 생성하여 여행 스케줄을 동행자와 함께 관리합니다.
| 4    | 마이페이지 관리            | 마이페이지를 통해 사용자 프로필 관리 및 활동이력을 관리하고 다가올 스케줄을 확인합니다. |
---

## 🎥 시연 영상

### 1. 여행일지 관리

- 📝 **설명**: 여행일지를 작성하고 조회하는 과정을 시연합니다.

https://github.com/user-attachments/assets/5bed7a43-42ea-43ea-b8af-f33b4d524847



---

### 2. 여행 동행자 모집 포스팅

- 📝 **설명**: 여행 모집글을 작성하고 조회하는 과정을 시연합니다.



https://github.com/user-attachments/assets/053f9373-56af-4b2d-b9d1-6a3f6c64b670




---

### 3. 여행 계획 생성 및 관리

- 📝 **설명**: 여행 계획을 생성하고 여행 참여자와 함께 스케줄을 관리합니다.

#### 3-1. 여행 계획 생성

https://github.com/user-attachments/assets/c13299e3-3ddf-4bc0-838d-05f765587e04

#### 3-2. 선택한 도시에 대한 여행지 추천

https://github.com/user-attachments/assets/acc2d19f-b7c3-4f88-b5bd-58d7d1e61abc

#### 3-3. 스케줄 편집

https://github.com/user-attachments/assets/3c5d0567-a7aa-40b4-b95f-3f0a790d9c2a

---

### 4. 마이페이지

- 📝 **설명**: 마이페이지에서 사용자의 프로필 관리, 활동 이력 그리고 다가오는 일정 등을 확인하는 기능을 시연합니다.

https://github.com/user-attachments/assets/b0c4d1d0-26cf-4f0c-9dfe-5ab17bf524ca

---

### 5. 여행일지 정렬 및 필터링 조회

- 📝 **설명**: 여행일지를 정렬하거나 필터링하여 조회하는 과정을 시연합니다.

https://github.com/user-attachments/assets/b443d267-afda-402d-8033-db48d531271a

---
