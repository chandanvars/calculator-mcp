package com.mcp.calculator.service;

import com.mcp.calculator.context.CalculatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CalculatorService
 */
class CalculatorServiceTest {
    
    private CalculatorContext context;
    private CalculatorService calculatorService;
    
    @BeforeEach
    void setUp() {
        context = new CalculatorContext();
        calculatorService = new CalculatorService(context);
    }
    
    @Test
    void testBasicArithmetic() throws CalculatorService.CalculationException {
        assertEquals(5.0, calculatorService.add(2, 3));
        assertEquals(1.0, calculatorService.subtract(3, 2));
        assertEquals(6.0, calculatorService.multiply(2, 3));
        assertEquals(2.0, calculatorService.divide(6, 3));
    }
    
    @Test
    void testDivisionByZero() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.divide(5, 0));
    }
    
    @Test
    void testPowerAndSqrt() throws CalculatorService.CalculationException {
        assertEquals(8.0, calculatorService.power(2, 3));
        assertEquals(3.0, calculatorService.sqrt(9));
    }
    
    @Test
    void testSqrtNegative() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.sqrt(-1));
    }
    
    @Test
    void testTrigonometricFunctions() {
        assertEquals(0.0, calculatorService.sin(0), 1e-10);
        assertEquals(1.0, calculatorService.cos(0), 1e-10);
        assertEquals(0.0, calculatorService.tan(0), 1e-10);
    }
    
    @Test
    void testFactorial() throws CalculatorService.CalculationException {
        assertEquals(1.0, calculatorService.factorial(0));
        assertEquals(1.0, calculatorService.factorial(1));
        assertEquals(24.0, calculatorService.factorial(4));
        assertEquals(120.0, calculatorService.factorial(5));
    }
    
    @Test
    void testFactorialNegative() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.factorial(-1));
    }
    
    @Test
    void testSimpleExpression() throws CalculatorService.CalculationException {
        double result = calculatorService.evaluate("2 + 3 * 4");
        assertEquals(14.0, result);
    }
    
    @Test
    void testExpressionWithParentheses() throws CalculatorService.CalculationException {
        double result = calculatorService.evaluate("(2 + 3) * 4");
        assertEquals(20.0, result);
    }
    
    @Test
    void testVariableInExpression() throws CalculatorService.CalculationException {
        context.setVariable("x", 5.0);
        double result = calculatorService.evaluate("2 * $x + 3");
        assertEquals(13.0, result);
    }
    
    @Test
    void testMemoryInExpression() throws CalculatorService.CalculationException {
        context.memoryStore(7.0);
        double result = calculatorService.evaluate("$M + 3");
        assertEquals(10.0, result);
    }
    
    @Test
    void testLastResultInExpression() throws CalculatorService.CalculationException {
        context.setLastResult(15.0);
        double result = calculatorService.evaluate("$_ / 3");
        assertEquals(5.0, result);
    }
    
    @Test
    void testUndefinedVariable() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.evaluate("$undefined + 1"));
    }
    
    @Test
    void testHasUndefinedVariables() {
        context.setVariable("x", 5.0);
        assertFalse(calculatorService.hasUndefinedVariables("$x + 1"));
        assertTrue(calculatorService.hasUndefinedVariables("$y + 1"));
    }
    
    @Test
    void testAverage() throws CalculatorService.CalculationException {
        // Test with multiple values
        assertEquals(3.0, calculatorService.average(1.0, 2.0, 3.0, 4.0, 5.0));
        
        // Test with two values
        assertEquals(2.5, calculatorService.average(2.0, 3.0));
        
        // Test with single value
        assertEquals(5.0, calculatorService.average(5.0));
        
        // Test with decimal values
        assertEquals(2.25, calculatorService.average(1.5, 2.0, 3.5, 2.0));
        
        // Test with negative values
        assertEquals(0.0, calculatorService.average(-2.0, 0.0, 2.0));
    }
    
    @Test
    void testAverageEmptyArray() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.average());
    }
    
    @Test
    void testAverageNullArray() {
        assertThrows(CalculatorService.CalculationException.class, 
            () -> calculatorService.average((double[]) null));
    }
}
