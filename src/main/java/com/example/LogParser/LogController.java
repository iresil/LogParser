package com.example.LogParser;

import net.minidev.json.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
class LogController {

    private DataHolder data = new DataHolder();

    LogController() {
        byte[] bytes = LogParserApplication.ftpResponse;
        LogParser parser = new LogParser();
        List<RequestModel> result = parser.unZipFile(bytes);

        data = StatisticsCalculator.createBaseDataHolder(result);
        data.resourcesSortedByFrequency = StatisticsCalculator.sortResourcesByFrequency(data.resourceCallCount);
        data.failedResourcesSortedByFrequency = StatisticsCalculator.sortFailedResourcesByFrequency(data.resourceFailCount);
        data.hostsSortedByCallFrequency = StatisticsCalculator.sortHostsByRequestFrequency(data.requestsPerHost);
        data.top10HostResources = StatisticsCalculator.getAllRequestsForTopHosts(data.hostsSortedByCallFrequency);
        data.top10FailedResources = StatisticsCalculator.getFrequentlyFailingResources(data.failedResourcesSortedByFrequency);
        data.top10HostRequests = StatisticsCalculator.getFrequentRequestsPerHost(data.top10HostResources);
    }

    @GetMapping("/logs")
    JSONObject logs() {
        JSONObject object = new JSONObject();
        object.appendField("most requested", getMostRequestedResources());
        object.appendField("successful", getSuccessfulRequestPercentage());
        object.appendField("failed", getFailedRequestPercentage());
        object.appendField("frequent resources", getFrequentlyFailingResources());
        object.appendField("frequent hosts", getFrequentlyAppearingHosts());
        object.appendField("frequent requests for frequent hosts", getFrequentRequestsForFrequentlyAppearingHosts());

        return object;
    }

    @GetMapping("/top10Resources")
    JSONArray getMostRequestedResources() {
        JSONArray top10Resources = new JSONArray();
        JSONObject resource;
        List<Map.Entry> entries = data.resourcesSortedByFrequency.stream().limit(10).collect(Collectors.toList());
        for (Map.Entry entry : entries) {
            resource = new JSONObject();
            resource.appendField("resource", entry.getKey());
            resource.appendField("requests", entry.getValue());
            top10Resources.add(resource);
        }
        return top10Resources;
    }

    @GetMapping("/successPercentage")
    JSONObject getSuccessfulRequestPercentage() {
        JSONObject successfulPercentage = new JSONObject();
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(3);
        Double percentage = (data.successfulRequests * 1.0 / data.allRequests) * 100;
        successfulPercentage.appendField("successful request percentage", nf.format(percentage));
        return successfulPercentage;
    }

    @GetMapping("/failPercentage")
    JSONObject getFailedRequestPercentage() {
        JSONObject failedPercentage = new JSONObject();
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(3);
        int failedRequests = data.allRequests - data.successfulRequests;
        Double percentage = ((failedRequests) * 1.0 / data.allRequests) * 100;
        failedPercentage.appendField("failed request percentage", nf.format(percentage));
        return failedPercentage;
    }

    @GetMapping("/top10FailingResources")
    JSONArray getFrequentlyFailingResources() {
        JSONArray top10Failed = new JSONArray();
        JSONObject failedRequest;
        for (String request : data.top10FailedResources) {
            failedRequest = new JSONObject();
            failedRequest.appendField("resource", request);
            top10Failed.add(failedRequest);
        }
        return top10Failed;
    }

    @GetMapping("/top10Hosts")
    JSONArray getFrequentlyAppearingHosts() {
        JSONArray top10Hosts = new JSONArray();
        JSONObject host;
        for (Map.Entry request : data.top10HostResources) {
            host = new JSONObject();
            host.appendField("host", request.getKey());
            host.appendField("requests", ((List<String>)request.getValue()).size());
            top10Hosts.add(host);
        }
        return top10Hosts;
    }

    @GetMapping("/top5RequestsForTop10Hosts")
    JSONArray getFrequentRequestsForFrequentlyAppearingHosts() {
        JSONArray top10HostsTop5Requests = new JSONArray();
        JSONObject hostObject;
        JSONArray hostRequests = new JSONArray();
        JSONObject hostRequestObj;
        for (Map.Entry hostRequest : new ArrayList<Map.Entry>(data.top10HostRequests.entrySet())) {
            hostObject = new JSONObject();
            hostObject.appendField("host", hostRequest.getKey());
            hostRequests = new JSONArray();
            for (Map.Entry hostRequestCount : (List<Map.Entry>)hostRequest.getValue()) {
                hostRequestObj = new JSONObject();
                hostRequestObj.appendField("resource", hostRequestCount.getKey());
                hostRequestObj.appendField("count", hostRequestCount.getValue());
                hostRequests.add(hostRequestObj);
            }
            hostObject.appendField("requests", hostRequests);
            top10HostsTop5Requests.add(hostObject);
        }
        return top10HostsTop5Requests;
    }
}