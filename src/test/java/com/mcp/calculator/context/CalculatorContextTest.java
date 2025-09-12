package com.mcp.calculator.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CalculatorContext
 */
class CalculatorContextTest {
    
    private CalculatorContext context;
    
    @BeforeEach
    void setUp() {
        context = new CalculatorContext();
    }
    
    @Test
    void testVariableOperations() {
        // Test setting and getting variables
        context.setVariable("x", 10.0);
        assertEquals(10.0, context.getVariable("x"));
        
        // Test non-existent variable
        assertNull(context.getVariable("nonexistent"));
        
        // Test clearing variable
        assertTrue(context.clearVariable("x"));
        assertNull(context.getVariable("x"));
        assertFalse(context.clearVariable("nonexistent"));
    }
    
    @Test
    void testMemoryOperations() {
        // Initially empty
        assertEquals(0, context.getMemorySize());
        assertNull(context.memoryRecall());
        
        // Store values
        context.memoryStore(5.0);
        context.memoryStore(10.0);
        assertEquals(2, context.getMemorySize());
        
        // Recall (peek)
        assertEquals(10.0, context.memoryRecall());
        assertEquals(2, context.getMemorySize()); // Should not change size
        
        // Pop
        assertEquals(10.0, context.memoryPop());
        assertEquals(1, context.getMemorySize());
        
        // Clear memory
        context.memoryClear();
        assertEquals(0, context.getMemorySize());
    }
    
    @Test
    void testMemoryArithmetic() {
        context.memoryStore(10.0);
        
        // Add to memory
        assertEquals(15.0, context.memoryAdd(5.0));
        assertEquals(15.0, context.memoryRecall());
        
        // Subtract from memory
        assertEquals(12.0, context.memorySubtract(3.0));
        assertEquals(12.0, context.memoryRecall());
    }
    
    @Test
    void testLastResult() {
        assertEquals(0.0, context.getLastResult());
        
        context.setLastResult(42.0);
        assertEquals(42.0, context.getLastResult());
    }
    
    @Test
    void testHistory() {
        // Initially empty
        assertTrue(context.getHistory().isEmpty());
        
        // Add operations
        context.addToHistory("ADD", "2 + 3", 5.0);
        context.addToHistory("MULTIPLY", "5 * 2", 10.0);
        
        // Check history
        assertEquals(2, context.getHistory().size());
        assertEquals(10.0, context.getLastResult()); // Should be updated
        
        // Test recent history
        var recent = context.getRecentHistory(1);
        assertEquals(1, recent.size());
        assertEquals("MULTIPLY", recent.get(0).getOperation());
        
        // Clear history
        context.clearHistory();
        assertEquals(1, context.getHistory().size()); // Clear operation is added to history
    }
    
    @Test
    void testContextSummary() {
        context.setVariable("x", 5.0);
        context.setVariable("y", 10.0);
        context.memoryStore(15.0);
        context.addToHistory("TEST", "1 + 1", 2.0);
        
        var summary = context.getSummary();
        assertEquals(2, summary.getVariableCount());
        assertEquals(1, summary.getMemorySize());
        assertEquals(1, summary.getHistorySize());
        assertEquals(2.0, summary.getLastResult());
        assertNotNull(summary.getSessionId());
    }
    
    @Test
    void testReset() {
        // Set up some state
        context.setVariable("x", 5.0);
        context.memoryStore(10.0);
        context.addToHistory("TEST", "1 + 1", 2.0);
        String originalSessionId = context.getSessionId();
        
        // Reset
        context.reset();
        
        // Verify everything is cleared
        assertTrue(context.getAllVariables().isEmpty());
        assertEquals(0, context.getMemorySize());
        assertEquals(0.0, context.getLastResult());
        assertNotEquals(originalSessionId, context.getSessionId());
        
        // History should have one entry (the reset operation)
        assertEquals(1, context.getHistory().size());
        assertEquals("CONTEXT_RESET", context.getHistory().get(0).getOperation());
    }
    
    @Test
    void testSessionId() {
        String sessionId1 = context.getSessionId();
        assertNotNull(sessionId1);
        
        CalculatorContext context2 = new CalculatorContext();
        String sessionId2 = context2.getSessionId();
        
        assertNotEquals(sessionId1, sessionId2);
    }
}
