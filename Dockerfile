# Set base image: Alpine Linux distribution with OpenJDK 17 installed
FROM openjdk:17-alpine

WORKDIR /app

# This path must exist as it is used as a mount point for testing
# Ensure your app is loading files from this location
RUN mkdir /app/test-files

# Copy the source code into the container
COPY . .

# Compile the Java code into a JAR file
RUN ./gradlew build

EXPOSE 8279

# Run the Java application inside the container
CMD [ "java", "-jar", "/app/build/libs/api-0.0.1-SNAPSHOT.jar" ]
