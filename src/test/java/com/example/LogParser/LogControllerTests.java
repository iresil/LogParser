package com.example.LogParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
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
}
