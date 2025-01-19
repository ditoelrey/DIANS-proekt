# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Install Python and required dependencies
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    maven \
    && apt-get clean

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build file into the container (for caching)
COPY pom.xml /app/

# Run Maven dependency resolution, so dependencies are cached separately
RUN mvn dependency:resolve

# Copy the application code (except requirements.txt first)
COPY src /app/src

# Copy the requirements.txt file into the container
COPY requirements.txt /app/

# Install Python dependencies
RUN pip3 install -r requirements.txt

# Run Maven to build the app and create the JAR file
RUN mvn clean package -DskipTests

# Expose the port the application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/target/dians2-0.0.1-SNAPSHOT.jar"]
