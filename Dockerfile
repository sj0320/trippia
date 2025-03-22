## 사용하고자 하는 Java 버전 기반 이미지 선택
#FROM openjdk:17-jdk-slim
#
## Spring Boot 애플리케이션 빌드 후 JAR 파일을 컨테이너에 복사
#COPY target/trippia-0.0.1-SNAPSHOT.jar /app.jar
#
## 애플리케이션 실행
#ENTRYPOINT ["java", "-jar", "/app.jar"]