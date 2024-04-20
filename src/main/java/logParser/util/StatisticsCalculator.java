package logParser.util;

import logParser.dataModel.RequestModel;
import logParser.dataModel.DataHolder;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCalculator {
    /**
     * Loops through all parsed RequestModels and retrieves the number of times each resource was called,
     * all requests that were made per host and the number of successful requests.
     * @param input A List of parsed RequestModels
     * @return A DataHolder object with its allRequests, resourceCallCount, resourceFailCount, requestsPerHost
     * and successfulRequests fields already filled
     */
    public static DataHolder createBaseDataHolder(List<RequestModel> input) {
        DataHolder output = new DataHolder();
        output.setAllRequests(input.size());
        HashMap<String, Integer> resourceCallCount = output.getResourceCallCount();
        HashMap<String, List<String>> requestsPerHost = output.getRequestsPerHost();
        HashMap<String, Integer> resourceFailCount = output.getResourceFailCount();
        int successfulRequests = 0;
        for (RequestModel rm : input) {
            String resource = rm.getResource();
            resourceCallCount.put(resource, resourceCallCount.getOrDefault(resource, 0) + 1);
            output.setResourceCallCount(resourceCallCount);

            String host = rm.getHost();
            if (!requestsPerHost.containsKey(host)) {
                requestsPerHost.put(host, new ArrayList<>());
            }
            requestsPerHost.get(host).add(resource);

            if (rm.isSuccessful()) {
                successfulRequests++;
            } else {
                resourceFailCount.put(resource, resourceFailCount.getOrDefault(resource, 0) + 1);
            }
        }
        output.setResourceCallCount(resourceCallCount);
        output.setRequestsPerHost(requestsPerHost);
        output.setResourceFailCount(resourceFailCount);
        output.setSuccessfulRequests(successfulRequests);
        return output;
    }

    /**
     * Sorts the resources contained in resourceCallCount by number of calls performed
     * @param resourceCallCount A HashMap of Strings and Integers containing the number of times each resource was called
     * @return A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     */
    public static LinkedHashMap<String, Integer> sortResourcesByFrequency(HashMap<String, Integer> resourceCallCount) {
        return resourceCallCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.nullsLast((e1, e2) -> e2 - e1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
    }

    /**
     * Sorts the resources contained in resourceCallCount by number of calls performed
     * @param resourceFailCount A HashMap of Strings and Integers containing the number of times each resource was called and failed
     * @return A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     */
    public static LinkedHashMap<String, Integer> sortFailedResourcesByFrequency(HashMap<String, Integer> resourceFailCount) {
        return resourceFailCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.nullsLast((e1, e2) -> e2 - e1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
    }

    /**
     * Sorts the hosts contained in requestsPerHost by number of requests performed
     * @param requestsPerHost A HashMap of Strings and Lists of Strings containing the requests that were made by each host
     * @return A List of Entries containing Strings and Lists of Strings, sorted by the inner Lists' size, descending
     */
    public static LinkedHashMap<String, List<String>> sortHostsByRequestFrequency(HashMap<String, List<String>> requestsPerHost) {
        return requestsPerHost.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.nullsLast((e1, e2) -> e2.size() - e1.size())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
    }

    /**
     * Retrieves the requested resources for the top 10 most frequently appearing hosts
     * @param hostsSortedByCallFrequency A List of Entries containing Strings and Lists of Strings, sorted by the inner Lists' size, descending
     * @return The first 10 entries found in the input
     */
    public static LinkedHashMap<String, List<String>> getAllRequestsForTopHosts(LinkedHashMap<String, List<String>> hostsSortedByCallFrequency) {
        return hostsSortedByCallFrequency.entrySet().stream().limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
    }

    /**
     * Retrieves the top 10 most often failing resources
     * @param failedResourcesSortedByFrequency A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     * @return The first 10 values that contain failed requests
     */
    public static String[] getFrequentlyFailingResources(LinkedHashMap<String, Integer> failedResourcesSortedByFrequency) {
        String[] top10FailedResources = new String[10];
        int i = 0;
        for (Map.Entry<String, Integer> res : failedResourcesSortedByFrequency.entrySet()) {
            if (i < 10 && Arrays.stream(top10FailedResources).noneMatch(x -> x == res.getKey())) {
                top10FailedResources[i] = res.getKey();
                i++;
            }
        }

        return top10FailedResources;
    }

    /**
     * Calculates the number of calls for the top 5 resources that were called by the top 10 hosts
     * @param top10HostResources A List of Entries containing Strings and Lists of Strings, corresponding to the top 10 hosts in frequency
     * @return A HashMap of Strings and Lists of Entries containing Strings and Integers, corresponding to the top 5 requested resources for each of the top 10 hosts
     */
    public static LinkedHashMap<String, LinkedHashMap<String, Long>> getFrequentRequestsPerHost(LinkedHashMap<String, List<String>> top10HostResources) {
        LinkedHashMap<String, LinkedHashMap<String, Long>> top10HostRequests = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> request : top10HostResources.entrySet()) {
            Map<String, Long> hostRequests = request.getValue().stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            LinkedHashMap<String, Long> sortedRequestsFrequency = hostRequests.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.nullsLast((e1, e2) -> (e2.intValue() - e1.intValue()))))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));

            top10HostRequests.put(request.getKey(), sortedRequestsFrequency.entrySet().stream().limit(5)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new)));
        }
        return top10HostRequests;
    }
}
