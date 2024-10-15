package com.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.OrchestrationRunner;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

import java.util.Optional;

/**
 * Azure Durable Functions with HTTP trigger.
 * 
 * If you'd like to write longer running or stateful functions using the same
 * programming model, then Durable Functions is what you're looking for. An
 * extension of Azure functions, Durable Functions give you the ability to
 * implement stateful workflows and stateful entities. Durable Functions the
 * ideal solution for complex workflows, where sequential execution order is
 * important, or keeping track of state is necessary; all while keeping the same
 * consumption-based billing model. Keeping track of state, like all the inputs
 * and outputs that affect your function executions, can get pretty complicated.
 * But, Durable Functions handles that for you. The library takes care of
 * loading and saving state, so you don't have to add additional code to
 * explicitly handle that. State is persisted to Azure storage, which includes
 * the triggers, actions, inputs, and outputs of every interaction. This is
 * quite useful, as process interruptions are less likely to result in data loss
 * and retry capabilities are possible from the last known "good" save point.
 * 
 * Every durable function will have at least one client function, one
 * orchestrator function and either one or more activity or entity functions.
 * These building blocks let you build workflows, also called orchestrations,
 * that can have multiple steps and multiple functions.
 * 
 * Client:
 * 
 * The first building block is the client function. You can think of the client
 * function as the kickstarter of an orchestration, as it is responsible for
 * starting a durable function workflow. Orchestrator functions can only be
 * triggered by a client function. Client functions will typically bind to two
 * things, a trigger, that will be used to invoke the client function itself,
 * and more importantly, the Durable Client Attribute, a unique binding that
 * gives you access to all Orchestration Client APIs, supported by durable
 * functions.
 * 
 * Orchestrator:
 * 
 * The next building block is the orchestrator function. Very much like a
 * conductor for an orchestra, the orchestrator function outlines the steps of
 * the orchestration, keeps track of the orchestration's state after every step
 * and manages the orchestration from start to finish. It's the most important
 * part of a durable function as it's responsible for the parts that enable
 * sequential and stateful workflows. With our durable function started and
 * orchestration steps outlined, the next action is to actually perform some
 * work, and that's done through activity functions. An orchestrator function is
 * the brain of a durable function. It describes in code that you write how
 * actions are done and in what order they will be completed. These actions and
 * the execution flow specified for them are called orchestrations. Fittingly,
 * orchestrator functions create and maintain orchestrations. Orchestrator
 * functions have access to a special context object called the
 * DurableOrchestrationContext. It's through this context that orchestrator
 * functions get all their powers. They can manage the state of workflows,
 * control the execution of other functions, and generate and manage
 * checkpoints.
 * 
 * As a built-in part of the durable functions framework, whenever you start a
 * durable function, it automatically creates a DurableFunctionsHubHistory and
 * DurableFunctionsHubInstances Azure storage table for you. These storage
 * providers store each request and response that happens between the
 * orchestrator and client activity and entity functions. It's through this
 * append-only history that orchestrators also generate checkpoints throughout
 * the orchestration. This makes durable functions extremely reliable and
 * durable as data loss is unlikely to occur. If something were to go wrong in
 * the middle of the orchestration, an orchestrator would just go back to the
 * DurableFunctionsHubHistory, see the last successful task that was completed,
 * and retry the remaining steps again. This last part is very important. The
 * orchestrator function's built-in retry behavior is a very powerful part of
 * durable functions. But, as we've been taught, with great power comes great
 * responsibility. And that responsibility comes in the form of constraints. If
 * there's one other thing to understand, it's that orchestrator functions must
 * be written in a deterministic way. What does that mean? It means that given
 * the same inputs, no matter how many times the orchestrator function runs, it
 * should always produce the same outputs. It's quite similar to the constraints
 * that dictate the loss of multiplication. When you multiply nine by seven, you
 * will always get the product of 63. Creating a deterministic orchestrator can
 * be condensed into a pretty standard rule: Don't use nondeterministic
 * components in your orchestrator function. Nondeterministic components include
 * static or environment variables, synchronous APIs, the generation of random
 * numbers, GUIDs, or UUIDs, outbound network calls, or nondeterministic APIs,
 * like calling DateTime.Now or using a stopwatch. If you go through each of
 * these components, you can see how they may change the execution or produce a
 * different result for the orchestrator, rendering it nondeterministic. One
 * benefit of durable function orchestrators is that they're capable of
 * error-handling. So if you wanted to add a try-catch block to this
 * orchestrator function, you could, and deal with any exceptions as you see fit
 * and in a way that's familiar to you as a developer. An orchestrator function
 * keeps things running, keeps track of everything that happens, and can run for
 * a long time, even forever.
 * 
 * Activity:
 * 
 * Activity functions are just like regular Azure functions. You write code to
 * perform some tasks and you ideally create them to do one thing. An activity
 * function is considered a unit of work so focusing on creating small tasks
 * helps you take advantage of them within a durable function. The biggest
 * difference between an activity function and a regular Azure function is that
 * you need a special trigger, an activity trigger, to invoke them rather than
 * the range of triggers used for Azure functions. Additionally, only the
 * orchestrator function can call this activity trigger. As activity functions
 * complete, they inform the orchestrator. This continues until the
 * orchestration is complete or an error occurs.
 * 
 * The basic unit of work in a durable function, an activity function is an
 * action or task that is managed by the orchestrator function. Activity
 * functions can be executed sequentially, in parallel, or some combination of
 * both.
 * 
 * No matter how an activity function is executed, it will always communicate
 * back its status to the orchestrator function. Speaking of communication,
 * orchestrator functions and activity functions have a special relationship
 * with each other. Only an orchestrator function can trigger an activity
 * function using a special activity trigger, and activity functions can return
 * data back to the orchestrator function, since the activity trigger supports
 * the durable activity context as an input.
 * 
 * Orchestrator functions need to be written in a deterministic way. This means
 * a lot of work is encouraged to be placed within activity functions instead of
 * the orchestrator function to keep it deterministic. So activity functions
 * frequently execute network calls, CPU-intensive operations, and other tasks
 * that are prohibited in the orchestrator. It should be noted that the durable
 * task framework does guarantee an at least once execution of all activity
 * functions. This means even though there are no strict constraints on how
 * activity functions are written, it is a good idea to make sure your activity
 * function logic is as itempotent as possible. What does itempotent mean? In
 * this case, no matter how many times you execute your activity function, no
 * side effects occur. Whether you run your activity function five times or 50
 * times, the resulting effect is as if you had only run it once.
 * 
 * Entity:
 * 
 * Finally, an alternative building block, the entity function, can be used in
 * two ways. It can be invoked by a client function or by an orchestrator
 * function. Once called, they define operations to read and update small pieces
 * of state for durable entities.
 * 
 * A durable entity is a representation of state. State can be anything you'd
 * like to keep track of, like a player in a game, an IoT device, or an event at
 * a venue. Each of these entities can have specific information about them,
 * like XP, skill tree selections, or progress for a player, hourly stats, or
 * daily readings from an IoT device, or attendee counts and food sales for an
 * event. These are just a few examples. There's probably lots more information
 * you could capture about these entities. Now, entity functions are connected
 * to durable entities. When an entity function is called, it receives a message
 * that either updates the state, reads the state, or does some other action
 * against a durable entity. These state changes can be tied to operations
 * specific actions that can be taken to change the state of the entity. So if a
 * player increases their XP or an IoT device receives its next reading, there
 * may be a corresponding increase XP operation for the player entity, and an ad
 * reading operation for the IoT device entity that is executed to update those
 * respective entities. Entities communicate via messages sent through reliable
 * queues. In order for the right operations to be invoked on the right
 * entities. These messages must have a few pieces of information. First, the
 * entity ID. This is a combination of the entity name and the entity key. The
 * entity name identifies the type of entity, and the entity key is a unique
 * string that distinguishes a particular entity from all other entities of the
 * same name. For example, a player in a game might have an entity ID of player
 * 3304. Next, the operation name is required so that we know which operation to
 * perform. In our player example, removing a point from a particular skill tree
 * could have an operation called remove skill point. With these two pieces of
 * information, the message is valid and goes into the queue where once
 * processed, the state can be changed. In our case, a skill point will be
 * removed from the player's skill tree. Entity functions also have a few unique
 * features. They can be invoked from either a client or orchestrator function,
 * they have a special trigger type called the entity trigger, and they're the
 * only functions that manage state explicitly.
 *
 */
public class DurableFunction {
	/**
	 * This HTTP-triggered function starts the orchestration.
	 */
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
