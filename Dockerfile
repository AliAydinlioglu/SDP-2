FROM maven:3.9-eclipse-temurin-21

WORKDIR /app/sdp2-java

# Install dependencies needed for JavaFX (if run with display)
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libgtk-3-0 \
    libxtst6 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

COPY sdp2-java/pom.xml ./
RUN mvn dependency:go-offline -B || true

COPY sdp2-java/src ./src
COPY sdp2-java/config ./config

RUN mvn clean package -DskipTests

CMD ["mvn", "javafx:run"]
