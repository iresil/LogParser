package com.example.LogParser;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCalculator {
    /**
     * Loops through all parsed RequestModels and retrieves the number of times each resource was called,
     * all requests that were made per host and the number of successful requests.
     * @param input A List of parsed RequestModels
     * @param output An object containing the response
     */
    public static void fillBasicInfo(List<RequestModel> input, DataHolder output) {
        output.allRequests = input.size();
        for (RequestModel rm : input) {
            output.resourceCallCount.put(rm.resource, output.resourceCallCount.getOrDefault(rm.resource, 0) + 1);

            if (!output.requestsPerHost.containsKey(rm.host)) {
                output.requestsPerHost.put(rm.host, new ArrayList<>());
            }
            output.requestsPerHost.get(rm.host).add(rm.resource);

            if (rm.isSuccessful()) {
                output.successfulRequests++;
            }
        }
    }

    /**
     * Sorts the resources contained in resourceCallCount by number of calls performed
     * (result stored in resourcesSortedByFrequency)
     * @param output An object containing the response
     */
    public static void sortResourcesByFrequency(DataHolder output) {
        output.resourcesSortedByFrequency = new ArrayList<>(output.resourceCallCount.entrySet());
        Collections.sort(output.resourcesSortedByFrequency, Comparator.nullsLast(new Comparator<>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                return (Integer)e2.getValue() - (Integer)e1.getValue();
            }
        }));
    }

    /**
     * Sorts the hosts contained in requestsPerHost by number of requests performed
     * (result stored in hostsSortedByCallFrequency)
     * @param output An object containing the response
     */
    public static void sortHostsByRequestFrequency(DataHolder output) {
        output.hostsSortedByCallFrequency = new ArrayList<>(output.requestsPerHost.entrySet());
        Collections.sort(output.hostsSortedByCallFrequency, Comparator.nullsLast(new Comparator<>() {
            @Override
            public int compare(Map.Entry e1, Map.Entry e2) {
                return ((List<String>)e2.getValue()).size() - ((List<String>)e1.getValue()).size();
            }
        }));
    }

    /**
     * Saves the requested resources for the top 10 most frequently appearing hosts in top10HostResources
     * @param output An object containing the response
     */
    public static void getAllRequestsForTopHosts(DataHolder output) {
        output.top10HostResources = output.hostsSortedByCallFrequency.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * Saves the top 10 most often failing resources in top10FailedResources
     * @param output An object containing the response
     */
    public static void getFrequentlyFailingResources(DataHolder output) {
        int i = 0;
        for (Map.Entry resource : output.resourcesSortedByFrequency) {
            if (i < 10 && !Arrays.stream(output.top10FailedResources).anyMatch(x -> x == resource.getKey())) {
                output.top10FailedResources[i] = (String)resource.getKey();
                i++;
            }
        }
    }

    /**
     * Saves the number of calls for the top 5 resources that were called by the top 10 hosts,
     * in top10HostRequests
     * @param output An object containing the response
     */
    public static void getFrequentRequestsPerHost(DataHolder output) {
        for (Map.Entry request : output.top10HostResources) {
            Map<Object, Long> hostRequests = ((List<String>)request.getValue()).stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            List<Map.Entry> sortedRequestFrequency = new ArrayList<>(hostRequests.entrySet());
            Collections.sort(sortedRequestFrequency, new Comparator<>() {
                @Override
                public int compare(Map.Entry e1, Map.Entry e2) {
                    return ((Long)e2.getValue()).intValue() - ((Long)e1.getValue()).intValue();
                }
            });
            output.top10HostRequests.put((String)request.getKey(), sortedRequestFrequency.stream().limit(5).collect(Collectors.toList()));
        }
    }
}
