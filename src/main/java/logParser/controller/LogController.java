package logParser.controller;

import jakarta.annotation.PostConstruct;
import logParser.domainModel.StatisticsContainer;
import logParser.dataModel.RequestEntity;
import logParser.repository.RequestRepository;
import logParser.util.StatisticsCalculator;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class LogController {

    @Autowired
    private RequestRepository requestRepository;

    private StatisticsContainer data;

    @PostConstruct
    public void initialize() {
        System.out.println("Retrieving requests from H2 database ...");
        List<RequestEntity> result = StreamSupport.stream(requestRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        System.out.println("Creating base StatisticsContainer ...");

        if (!result.isEmpty()) {
            data = StatisticsCalculator.createBaseStatisticsContainer(result);
            data.setResourcesSortedByFrequency(StatisticsCalculator.sortResourcesByFrequency(data.getResourceCallCount()));
            data.setFailedResourcesSortedByFrequency(StatisticsCalculator.sortFailedResourcesByFrequency(data.getResourceFailCount()));
            data.setHostsSortedByCallFrequency(StatisticsCalculator.sortHostsByRequestFrequency(data.getRequestsPerHost()));
            data.setTop10HostResources(StatisticsCalculator.getAllRequestsForTopHosts(data.getHostsSortedByCallFrequency()));
            data.setTop10FailedResources(StatisticsCalculator.getFrequentlyFailingResources(data.getFailedResourcesSortedByFrequency()));
            data.setTop10HostRequests(StatisticsCalculator.getFrequentRequestsPerHost(data.getTop10HostResources()));
        }
        System.out.println("Controller initialized");
    }

    /**
     * Single endpoint containing the concatenated results of all other endpoints.
     * @return A JSONObject with the following fields:
     * <ul>
     *   <li>most requested
     *   <li>successful
     *   <li>failed
     *   <li>frequent resources
     *   <li>frequent hosts
     *   <li>frequent requests for frequent hosts
     * </ul>
     */
    @GetMapping("/logs")
    public JSONObject logs() {
        JSONObject object = new JSONObject();
        object.appendField("most requested", getMostRequestedResources());
        object.appendField("successful", getSuccessfulRequestPercentage());
        object.appendField("failed", getFailedRequestPercentage());
        object.appendField("frequent resources", getFrequentlyFailingResources());
        object.appendField("frequent hosts", getFrequentlyAppearingHosts());
        object.appendField("frequent requests for frequent hosts", getFrequentRequestsForFrequentlyAppearingHosts());

        return object;
    }

    /**
     * The top 10 resources that were requested and the number of calls to each resource, sorted by the number of calls,
     * descending.
     * @return A JSONArray with 10 entries
     */
    @GetMapping("/top10Resources")
    public JSONArray getMostRequestedResources() {
        JSONArray top10Resources = new JSONArray();
        if (data != null) {
            JSONObject resource;
            LinkedHashMap<String, Integer> entries = data.getResourcesSortedByFrequency().entrySet().stream().limit(10)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
            for (Map.Entry<String, Integer> entry : entries.entrySet()) {
                resource = new JSONObject();
                resource.appendField("resource", entry.getKey());
                resource.appendField("requests", entry.getValue());
                top10Resources.add(resource);
            }
        }
        return top10Resources;
    }

    /**
     * The percentage of successful requests (i.e. requests with a response code like 2xx or 3xx).
     * @return A single JSONObject with "successful request percentage" as its field
     */
    @GetMapping("/successPercentage")
    public JSONObject getSuccessfulRequestPercentage() {
        JSONObject successfulPercentage = new JSONObject();
        if (data != null) {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            nf.setMaximumFractionDigits(3);
            Double percentage = (data.getSuccessfulRequests() * 1.0 / data.getAllRequests()) * 100;
            successfulPercentage.appendField("successful request percentage", nf.format(percentage));
        }
        return successfulPercentage;
    }

    /**
     * The percentage of failed requests (i.e. requests with a response code not like 2xx or 3xx, including requests
     * that couldn't be parsed).
     * @return A single JSONObject with "failed request percentage" as its field
     */
    @GetMapping("/failPercentage")
    public JSONObject getFailedRequestPercentage() {
        JSONObject failedPercentage = new JSONObject();
        if (data != null) {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            nf.setMaximumFractionDigits(3);
            int failedRequests = data.getAllRequests() - data.getSuccessfulRequests();
            Double percentage = ((failedRequests) * 1.0 / data.getAllRequests()) * 100;
            failedPercentage.appendField("failed request percentage", nf.format(percentage));
        }
        return failedPercentage;
    }

    /**
     * The top 10 failing resources, sorted by failure frequency, descending.
     * @return A JSONArray with 10 entries
     */
    @GetMapping("/top10FailingResources")
    public JSONArray getFrequentlyFailingResources() {
        JSONArray top10Failed = new JSONArray();
        if (data != null) {
            JSONObject failedRequest;
            for (String request : data.getTop10FailedResources()) {
                failedRequest = new JSONObject();
                failedRequest.appendField("resource", request);
                top10Failed.add(failedRequest);
            }
        }
        return top10Failed;
    }

    /**
     * The top 10 hosts with the most requests (includes the hostname/IP and the number of requests made by each host).
     * @return A JSONArray with 10 entries
     */
    @GetMapping("/top10Hosts")
    public JSONArray getFrequentlyAppearingHosts() {
        JSONArray top10Hosts = new JSONArray();
        if (data != null) {
            JSONObject host;
            for (Map.Entry<String, List<String>> request : data.getTop10HostResources().entrySet()) {
                host = new JSONObject();
                host.appendField("host", request.getKey());
                host.appendField("requests", request.getValue().size());
                top10Hosts.add(host);
            }
        }
        return top10Hosts;
    }

    /**
     * The top 5 most often requested resources for each of the top 10 hosts with the most requests.
     * @return A JSONArray with 10 entries, each of which contains a JSONArray with 5 entries
     */
    @GetMapping("/top5RequestsForTop10Hosts")
    public JSONArray getFrequentRequestsForFrequentlyAppearingHosts() {
        JSONArray top10HostsTop5Requests = new JSONArray();
        if (data != null) {
            JSONObject hostObject;
            JSONArray hostRequests;
            JSONObject hostRequestObj;
            for (Map.Entry<String, LinkedHashMap<String, Long>> hostRequest : new ArrayList<>(data.getTop10HostRequests().entrySet())) {
                hostObject = new JSONObject();
                hostObject.appendField("host", hostRequest.getKey());
                hostRequests = new JSONArray();
                for (Map.Entry<String, Long> hostRequestCount : hostRequest.getValue().entrySet()) {
                    hostRequestObj = new JSONObject();
                    hostRequestObj.appendField("resource", hostRequestCount.getKey());
                    hostRequestObj.appendField("count", hostRequestCount.getValue());
                    hostRequests.add(hostRequestObj);
                }
                hostObject.appendField("requests", hostRequests);
                top10HostsTop5Requests.add(hostObject);
            }
        }
        return top10HostsTop5Requests;
    }
}