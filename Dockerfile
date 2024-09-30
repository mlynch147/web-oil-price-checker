FROM maven:latest
#set maven dependency to specific version?

WORKDIR /code

COPY . /code

RUN mvn clean install -DskipTests=true

EXPOSE 8080

CMD ["java", "-jar", "/code/target/oilpricechecker-0.0.1-SNAPSHOT.jar", "server"]
