# Use the official OpenJDK 21 image from the Docker Hub
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY ./WebAPImodule/target /app

# Copy the entrypoint script into the container
COPY set-env.sh /app/set-env.sh

# Install Apache Maven
RUN apt-get update && apt-get install -y maven

# Give execute permission to the entrypoint script
RUN chmod +x /app/set-env.sh

# Define the entrypoint to run the script
ENTRYPOINT ["/app/set-env.sh"]