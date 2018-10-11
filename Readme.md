# Webcrawler

## Build

```$ mvn clean install```

## Usage

```$ java -jar target/crawler.jar```


## Implementation aspects

### Testing

* Refactored the code and extracted the "Downloader" interface, so that unit tests can use a mock implementation.
* more refactoring can be done to unit test corner cases, for example extensive testing of regexp patterns.

### Concurrency

* The process is using Java8 streaming API. Already improved runtime by utilizing parallel execution of download steos.
* Further improvements could be done in connection handling.

### deduplication

* not implemented yet. Could download javascript code, hash it for comparison to find out about code having a different name.