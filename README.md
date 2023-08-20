# LogParser

> [!NOTE]
> This project was created as a take-home assessment for a job interview.  
> I got the job.

Small project created in Java 11 and Spring Boot 2.5.3, which downloads the log file found in [ftp://ita.ee.lbl.gov/traces/NASA_access_log_Aug95.gz](ftp://ita.ee.lbl.gov/traces/NASA_access_log_Aug95.gz),
parses it, and generates statistics regarding the requests.

Statistics are accessible via a REST API, which returns each set of information in JSON format.

## Usage
When the application starts, a "***Performing FTP request ...***" message is printed to the console.
As long as this is the last thing that is displayed, the log file is still being downloaded and parsed and the application hasn't started.

As soon as the FTP request finishes, an "***FTP request completed***" message is written to the console.

Any parsing errors are displayed in the console afterwards, in the form of:
> Line: 914, Request could not be parsed, Request string: columbia.acc.brad.ac.uk - - [01/Aug/1995:00:34:55 -0400] "GET /ksc.html" 200 7280
> Line: 23643, Invalid host: derec, Request string: \derec - - [01/Aug/1995:11:53:44 -0400] "GET /ksc.html HTTP/1.0" 200 7280

When running the project on localhost, calling each of the available URLs returns the following information:
- http://localhost:8080/top10Resources
The top 10 resources that were requested and the number of calls to each resource, ordered by the number of calls descending.
- http://localhost:8080/successPercentage
The percentage of successful requests (i.e. requests with a response code like 2xx or 3xx).
- http://localhost:8080/failPercentage
The percentage of failed requests (i.e. requests with a response code not like 2xx or 3xx, including requests that couldn't be parsed).
- http://localhost:8080/top10FailingResources
The top 10 resources in failure frequency.
- http://localhost:8080/top10Hosts
The top 10 hosts in total number of requests (includes the hostname/IP and the number of requests made by each host).
- http://localhost:8080/top5RequestsForTop10Hosts
Sets of 5 requests selected for their frequency, performed by the top 10 hosts in total number of requests.
- http://localhost:8080/logs
Summary including all the information mentioned above, added for testing.

## Dependencies
- [Spring Boot](https://mvnrepository.com/artifact/org.springframework.boot)
- [Apache Commons Net 3.8](https://mvnrepository.com/artifact/commons-net/commons-net/3.8.0)
- [JSON Small and Fast Parser](https://mvnrepository.com/artifact/net.minidev/json-smart)

## Assumptions
- We care about the speed of each response more than we care about having updated data, thus the log file is downloaded and unzipped only once, during application start.
- The application should not start if the log file can't be retrieved.
- Requests that can't be parsed are considered failed requests for the purposes of failed percentage calculation.
- A hostname/IP is considered invalid if it couldn't be parsed or it doesn't contain the '.' character at least once (e.g. *remote50.compusmart.ab.ca* and *128.159.146.92* are both valid, but *\derec* is not)
- HTTP verbs, resources and response codes are considered invalid if they couldn't be parsed correctly.
- When the "top X" is mentioned, it refers to the appearance frequency of that parameter within the log file.
- Percentages are calculated in relation to the total number of requests, regardless if they could be parsed correctly or not.
