package logParser.util;

import logParser.dataModel.RequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;

@Component
public class LogParser {
    private static final Logger logger = LogManager.getLogger(LogParser.class);

    /**
     * Unzips the contents of a byte array containing a zipped file
     * @param bytes A byte array containing a zipped file
     * @return A List containing each line as a RequestEntity
     */
    public List<RequestEntity> unZipFile(byte[] bytes) {
        List<RequestEntity> result = new ArrayList<>();

        try {
            if (bytes != null) {
                GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                BufferedReader br = new BufferedReader(new InputStreamReader(gZIPInputStream));

                String lineContent;
                int i = 1;
                while ((lineContent = br.readLine()) != null) {
                    RequestEntity req = parseEntry(lineContent);
                    validateEntry(lineContent, i, req);

                    result.add(req);
                    i++;
                }

                gZIPInputStream.close();
            }
        } catch (IOException ex) {
            logger.error("LogParser Error: ", ex);
        }

        return result;
    }

    /**
     * Parses a single line from the original file and translates it to a RequestEntity
     * @param input A string containing a single request
     * @return A RequestEntity containing the request parameters
     */
    private RequestEntity parseEntry(String input) {
        String logEntryPattern = "([^\\s]+)\\s-\\s-\\s\\[[^\\]]+\\]\\s\"([A-Z]+)\\s([^\\s]+)\\s([^\"]+)\"\\s([0-9]+)\\s(.+)";
        Pattern p = Pattern.compile(logEntryPattern);
        Matcher matcher = p.matcher(input);

        RequestEntity model = new RequestEntity();
        while (matcher.find()) {
            model.setHost(matcher.group(1));
            model.setHttpVerb(matcher.group(2));
            model.setResource(matcher.group(3));
            model.setResponseCode(matcher.group(5));
        }

        return model;
    }

    /**
     * Validates the RequestEntity that has been parsed from the input
     * @param lineContent The original content of a single line
     * @param line The line number
     * @param req The RequestEntity that has already been parsed
     */
    private void validateEntry(String lineContent, Integer line, RequestEntity req) {
        if (!req.validFieldsExist()) {
            logger.warn("Line: {}}, Request could not be parsed, Request string: {}", line, lineContent);
        }
        else {
            if (!req.isHostValid())
                logger.warn("Line: {}, Invalid host: {}, Request string: {}", line, req.getHost(), lineContent);

            if (!req.isHttpVerbValid())
                logger.warn("Line: {}, Invalid http verb: {}, Request string: {}", line, req.getHttpVerb(), lineContent);

            if (!req.isResourceValid())
                logger.warn("Line: {}, Invalid resource: {}, Request string: {}", line, req.getResource(), lineContent);

            if (!req.isResponseCodeValid())
                logger.warn("Line: {}, Invalid response code: {}, Request string: {}", line, req.getResponseCode(), lineContent);
        }
    }
}
