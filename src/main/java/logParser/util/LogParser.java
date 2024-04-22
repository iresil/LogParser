package logParser.util;

import logParser.dataModel.RequestEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;

@Component
public class LogParser {
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
            System.out.println("LogParser Error: " + ex.getMessage());
            ex.printStackTrace();
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
            System.out.println("Line: " + line + ", Request could not be parsed, Request string: " + lineContent);
        }
        else {
            if (!req.isHostValid())
                System.out.println("Line: " + line + ", Invalid host: " + req.getHost() + ", Request string: \\" + lineContent);

            if (!req.isHttpVerbValid())
                System.out.println("Line: " + line + ", Invalid http verb: " + req.getHttpVerb() + ", Request string: \\" + lineContent);

            if (!req.isResourceValid())
                System.out.println("Line: " + line + ", Invalid resource: " + req.getResource() + ", Request string: \\" + lineContent);

            if (!req.isResponseCodeValid())
                System.out.println("Line: " + line + ", Invalid response code: " + req.getResponseCode() + ", Request string: \\" + lineContent);
        }
    }
}
