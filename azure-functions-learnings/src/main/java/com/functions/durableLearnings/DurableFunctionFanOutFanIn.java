package com.functions.durableLearnings;

import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.Task;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

/**
 * 
 */
public class DurableFunctionFanOutFanIn {

	/*
	 * Fan out/fan in
	 * 
	 * Pattern, multiple functions can be run at the same time. This is the fan out
	 * part. Once all the functions have completed, the respective results are
	 * collected and returned to the orchestrator function, the fan in part.
	 */
	@FunctionName("FanOutFanIn")
	public Integer fanOutFanInOrchestrator(@DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
		// Get the list of work-items to process in parallel
		List<?> batch = ctx.callActivity("F1", List.class).await();
		// Schedule each task to run in parallel
		List<Task<Integer>> parallelTasks = batch.stream().map(item -> ctx.callActivity("F2", item, Integer.class))
				.collect(Collectors.toList());
		// Wait for all tasks to complete, then return the aggregated sum of the results
		List<Integer> results = ctx.allOf(parallelTasks).await();
		return results.stream().reduce(0, Integer::sum);
	}

}
