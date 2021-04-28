# Booking API
This is a Booking API for the very last hotel in Cancun:
* The hotel has only one room available.
* To give a chance to everyone to book the room, the stay can’t be longer than 3 days and
can’t be reserved more than 30 days in advance.
* All reservations start at least the next day of booking, to simplify the use case, a “DAY’ in the hotel room starts from 00:00 to 23:59:59.
* For the sake of simplicity, I considered that there is no need for any data from the customer who made the booking and that the booking id is enough for any type of action needed.


In this project it is possible to:
* Create a booking.
* Get a booking by its id.
* Get all bookings.
* Delete a booking.
* Update a booking.

The technologies/frameworks used were:
* Swagger
* Java 8
* Spring Boot
* MongoDB
* JUnit
* MockMvc
* Mockito
* Docker
* Docker Compose

### To run the application:
To run the application you will need at least to have installed [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).

Using your terminal, go to the docker folder and run:
* *docker build -t java-api .*
* *docker-compose build --no-cache*
* *docker-compose up*

With this commands, your application will be running and you will be able to access the [Application Swagger](http://localhost:8081/swagger-ui.html).

### If you are a developer:
You will need to have installed [Docker](https://docs.docker.com/get-docker/), [Docker Compose](https://docs.docker.com/compose/install/), [Maven](https://maven.apache.org/download.cgi) and [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows) (or another IDE of your choice.).

To test your modifications using Docker containers, you will have to run, using your terminal:
* *mvn clean install* (this command will also run all tests developed in the application)
* *docker build -t java-api .*
* *docker-compose build --no-cache*
* *docker-compose up*

To access MongoDB inside of the Docker container, using your terminal and run:
* *docker exec -it mongo-db bash*
* *mongo bookings*
* *use booking*
* *db.bookings.find({});* (or any other command)