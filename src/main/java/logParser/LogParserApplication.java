package logParser;

import jakarta.annotation.PostConstruct;
import logParser.dataModel.RequestEntity;
import logParser.repository.RequestRepository;
import logParser.util.LogLoader;
import logParser.util.LogParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(LogParserApplication.class);

    @PostConstruct
    public void initialize() {
        if (requestRepository.count() == 0) {
            logger.info("Performing FTP request ...");
            byte[] ftpResponse = loader.loadLogs();
            logger.info("FTP request completed");
            List<RequestEntity> result = parser.unZipFile(ftpResponse);
            logger.info("FTP response unzipped");
            logger.info("Storing requests in H2 database ...");
            requestRepository.saveAll(result);
            logger.info("Stored {} requests in H2 database", result.size());
        }
        logger.info("Application initialized");
    }

    public static void main(String[] args) {
        SpringApplication.run(LogParserApplication.class, args);
    }

}
