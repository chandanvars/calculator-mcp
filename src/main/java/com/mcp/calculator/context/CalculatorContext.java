package com.mcp.calculator.context;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages stateful context for calculator operations including variables, 
 * memory, and operation history
 */
public class CalculatorContext {
    
    private final Map<String, Double> variables;
    private final List<OperationHistory> history;
    private final Stack<Double> memoryStack;
    private final AtomicLong operationCounter;
    private double lastResult;
    private String sessionId;
    
    public CalculatorContext() {
        this.variables = new ConcurrentHashMap<>();
        this.history = Collections.synchronizedList(new ArrayList<>());
        this.memoryStack = new Stack<>();
        this.operationCounter = new AtomicLong(0);
        this.lastResult = 0.0;
        this.sessionId = UUID.randomUUID().toString();
    }
    
    /**
     * Set a variable value
     */
    public void setVariable(String name, double value) {
        variables.put(name, value);
        addToHistory("VARIABLE_SET", name + " = " + value, value);
    }
    
    /**
     * Get a variable value
     */
    public Double getVariable(String name) {
        return variables.get(name);
    }
    
    /**
     * Get all variables
     */
    public Map<String, Double> getAllVariables() {
        return new HashMap<>(variables);
    }
    
    /**
     * Clear a specific variable
     */
    public boolean clearVariable(String name) {
        boolean existed = variables.containsKey(name);
        variables.remove(name);
        if (existed) {
            addToHistory("VARIABLE_CLEAR", "Cleared variable: " + name, 0.0);
        }
        return existed;
    }
    
    /**
     * Clear all variables
     */
    public void clearAllVariables() {
        int count = variables.size();
        variables.clear();
        addToHistory("VARIABLES_CLEAR", "Cleared " + count + " variables", 0.0);
    }
    
    /**
     * Push value to memory stack
     */
    public void memoryStore(double value) {
        memoryStack.push(value);
        addToHistory("MEMORY_STORE", "Stored " + value + " to memory", value);
    }
    
    /**
     * Recall last stored memory value
     */
    public Double memoryRecall() {
        if (memoryStack.isEmpty()) {
            return null;
        }
        Double value = memoryStack.peek();
        addToHistory("MEMORY_RECALL", "Recalled " + value + " from memory", value);
        return value;
    }
    
    /**
     * Pop value from memory stack
     */
    public Double memoryPop() {
        if (memoryStack.isEmpty()) {
            return null;
        }
        Double value = memoryStack.pop();
        addToHistory("MEMORY_POP", "Popped " + value + " from memory", value);
        return value;
    }
    
    /**
     * Clear memory stack
     */
    public void memoryClear() {
        int count = memoryStack.size();
        memoryStack.clear();
        addToHistory("MEMORY_CLEAR", "Cleared " + count + " memory values", 0.0);
    }
    
    /**
     * Get memory stack size
     */
    public int getMemorySize() {
        return memoryStack.size();
    }
    
    /**
     * Add memory value to current value
     */
    public Double memoryAdd(double value) {
        if (memoryStack.isEmpty()) {
            memoryStack.push(value);
        } else {
            double current = memoryStack.pop();
            double result = current + value;
            memoryStack.push(result);
            addToHistory("MEMORY_ADD", "Added " + value + " to memory: " + result, result);
            return result;
        }
        return value;
    }
    
    /**
     * Subtract value from memory
     */
    public Double memorySubtract(double value) {
        if (memoryStack.isEmpty()) {
            memoryStack.push(-value);
        } else {
            double current = memoryStack.pop();
            double result = current - value;
            memoryStack.push(result);
            addToHistory("MEMORY_SUBTRACT", "Subtracted " + value + " from memory: " + result, result);
            return result;
        }
        return -value;
    }
    
    /**
     * Set the last calculation result
     */
    public void setLastResult(double result) {
        this.lastResult = result;
    }
    
    /**
     * Get the last calculation result
     */
    public double getLastResult() {
        return lastResult;
    }
    
    /**
     * Add operation to history
     */
    public void addToHistory(String operation, String expression, double result) {
        OperationHistory historyEntry = new OperationHistory(
            operationCounter.incrementAndGet(),
            LocalDateTime.now(),
            operation,
            expression,
            result
        );
        history.add(historyEntry);
        setLastResult(result);
    }
    
    /**
     * Get operation history
     */
    public List<OperationHistory> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Get recent history (last n operations)
     */
    public List<OperationHistory> getRecentHistory(int count) {
        int size = history.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(history.subList(fromIndex, size));
    }
    
    /**
     * Clear operation history
     */
    public void clearHistory() {
        int count = history.size();
        history.clear();
        operationCounter.set(0);
        addToHistory("HISTORY_CLEAR", "Cleared " + count + " history entries", 0.0);
    }
    
    /**
     * Get session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Reset entire context
     */
    public void reset() {
        variables.clear();
        history.clear();
        memoryStack.clear();
        operationCounter.set(0);
        lastResult = 0.0;
        sessionId = UUID.randomUUID().toString();
        addToHistory("CONTEXT_RESET", "Calculator context reset", 0.0);
    }
    
    /**
     * Get context summary
     */
    public ContextSummary getSummary() {
        return new ContextSummary(
            sessionId,
            variables.size(),
            memoryStack.size(),
            history.size(),
            lastResult,
            operationCounter.get()
        );
    }
    
    /**
     * Operation history entry
     */
    public static class OperationHistory {
        private final long id;
        private final LocalDateTime timestamp;
        private final String operation;
        private final String expression;
        private final double result;
        
        public OperationHistory(long id, LocalDateTime timestamp, String operation, 
                              String expression, double result) {
            this.id = id;
            this.timestamp = timestamp;
            this.operation = operation;
            this.expression = expression;
            this.result = result;
        }
        
        public long getId() { return id; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getOperation() { return operation; }
        public String getExpression() { return expression; }
        public double getResult() { return result; }
        
        @Override
        public String toString() {
            return String.format("[%d] %s: %s = %s", 
                id, operation, expression, result);
        }
    }
    
    /**
     * Context summary
     */
    public static class ContextSummary {
        private final String sessionId;
        private final int variableCount;
        private final int memorySize;
        private final int historySize;
        private final double lastResult;
        private final long operationCount;
        
        public ContextSummary(String sessionId, int variableCount, int memorySize, 
                            int historySize, double lastResult, long operationCount) {
            this.sessionId = sessionId;
            this.variableCount = variableCount;
            this.memorySize = memorySize;
            this.historySize = historySize;
            this.lastResult = lastResult;
            this.operationCount = operationCount;
        }
        
        public String getSessionId() { return sessionId; }
        public int getVariableCount() { return variableCount; }
        public int getMemorySize() { return memorySize; }
        public int getHistorySize() { return historySize; }
        public double getLastResult() { return lastResult; }
        public long getOperationCount() { return operationCount; }
    }
}
