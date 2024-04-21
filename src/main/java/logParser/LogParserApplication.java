package logParser;

import jakarta.annotation.PostConstruct;
import logParser.dataModel.RequestModel;
import logParser.repository.RequestRepository;
import logParser.util.LogLoader;
import logParser.util.LogParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class LogParserApplication {

    @Autowired
    private LogLoader loader;
    @Autowired
    private LogParser parser;
    @Autowired
    private RequestRepository requestRepository;

    @PostConstruct
    public void initialize() {
        if (requestRepository.count() == 0) {
            System.out.println("Performing FTP request ...");
            byte[] ftpResponse = loader.loadLogs();
            System.out.println("FTP request completed");
            List<RequestModel> result = parser.unZipFile(ftpResponse);
            System.out.println("FTP response unzipped");
            System.out.println("Storing requests in H2 database ...");
            requestRepository.saveAll(result);
            System.out.printf("Stored %d requests in H2 database%n", result.size());
        }
        System.out.println("Application initialized");
    }

    public static void main(String[] args) {
        SpringApplication.run(LogParserApplication.class, args);
    }

}
