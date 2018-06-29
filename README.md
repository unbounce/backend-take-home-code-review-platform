
# Scraper

This is a service designed to scrape content from the web to calculate the total page size.
This information is stored in a database, and broadcast for other services to consume.

See `instructions.md` for details about the coding exercise.

## Running the service in development

```
mvn compile exec:java
```

## Running the service in production (optional)

If time avails - In production, the shaded jar file will be executed as a service by Tanuki.

Other than classpath oddities sometimes caused by Tanuki, this can be tested
by running `mvn package`, followed by:
```
java -jar target/plat-interview-1.0-SNAPSHOT-fat.jar
```

The `ScraperApp` class can also be run in any IDE.
