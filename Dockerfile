FROM maven:3-openjdk-17
ENV DB_REDIS_IP="localhost"
WORKDIR /project
COPY . ./
COPY wait-for-it.sh ./wait-for-it.sh
RUN chmod +x wait-for-it.sh
RUN mvn clean package
RUN echo "redis ip = ${DB_REDIS_IP}"
CMD ["java", "-jar", "target/Spring_LineBot-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
