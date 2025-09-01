# 1. JDK 이미지 선택
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드한 jar 복사
COPY build/libs/back-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 포트
EXPOSE 8080

# 4. 컨테이너 실행 명령
ENTRYPOINT ["java","-jar","app.jar"]