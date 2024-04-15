package logParser;

import jakarta.annotation.PostConstruct;
import logParser.util.LogLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogParserApplication {

	@Autowired
	public LogLoader getter;

	public static byte[] ftpResponse = null;

	@PostConstruct
	public void requestOnce() {
		System.out.println("Performing FTP request ...");
		LogParserApplication.ftpResponse = getter.loadLogs();
		System.out.println("FTP request completed");
	}

	public static void main(String[] args) {
		SpringApplication.run(LogParserApplication.class, args);
	}

}
