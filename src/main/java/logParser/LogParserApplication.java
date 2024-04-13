package logParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogParserApplication {

	public static byte[] ftpResponse = null;

	static{
		System.out.println("Performing FTP request ...");
		LogGetter getter = new LogGetter();
		LogParserApplication.ftpResponse = getter.GetLogs();
		System.out.println("FTP request completed");
	}

	public static void main(String[] args) {
		SpringApplication.run(LogParserApplication.class, args);
	}

}
