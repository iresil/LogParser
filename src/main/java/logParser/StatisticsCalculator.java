package logParser;

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
        output.allRequests = input.size();
        for (RequestModel rm : input) {
            output.resourceCallCount.put(rm.resource, output.resourceCallCount.getOrDefault(rm.resource, 0) + 1);

            if (!output.requestsPerHost.containsKey(rm.host)) {
                output.requestsPerHost.put(rm.host, new ArrayList<>());
            }
            output.requestsPerHost.get(rm.host).add(rm.resource);

            if (rm.isSuccessful()) {
                output.successfulRequests++;
            } else {
                output.resourceFailCount.put(rm.resource, output.resourceFailCount.getOrDefault(rm.resource, 0) + 1);
            }
        }
        return output;
    }

    /**
     * Sorts the resources contained in resourceCallCount by number of calls performed
     * @param resourceCallCount A HashMap of Strings and Integers containing the number of times each resource was called
     * @return A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     */
    public static List<Map.Entry> sortResourcesByFrequency(HashMap<String,Integer> resourceCallCount) {
        List<Map.Entry> resourcesSortedByFrequency = new ArrayList<>();
        resourcesSortedByFrequency = new ArrayList<>(resourceCallCount.entrySet());
        Collections.sort(resourcesSortedByFrequency, Comparator.nullsLast(new Comparator<>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                return (Integer)e2.getValue() - (Integer)e1.getValue();
            }
        }));
        return resourcesSortedByFrequency;
    }

    /**
     * Sorts the resources contained in resourceCallCount by number of calls performed
     * @param resourceFailCount A HashMap of Strings and Integers containing the number of times each resource was called and failed
     * @return A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     */
    public static List<Map.Entry> sortFailedResourcesByFrequency(HashMap<String,Integer> resourceFailCount) {
        List<Map.Entry> failedResourcesSortedByFrequency = new ArrayList<>();
        failedResourcesSortedByFrequency = new ArrayList<>(resourceFailCount.entrySet());
        Collections.sort(failedResourcesSortedByFrequency, Comparator.nullsLast(new Comparator<>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                return (Integer)e2.getValue() - (Integer)e1.getValue();
            }
        }));
        return failedResourcesSortedByFrequency;
    }

    /**
     * Sorts the hosts contained in requestsPerHost by number of requests performed
     * @param requestsPerHost A HashMap of Strings and Lists of Strings containing the requests that were made by each host
     * @return A List of Entries containing Strings and Lists of Strings, sorted by the inner Lists' size, descending
     */
    public static List<Map.Entry> sortHostsByRequestFrequency(HashMap<String, List<String>> requestsPerHost) {
        List<Map.Entry> hostsSortedByCallFrequency = new ArrayList<>(requestsPerHost.entrySet());
        Collections.sort(hostsSortedByCallFrequency, Comparator.nullsLast(new Comparator<>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                return ((List<String>)e2.getValue()).size() - ((List<String>)e1.getValue()).size();
            }
        }));
        return hostsSortedByCallFrequency;
    }

    /**
     * Retrieves the requested resources for the top 10 most frequently appearing hosts
     * @param hostsSortedByCallFrequency A List of Entries containing Strings and Lists of Strings, sorted by the inner Lists' size, descending
     * @return The first 10 entries found in the input
     */
    public static List<Map.Entry> getAllRequestsForTopHosts(List<Map.Entry> hostsSortedByCallFrequency) {
        return hostsSortedByCallFrequency.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * Retrieves the top 10 most often failing resources
     * @param failedResourcesSortedByFrequency A List of Entries containing Strings and Integers, sorted by the Integer values, descending
     * @return The first 10 values that contain failed requests
     */
    public static String[] getFrequentlyFailingResources(List<Map.Entry> failedResourcesSortedByFrequency) {
        String[] top10FailedResources = new String[10];
        int i = 0;
        for (Map.Entry res : failedResourcesSortedByFrequency) {
            if (i < 10 && !Arrays.stream(top10FailedResources).anyMatch(x -> x == res.getKey())) {
                top10FailedResources[i] = (String) res.getKey();
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
    public static HashMap<String, List<Map.Entry>> getFrequentRequestsPerHost(List<Map.Entry> top10HostResources) {
        HashMap<String, List<Map.Entry>> top10HostRequests = new HashMap<>();
        for (Map.Entry request : top10HostResources) {
            Map<Object, Long> hostRequests = ((List<String>)request.getValue()).stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            List<Map.Entry> sortedRequestFrequency = new ArrayList<>(hostRequests.entrySet());
            Collections.sort(sortedRequestFrequency, new Comparator<>() {
                @Override
                public int compare(Map.Entry e1, Map.Entry e2) {
                    return ((Long)e2.getValue()).intValue() - ((Long)e1.getValue()).intValue();
                }
            });
            top10HostRequests.put((String)request.getKey(), sortedRequestFrequency.stream().limit(5).collect(Collectors.toList()));
        }
        return top10HostRequests;
    }
}
