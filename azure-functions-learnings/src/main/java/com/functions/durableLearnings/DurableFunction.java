package com.functions.durableLearnings;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

/**
 * Async HTTP APIs
 * 
 * The async HTTP API pattern addresses the problem of coordinating the state of
 * long-running operations with external clients. A common way to implement this
 * pattern is by having an HTTP endpoint trigger the long-running action. Then,
 * redirect the client to a status endpoint that the client polls to learn when
 * the operation is finished.
 * 
 * Durable Functions provides built-in support for this pattern, simplifying or
 * even removing the code you need to write to interact with long-running
 * function executions.
 * 
 * Because the Durable Functions runtime manages state for you, you don't need
 * to implement your own status-tracking mechanism.
 * 
 * The Durable Functions extension exposes built-in HTTP APIs that manage
 * long-running orchestrations. You can alternatively implement this pattern
 * yourself by using your own function triggers (such as HTTP, a queue, or Azure
 * Event Hubs) and the durable client binding.
 * 
 * Example Implementation: -
 * 
 * https://learn.microsoft.com/en-us/azure/azure-functions/durable/quickstart-java?tabs=bash&pivots=create-option-manual-setup
 */
public class DurableFunction {

	@FunctionName("StartOrchestration")
	public HttpResponseMessage startOrchestration(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			@DurableClientInput(name = "durableContext") DurableClientContext durableContext,
			final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");

		DurableTaskClient client = durableContext.getClient();
		String instanceId = client.scheduleNewOrchestrationInstance("Cities");
		context.getLogger().info("Created new Java orchestration with instance ID = " + instanceId);
		return durableContext.createCheckStatusResponse(request, instanceId);
	}

	/**
	 * This is the orchestrator function, which can schedule activity functions,
	 * create durable timers, or wait for external events in a way that's completely
	 * fault-tolerant.
	 */
	@FunctionName("Cities")
	public String citiesOrchestrator(@DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
		String result = "";
		result += ctx.callActivity("Capitalize", "Tokyo", String.class).await() + ", ";
		result += ctx.callActivity("Capitalize", "London", String.class).await() + ", ";
		result += ctx.callActivity("Capitalize", "Seattle", String.class).await() + ", ";
		result += ctx.callActivity("Capitalize", "Austin", String.class).await();
		return result;
	}

	/**
	 * This is the activity function that gets invoked by the orchestration.
	 */
	@FunctionName("Capitalize")
	public String capitalize(@DurableActivityTrigger(name = "name") String name, final ExecutionContext context) {
		context.getLogger().info("Capitalizing: " + name);
		return name.toUpperCase();
	}

}
