# Nutrient-Health-Analysis-App
Nutrient Health Analysis App for team H20

## To Run the Prebuilt Images:

Run the command: ``docker-compose -f docker-compose.prebuilt.yml up -d``

Access the site via [http://localhost](http://localhost)

## To Build and Run The Full Solution:

Run the command: ``docker-compose up --build -d``

Access the site via [http://localhost](http://localhost)

## To Run The Web Service Independently (via IDE):

Add the `spring.profiles.active=local` parameter to the standard ``java`` command and run as normal.

Access the service via [http://localhost:8080](http://localhost:8080)

## To Run the Frontend Application Independently:

Run the command ``npm start``

Access the frontend via [http://localhost:4200](http://localhost:4200)
