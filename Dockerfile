FROM amazoncorretto:17

WORKDIR /opt/app

COPY build/libs/*-all.jar application.jar

ENTRYPOINT ["java","-jar","application.jar"]