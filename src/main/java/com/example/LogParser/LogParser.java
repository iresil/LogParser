package com.example.LogParser;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;

public class LogParser {
    /**
     * Unzips the contents of a byte array containing a zipped file
     * @param bytes A byte array containing a zipped file
     * @return A List containing each line as a RequestModel
     */
    public List<RequestModel> unZipFile(byte[] bytes) {
        List<RequestModel> result = new ArrayList<RequestModel>();

        try {

            GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            BufferedReader br = new BufferedReader(new InputStreamReader(gZIPInputStream));

            String lineContent;
            int i = 1;
            while ((lineContent = br.readLine()) != null) {
                RequestModel req = parseEntry(lineContent);
                validateEntry(lineContent, i, req);

                result.add(req);
                i++;
            }

            gZIPInputStream.close();

        } catch (IOException ex) {
            System.out.println("LogParser Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * Parses a single line from the original file and translates it to a RequestModel
     * @param input A string containing a single request
     * @return A RequestModel containing the request parameters
     */
    private RequestModel parseEntry(String input) {
        String logEntryPattern = "([^\\s]*)\\s-\\s-\\s\\[[^\\]]*\\]\\s\"([A-Z]*)\\s([^\\s]*)\\s([^\"]*)\"\\s([0-9]*)\\s(.*)";
        Pattern p = Pattern.compile(logEntryPattern);
        Matcher matcher = p.matcher(input);

        RequestModel model = new RequestModel();
        while (matcher.find()) {
            model.host = matcher.group(1);
            model.httpVerb = matcher.group(2);
            model.resource = matcher.group(3);
            model.responseCode = matcher.group(5);
        }

        return model;
    }

    /**
     * Validates the RequestModel that has been parsed from the input
     * @param lineContent The original content of a single line
     * @param line The line number
     * @param req The RequestModel that has already been parsed
     */
    private void validateEntry(String lineContent, Integer line, RequestModel req) {
        if (!req.validFieldsExist()) {
            System.out.println("Line: " + line + ", Request could not be parsed, Request string: " + lineContent);
        }
        else {
            if (!req.isHostValid())
                System.out.println("Line: " + line + ", Invalid host: " + req.host + ", Request string: \\" + lineContent);

            if (!req.isHttpVerbValid())
                System.out.println("Line: " + line + ", Invalid http verb: " + req.httpVerb + ", Request string: \\" + lineContent);

            if (!req.isResourceValid())
                System.out.println("Line: " + line + ", Invalid resource: " + req.resource + ", Request string: \\" + lineContent);

            if (!req.isResponseCodeValid())
                System.out.println("Line: " + line + ", Invalid response code: " + req.responseCode + ", Request string: \\" + lineContent);
        }
    }
}
