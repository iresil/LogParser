package logParser;

import logParser.util.LogGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class LogParserApplication {

	@Autowired
	public LogGetter getter;

	public static byte[] ftpResponse = null;

	@PostConstruct
	public void requestOnce() {
		System.out.println("Performing FTP request ...");
		LogParserApplication.ftpResponse = getter.getLogs();
		System.out.println("FTP request completed");
	}

	public static void main(String[] args) {
		SpringApplication.run(LogParserApplication.class, args);
	}

}
