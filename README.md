Rokt Parser
===

### Application
This application processes text files containing entries of the following format:
```
[Date in YYYY-MM-DDThh:mm:ssZ Format][space][Email Address][space][Session Id in GUID format]
```

### Code Structure
`/controller` - Controllers are responsible for handling HTTP requests

`/model` - Models of file entries and request payloads

`/service` - Business logic for processing the data

### Approach
The text files are processed as input streams as opposed to being loaded into memory entirely. This is to avoid memory issues with large files.
Furthermore, using parallel stream processing, multiple CPU cores can be leveraged to process the files.

```java
new BufferedReader(new InputStreamReader(Files.newInputStream(path)))
    .lines()
    .parallel()
```

### Build & Run
`./build.sh`

`./run.sh`


### Testing
Integration tests are written covering the numerous test cases.
Since there are no external data sources being utilized, unit tests are not required.
