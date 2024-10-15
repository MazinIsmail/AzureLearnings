package com.functions;

/**
 * Triggers and Bindings
 * 
 * A function can only have one trigger. For this reason, if you want to execute
 * the same logic through different triggers, you'll have to create several
 * functions that share the same code.
 * 
 * You can have one or more input or/and output bindings.
 * 
 * Your functions need to be short-lived or quick-running tasks that can
 * complete within timeout limits. If they take longer, they run the risk of
 * failing due to the function timing out or introducing weird edge cases if
 * they partially complete before the timeout kicks in. You may also incur
 * unintended additional costs if a function runs longer than expected.
 * 
 * 
 */
public class FunctionsLearnings {

}
