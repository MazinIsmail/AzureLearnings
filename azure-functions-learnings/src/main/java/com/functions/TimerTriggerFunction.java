package com.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import java.time.LocalDateTime;

/**
 * Azure Functions with Timer trigger.
 * https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-timer?tabs=java
 */
public class TimerTriggerFunction {
    /**
     * This function will be invoked periodically according to the specified schedule.
     * The below function is executed each time the minutes have a value divisible by five
     */
    @FunctionName("TimerTrigger")
    public void timerHandler(
        @TimerTrigger(name = "timerInfo", schedule = "0 */1 * * * *") String timerInfo,
        final ExecutionContext context) {
        context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now());
    }
}
