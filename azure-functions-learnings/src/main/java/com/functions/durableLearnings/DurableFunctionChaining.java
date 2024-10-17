package com.functions.durableLearnings;

import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

/**
 * 
 */
public class DurableFunctionChaining {

	@FunctionName("Chaining")
	public double functionChaining(@DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
		String input = ctx.getInput(String.class);
		int x = ctx.callActivity("F1", input, int.class).await();
		int y = ctx.callActivity("F2", x, int.class).await();
		int z = ctx.callActivity("F3", y, int.class).await();
		return ctx.callActivity("F4", z, double.class).await();
	}

}
