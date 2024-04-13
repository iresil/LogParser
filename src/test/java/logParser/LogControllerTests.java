package logParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import logParser.controller.LogController;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.*;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@SpringBootTest
@AutoConfigureMockMvc
class LogControllerTests {

	@Autowired
	private LogController controller;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void context_Loads() {
		assertThat(controller).isNotNull();
	}

	@Test
	void logsGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/logs"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).logs()));

		assertThat(status().is(200));
	}

	@Test
	void top10ResourcesGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/top10Resources"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getMostRequestedResources()));

		assertThat(status().is(200));
	}

	@Test
	void successPercentageGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/successPercentage"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getSuccessfulRequestPercentage()));

		assertThat(status().is(200));
	}

	@Test
	void failPercentageGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/failPercentage"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFailedRequestPercentage()));

		assertThat(status().is(200));
	}

	@Test
	void top10FailingResourcesGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/top10FailingResources"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentlyFailingResources()));

		assertThat(status().is(200));
	}

	@Test
	void top10HostsGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/top10Hosts"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentlyAppearingHosts()));

		assertThat(status().is(200));
	}

	@Test
	void top5RequestsForTop10HostsGet_IsValidAndSucceeds() throws Exception {
		mockMvc.perform(get("/top5RequestsForTop10Hosts"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentRequestsForFrequentlyAppearingHosts()));

		assertThat(status().is(200));
	}

	@Test
	void rootGet_ReturnsNotFound() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(handler().handlerType(ResourceHttpRequestHandler.class));

		assertThat(status().is(404));
	}

	@Test
	void invalidGet_ReturnsNotFound() throws Exception {
		mockMvc.perform(get("/test"))
				.andExpect(handler().handlerType(ResourceHttpRequestHandler.class));

		assertThat(status().is(404));
	}

	@Test
	void top10ResourcesGet_ReturnsJSONArrayWithStringResources() throws Exception {
		MvcResult result = mockMvc.perform(get("/top10Resources"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getMostRequestedResources()))
				.andReturn();
		String expectedEntry = "{a:[{\"resource\":\"X\"}]}";
		String content = "{a:" + result.getResponse().getContentAsString() + "}";

		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("[^\\s]*");  // matches one or more digits
		Customization[] customizations = new Customization[10];
		for (int i=0; i < customizations.length; i++) {
			String contextPath = "a["+i+"].resource";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });

		JSONAssert.assertEquals(expectedEntry, content, regExCustomArrayValueComparator);
	}

	@Test
	void top10ResourcesGet_ReturnsJSONArrayWithIntegerRequestCount() throws Exception {
		MvcResult result = mockMvc.perform(get("/top10Resources"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getMostRequestedResources()))
				.andReturn();
		String expectedEntry = "{a:[{\"requests\":\"X\"}]}";
		String content = "{a:" + result.getResponse().getContentAsString() + "}";

		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("\\d*");  // matches one or more digits
		Customization[] customizations = new Customization[10];
		for (int i=0; i < customizations.length; i++) {
			String contextPath = "a["+i+"].requests";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });

		JSONAssert.assertEquals(expectedEntry, content, regExCustomArrayValueComparator);
	}

	@Test
	void successPercentageGet_ContainsNumber() throws Exception {
		MvcResult result = mockMvc.perform(get("/successPercentage"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getSuccessfulRequestPercentage()))
				.andReturn();
		String expectedContent = "{\"successful request percentage\":X}";
		String content = result.getResponse().getContentAsString();

		JSONAssert.assertEquals(expectedContent, content,
				new CustomComparator(
						JSONCompareMode.LENIENT,
						new Customization("successful request percentage",
								new RegularExpressionValueMatcher<Object>("\\d+\\.{1}\\d+"))));
	}

	@Test
	void failPercentageGet_ContainsNumber() throws Exception {
		MvcResult result = mockMvc.perform(get("/failPercentage"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFailedRequestPercentage()))
				.andReturn();
		String expectedContent = "{\"failed request percentage\":X}";
		String content = result.getResponse().getContentAsString();

		JSONAssert.assertEquals(expectedContent, content,
				new CustomComparator(
						JSONCompareMode.LENIENT,
						new Customization("failed request percentage",
								new RegularExpressionValueMatcher<Object>("\\d+\\.{1}\\d+"))));
	}

	@Test
	void top10FailingResourcesGet_ReturnsJSONArrayWithStringResources() throws Exception {
		MvcResult result = mockMvc.perform(get("/top10FailingResources"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentlyFailingResources()))
				.andReturn();
		String expectedEntry = "{a:[{\"resource\":\"X\"}]}";
		String content = "{a:" + result.getResponse().getContentAsString() + "}";

		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("[^\\s]*");  // matches one or more digits
		Customization[] customizations = new Customization[10];
		for (int i=0; i < customizations.length; i++) {
			String contextPath = "a["+i+"].resource";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });

		JSONAssert.assertEquals(expectedEntry, content, regExCustomArrayValueComparator);
	}

	@Test
	void top10HostsGet_ReturnsJSONArrayWithStringResources() throws Exception {
		MvcResult result = mockMvc.perform(get("/top10Hosts"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentlyAppearingHosts()))
				.andReturn();
		String expectedEntry = "{a:[{\"host\":\"X\"}]}";
		String content = "{a:" + result.getResponse().getContentAsString() + "}";

		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("[^\\s]*");  // matches one or more digits
		Customization[] customizations = new Customization[10];
		for (int i=0; i < customizations.length; i++) {
			String contextPath = "a["+i+"].host";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });

		JSONAssert.assertEquals(expectedEntry, content, regExCustomArrayValueComparator);
	}

	@Test
	void top10HostsGet_ReturnsJSONArrayWithIntegerRequestCount() throws Exception {
		MvcResult result = mockMvc.perform(get("/top10Hosts"))
				.andExpect(handler().handlerType(LogController.class))
				.andExpect(handler().methodCall(on(LogController.class).getFrequentlyAppearingHosts()))
				.andReturn();
		String expectedEntry = "{a:[{\"requests\":\"X\"}]}";
		String content = "{a:" + result.getResponse().getContentAsString() + "}";

		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("\\d*");  // matches one or more digits
		Customization[] customizations = new Customization[10];
		for (int i=0; i < customizations.length; i++) {
			String contextPath = "a["+i+"].requests";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });

		JSONAssert.assertEquals(expectedEntry, content, regExCustomArrayValueComparator);
	}
}
