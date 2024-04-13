package logParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import logParser.DataHolder;
import logParser.RequestModel;
import logParser.StatisticsCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class StatisticsCalculatorTests {
    @Test
    void createBaseDataHolderValidInputSuccess_FillsExpectedValues() {
        List<RequestModel> input = new ArrayList<>();
        RequestModel model = new RequestModel();
        model.host = "in24.inetnebr.com";
        model.httpVerb = "GET";
        model.resource = "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt";
        model.responseCode = "200";
        input.add(model);
        DataHolder output = StatisticsCalculator.createBaseDataHolder(input);

        assertNotEquals(new DataHolder(), output);
        assertEquals(1, output.allRequests);
        assertEquals(1, output.successfulRequests);
        assertEquals(1, output.resourceCallCount.size());
        assertEquals(0, output.resourceFailCount.size());
        assertEquals(1, output.requestsPerHost.size());
    }

    @Test
    void createBaseDataHolderValidInputFailure_FillsExpectedValues() {
        List<RequestModel> input = new ArrayList<>();
        RequestModel model = new RequestModel();
        model.host = "js002.cc.utsunomiya-u.ac.jp";
        model.httpVerb = "GET";
        model.resource = "/shuttle/resources/orbiters/discovery.gif";
        model.responseCode = "404";
        input.add(model);
        DataHolder output = StatisticsCalculator.createBaseDataHolder(input);

        assertNotEquals(new DataHolder(), output);
        assertEquals(1, output.allRequests);
        assertEquals(0, output.successfulRequests);
        assertEquals(1, output.resourceCallCount.size());
        assertEquals(1, output.resourceFailCount.size());
        assertEquals(1, output.requestsPerHost.size());
    }

    @Test
    void createBaseDataHolderInvalidRequestInput_FillsExpectedValues() {
        List<RequestModel> input = new ArrayList<>();
        RequestModel model = new RequestModel();
        input.add(model);
        DataHolder output = StatisticsCalculator.createBaseDataHolder(input);

        assertNotEquals(null, output);
        assertEquals(1, output.allRequests);
        assertEquals(0, output.successfulRequests);
        assertEquals(1, output.resourceCallCount.size());
        assertEquals(1, output.resourceFailCount.size());
        assertEquals(1, output.requestsPerHost.size());
    }

    @Test
    void createBaseDataHolderInvalidInput_FillsExpectedFields() {
        List<RequestModel> input = new ArrayList<>();
        DataHolder output = StatisticsCalculator.createBaseDataHolder(input);

        assertNotEquals(null, output);
        assertEquals(0, output.allRequests);
        assertEquals(0, output.successfulRequests);
        assertEquals(0, output.resourceCallCount.size());
        assertEquals(0, output.resourceFailCount.size());
        assertEquals(0, output.requestsPerHost.size());
    }

    @Test
    void createBaseDataHolderNullInput_ThrowsException() {
        List<RequestModel> input = null;
        Throwable thrown = catchThrowable(() -> StatisticsCalculator.createBaseDataHolder(input));

        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    void sortResourcesByFrequency_SortsDescending() {
        HashMap<String,Integer> input = new HashMap<>();
        input.put("/images/MOSAIC-logosmall.gif", 3);
        input.put("/images/ksclogo-medium.gif", 1);
        input.put("/", 8);
        List<Map.Entry> output = StatisticsCalculator.sortFailedResourcesByFrequency(input);

        for (int i = 1; i < output.size(); i++) {
            assertThat((Integer)output.get(i).getValue() < (Integer)output.get(i-1).getValue());
        }
    }

    @Test
    void getAllRequestsForTopHostsCompleteInput_ReturnsTop10() {
        List<RequestModel> input = new ArrayList<>();
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-71/movies/sts-71-rollover.mpg", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("uplherc.upl.com", "GET", "/", "200"));
        input.add(new RequestModel("uplherc.upl.com", "GET", "/images/MOSAIC-logosmall.gif", "200"));
        input.add(new RequestModel("van15422.direct.ca", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("van15422.direct.ca", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("piweba1y.prodigy.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("ad11-061.compuserve.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("139.230.35.135", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("pm9.j51.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("piweba4y.prodigy.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("www-b5.proxy.aol.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("ns2.sharp.co.jp", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        DataHolder dataHolder = StatisticsCalculator.createBaseDataHolder(input);
        dataHolder.hostsSortedByCallFrequency = StatisticsCalculator.sortHostsByRequestFrequency(dataHolder.requestsPerHost);
        List<Map.Entry> output = StatisticsCalculator.getAllRequestsForTopHosts(dataHolder.hostsSortedByCallFrequency);

        assertThat(output.size() == 10);
    }

    @Test
    void getAllRequestsForTopHostsIncompleteInput_ReturnsFewerThan10Entries() {
        List<RequestModel> input = new ArrayList<>();
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-71/movies/sts-71-rollover.mpg", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/", "200"));
        input.add(new RequestModel("in24.inetnebr.com", "GET", "/shuttle/missions/sts-68/news/sts-68-mcc-05.txt", "200"));
        input.add(new RequestModel("uplherc.upl.com", "GET", "/", "200"));
        input.add(new RequestModel("uplherc.upl.com", "GET", "/images/MOSAIC-logosmall.gif", "200"));
        DataHolder dataHolder = StatisticsCalculator.createBaseDataHolder(input);
        dataHolder.hostsSortedByCallFrequency = StatisticsCalculator.sortHostsByRequestFrequency(dataHolder.requestsPerHost);
        List<Map.Entry> output = StatisticsCalculator.getAllRequestsForTopHosts(dataHolder.hostsSortedByCallFrequency);

        assertThat(output.size() == 2);
    }

    @Test
    void getAllRequestsForTopHostsInvalidInput_ReturnsEmptyList() {
        List<Map.Entry> output = StatisticsCalculator.getAllRequestsForTopHosts(new ArrayList<>());

        assertThat(output.size() == 0);
    }

    @Test
    void getAllRequestsForTopHostsNullInput_ThrowsException() {
        Throwable thrown = catchThrowable(() -> StatisticsCalculator.getAllRequestsForTopHosts(null));

        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }
}
