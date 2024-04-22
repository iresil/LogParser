package logParser.domainModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
public class StatisticsContainer {
    /**
     * How many times each resource was called
     */
    private HashMap<String, Integer> resourceCallCount = new HashMap<>();

    /**
     * Which requests were made per host
     */
    private HashMap<String, List<String>> requestsPerHost = new HashMap<>();

    /**
     * The total number of requests performed
     */
    private Integer allRequests = 0;

    /**
     * The total number of successful requests
     */
    private Integer successfulRequests = 0;

    /**
     * How many times each resource request failed
     */
    private HashMap<String, Integer> resourceFailCount = new HashMap<>();

    /**
     * How many times each resource was called, sorted by frequency descending
     */
    private LinkedHashMap<String, Integer> resourcesSortedByFrequency;

    /**
     * How many times each resource was called and failed, sorted by failure frequency descending
     */
    private LinkedHashMap<String, Integer> failedResourcesSortedByFrequency;

    /**
     * Which requests were made per host, sorted by frequency descending
     */
    private LinkedHashMap<String, List<String>> hostsSortedByCallFrequency;

    /**
     * Which requests were made for the top 10 hosts in frequency
     */
    private LinkedHashMap<String, List<String>> top10HostResources;

    /**
     * The top 10 resources that fail more often
     */
    private String[] top10FailedResources = new String[10];

    /**
     * How frequently each resource gets called, for the top 10 hosts
     */
    private LinkedHashMap<String, LinkedHashMap<String, Long>> top10HostRequests = new LinkedHashMap<>();
}
