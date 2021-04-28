# Start with the Java 8 image
FROM openjdk:8-jre-alpine

# Copy war file
COPY booking-0.0.1-SNAPSHOT.jar /booking.war

# Run the app
CMD ["/usr/bin/java", "-jar", "/booking.war"]