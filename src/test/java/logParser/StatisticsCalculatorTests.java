package logParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import logParser.dataModel.RequestEntity;
import logParser.domainModel.StatisticsContainer;
import logParser.util.StatisticsCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class StatisticsCalculatorTests {
    @Test
    void createBaseStatisticsContainerValidInputSuccess_FillsExpectedValues() {
        List<RequestEntity> input = new ArrayList<>();
        RequestEntity model = new RequestEntity();
        model.setHost("in24.inetnebr.com");
        model.setHttpVerb("GET");
        model.setResource("/shuttle/missions/sts-68/news/sts-68-mcc-05.txt");
        model.setResponseCode("200");
        input.add(model);
        StatisticsContainer output = StatisticsCalculator.createBaseStatisticsContainer(input);

        assertNotEquals(new StatisticsContainer(), output);
        assertEquals(1, output.getAllRequests());
        assertEquals(1, output.getSuccessfulRequests());
        assertEquals(1, output.getResourceCallCount().size());
        assertEquals(0, output.getResourceFailCount().size());
        assertEquals(1, output.getRequestsPerHost().size());
    }

    @Test
    void createBaseStatisticsContainerValidInputFailure_FillsExpectedValues() {
        List<RequestEntity> input = new ArrayList<>();
        RequestEntity model = new RequestEntity();
        model.setHost("js002.cc.utsunomiya-u.ac.jp");
        model.setHttpVerb("GET");
        model.setResource("/shuttle/resources/orbiters/discovery.gif");
        model.setResponseCode("404");
        input.add(model);
        StatisticsContainer output = StatisticsCalculator.createBaseStatisticsContainer(input);

        assertNotEquals(new StatisticsContainer(), output);
        assertEquals(1, output.getAllRequests());
        assertEquals(0, output.getSuccessfulRequests());
        assertEquals(1, output.getResourceCallCount().size());
        assertEquals(1, output.getResourceFailCount().size());
        assertEquals(1, output.getRequestsPerHost().size());
    }

    @Test
    void createBaseStatisticsContainerInvalidRequestInput_FillsExpectedValues() {
        List<RequestEntity> input = new ArrayList<>();
        RequestEntity model = new RequestEntity();
        input.add(model);
        StatisticsContainer output = StatisticsCalculator.createBaseStatisticsContainer(input);

        assertNotEquals(null, output);
        assertEquals(1, output.getAllRequests());
        assertEquals(0, output.getSuccessfulRequests());
        assertEquals(1, output.getResourceCallCount().size());
        assertEquals(1, output.getResourceFailCount().size());
        assertEquals(1, output.getRequestsPerHost().size());
    }

    @Test
    void createBaseStatisticsContainerInvalidInput_FillsExpectedFields() {
        List<RequestEntity> input = new ArrayList<>();
        StatisticsContainer output = StatisticsCalculator.createBaseStatisticsContainer(input);

        assertNotEquals(null, output);
        assertEquals(0, output.getAllRequests());
        assertEquals(0, output.getSuccessfulRequests());
        assertEquals(0, output.getResourceCallCount().size());
        assertEquals(0, output.getResourceFailCount().size());
        assertEquals(0, output.getRequestsPerHost().size());
    }

    @Test
    void createBaseStatisticsContainerNullInput_ThrowsException() {
        List<RequestEntity> input = null;
        Throwable thrown = catchThrowable(() -> StatisticsCalculator.createBaseStatisticsContainer(input));

        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    void sortResourcesByFrequency_SortsDescending() {
        HashMap<String,Integer> input = new HashMap<>();
        input.put("/images/MOSAIC-logosmall.gif", 3);
        input.put("/images/ksclogo-medium.gif", 1);
        input.put("/", 8);
        LinkedHashMap<String, Integer> output = StatisticsCalculator.sortFailedResourcesByFrequency(input);

        List<String> keys = output.keySet().stream().toList();
        for (int i = 1; i < output.size(); i++) {
            assertThat(output.get(keys.get(i)) < output.get(keys.get(i-1)));
        }
    }

    @Test
    void getAllRequestsForTopHostsCompleteInput_ReturnsTop10() {
        List<RequestEntity> input = new ArrayList<>();
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-71/movies/sts-71-rollover.mpg", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("uplherc.upl.com", "GET", "/", "200"));
        input.add(new RequestEntity("uplherc.upl.com", "GET", "/images/MOSAIC-logosmall.gif", "200"));
        input.add(new RequestEntity("van15422.direct.ca", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("van15422.direct.ca", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("piweba1y.prodigy.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("ad11-061.compuserve.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("139.230.35.135", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("pm9.j51.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("piweba4y.prodigy.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("www-b5.proxy.aol.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("ns2.sharp.co.jp", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        StatisticsContainer statisticsContainer = StatisticsCalculator.createBaseStatisticsContainer(input);
        statisticsContainer.setHostsSortedByCallFrequency(StatisticsCalculator.sortHostsByRequestFrequency(statisticsContainer.getRequestsPerHost()));
        LinkedHashMap<String, List<String>> output = StatisticsCalculator.getAllRequestsForTopHosts(statisticsContainer.getHostsSortedByCallFrequency());

        assertThat(output.size() == 10);
    }

    @Test
    void getAllRequestsForTopHostsIncompleteInput_ReturnsFewerThan10Entries() {
        List<RequestEntity> input = new ArrayList<>();
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-71/movies/sts-71-rollover.mpg", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/", "200"));
        input.add(new RequestEntity("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestEntity("uplherc.upl.com", "GET", "/", "200"));
        input.add(new RequestEntity("uplherc.upl.com", "GET", "/images/MOSAIC-logosmall.gif", "200"));
        StatisticsContainer statisticsContainer = StatisticsCalculator.createBaseStatisticsContainer(input);
        statisticsContainer.setHostsSortedByCallFrequency(StatisticsCalculator.sortHostsByRequestFrequency(statisticsContainer.getRequestsPerHost()));
        LinkedHashMap<String, List<String>> output = StatisticsCalculator.getAllRequestsForTopHosts(statisticsContainer.getHostsSortedByCallFrequency());

        assertThat(output.size() == 2);
    }

    @Test
    void getAllRequestsForTopHostsInvalidInput_ReturnsEmptyList() {
        LinkedHashMap<String, List<String>> output = StatisticsCalculator.getAllRequestsForTopHosts(new LinkedHashMap<>());

        assertThat(output.size() == 0);
    }

    @Test
    void getAllRequestsForTopHostsNullInput_ThrowsException() {
        Throwable thrown = catchThrowable(() -> StatisticsCalculator.getAllRequestsForTopHosts(null));

        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }
}
