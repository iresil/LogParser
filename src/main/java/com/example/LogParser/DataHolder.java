package com.example.LogParser;

import java.util.*;

public class DataHolder {
    /**
     * How many times each resource was called
     */
    HashMap<String, Integer> resourceCallCount = new HashMap<>();

    /**
     * Which requests were made per host
     */
    HashMap<String, List<String>> requestsPerHost = new HashMap<>();

    /**
     * The total number of requests performed
     */
    Integer allRequests = 0;

    /**
     * The total number of successful requests
     */
    Integer successfulRequests = 0;

    /**
     * How many times each resource request failed
     */
    HashMap<String, Integer> resourceFailCount = new HashMap<>();

    /**
     * How many times each resource was called, sorted by frequency descending
     */
    List<Map.Entry> resourcesSortedByFrequency;

    /**
     * How many times each resource was called and failed, sorted by failure frequency descending
     */
    List<Map.Entry> failedResourcesSortedByFrequency;

    /**
     * Which requests were made per host, sorted by frequency descending
     */
    List<Map.Entry> hostsSortedByCallFrequency;

    /**
     * Which requests were made for the top 10 hosts in frequency
     */
    List<Map.Entry> top10HostResources;

    /**
     * The top 10 resources that fail more often
     */
    String[] top10FailedResources = new String[10];

    /**
     * How frequently each resource gets called, for the top 10 hosts
     */
    HashMap<String, List<Map.Entry>> top10HostRequests = new HashMap<>();
}
