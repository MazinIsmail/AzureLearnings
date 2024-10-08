package com.functions;


import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Azure Functions with Azure Service Bus Topic.
 * https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-service-bus-trigger?tabs=java
 */
public class ServiceBusTopicTriggerFunction {

    /*
    @FunctionName("ServiceBusTopicTrigger")
    public void serviceBusTopicTrigger(
        @ServiceBusTopicTrigger(name = "message", topicName = "SBTopicNameSingle", subscriptionName = "SBTopicNameSingleSubName", connection = "AzureWebJobsServiceBus") String message,
        @QueueOutput(name = "output", queueName = "test-servicebustopicbatch-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Topic trigger function processed a message: " + message);
        output.setValue(message);
    }

    @FunctionName("ServiceBusTopicTriggerMetadata")
    public void serviceBusTopicTriggerMetadata(
        @ServiceBusTopicTrigger(name = "message", topicName = "SBTopicNameMetadata", subscriptionName = "SBTopicNameMetadataSubName", connection = "AzureWebJobsServiceBus") String message,
        @BindingName("UserProperties") Map<String, Object> properties,
        @BindingName("CorrelationId") String correlationId,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Topic trigger function processed a message: " + message);
        context.getLogger().info("Custom message properties = " + properties);
        context.getLogger().info("CorrelationId = " + correlationId);
    }

    @FunctionName("ServiceBusTopicBatchTrigger")
    public void serviceBusTopicBatchTrigger(
        @ServiceBusTopicTrigger(name = "message", topicName = "SBTopicNameBatch", subscriptionName = "SBTopicNameBatchSubName", connection = "AzureWebJobsServiceBus", cardinality = Cardinality.MANY, dataType = "String") List<String> messages,
        @QueueOutput(name = "output", queueName = "test-servicebustopicbatch-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Topic trigger function processed a message: " + messages.get(0));
        output.setValue(messages.get(0));
    }

    *//**
     * This function will be invoked when a http request is received. The message contents are provided as output to this function.
     *//*
    @FunctionName("ServiceBusTopicOutput")
    public void serviceBusTopicOutput(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @ServiceBusTopicOutput(name = "message", topicName = "%SBTopicName%", subscriptionName = "%SBTopicSubName%", connection = "AzureWebJobsServiceBus") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        String message = request.getBody().orElse("default message");
        output.setValue(message);
        context.getLogger().info("Java Service Bus Topic output function got a message: " + message);
    }
    */
}
