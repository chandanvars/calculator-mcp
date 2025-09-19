package com.mcp.calculator.service;

import com.mcp.calculator.context.CalculatorContext;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculator service that performs mathematical operations with context support
 */
public class CalculatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);
    private final CalculatorContext context;
    
    // Pattern to match variable references in expressions
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern MEMORY_PATTERN = Pattern.compile("\\$M");
    private static final Pattern LAST_RESULT_PATTERN = Pattern.compile("\\$_");
    
    public CalculatorService(CalculatorContext context) {
        this.context = context;
    }
    
    /**
     * Evaluate a mathematical expression with variable and memory support
     */
    public double evaluate(String expression) throws CalculationException {
        try {
            logger.debug("Evaluating expression: {}", expression);
            
            // Preprocess expression to handle variables, memory, and last result
            String processedExpression = preprocessExpression(expression);
            logger.debug("Processed expression: {}", processedExpression);
            
            // Build and evaluate expression
            Expression exp = new ExpressionBuilder(processedExpression)
                .build();
            
            double result = exp.evaluate();
            
            // Store result in context
            context.addToHistory("EVALUATE", expression, result);
            
            logger.debug("Expression '{}' evaluated to: {}", expression, result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error evaluating expression '{}': {}", expression, e.getMessage());
            throw new CalculationException("Error evaluating expression: " + e.getMessage(), e);
        }
    }
    
    /**
     * Perform basic arithmetic operations
     */
    public double add(double a, double b) {
        double result = a + b;
        context.addToHistory("ADD", a + " + " + b, result);
        return result;
    }
    
    public double subtract(double a, double b) {
        double result = a - b;
        context.addToHistory("SUBTRACT", a + " - " + b, result);
        return result;
    }
    
    public double multiply(double a, double b) {
        double result = a * b;
        context.addToHistory("MULTIPLY", a + " * " + b, result);
        return result;
    }
    
    public double divide(double a, double b) throws CalculationException {
        if (b == 0) {
            throw new CalculationException("Division by zero");
        }
        double result = a / b;
        context.addToHistory("DIVIDE", a + " / " + b, result);
        return result;
    }
    
    public double power(double base, double exponent) {
        double result = Math.pow(base, exponent);
        context.addToHistory("POWER", base + " ^ " + exponent, result);
        return result;
    }
    
    public double sqrt(double value) throws CalculationException {
        if (value < 0) {
            throw new CalculationException("Square root of negative number");
        }
        double result = Math.sqrt(value);
        context.addToHistory("SQRT", "sqrt(" + value + ")", result);
        return result;
    }
    
    public double log(double value) throws CalculationException {
        if (value <= 0) {
            throw new CalculationException("Logarithm of non-positive number");
        }
        double result = Math.log(value);
        context.addToHistory("LOG", "ln(" + value + ")", result);
        return result;
    }
    
    public double log10(double value) throws CalculationException {
        if (value <= 0) {
            throw new CalculationException("Logarithm of non-positive number");
        }
        double result = Math.log10(value);
        context.addToHistory("LOG10", "log10(" + value + ")", result);
        return result;
    }
    
    public double sin(double radians) {
        double result = Math.sin(radians);
        context.addToHistory("SIN", "sin(" + radians + ")", result);
        return result;
    }
    
    public double cos(double radians) {
        double result = Math.cos(radians);
        context.addToHistory("COS", "cos(" + radians + ")", result);
        return result;
    }
    
    public double tan(double radians) {
        double result = Math.tan(radians);
        context.addToHistory("TAN", "tan(" + radians + ")", result);
        return result;
    }
    
    public double factorial(int n) throws CalculationException {
        if (n < 0) {
            throw new CalculationException("Factorial of negative number");
        }
        if (n > 170) {
            throw new CalculationException("Factorial too large (overflow)");
        }
        
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        
        context.addToHistory("FACTORIAL", n + "!", result);
        return result;
    }
    
    /**
     * Convert degrees to radians
     */
    public double degreesToRadians(double degrees) {
        double result = Math.toRadians(degrees);
        context.addToHistory("DEG_TO_RAD", "deg2rad(" + degrees + ")", result);
        return result;
    }
    
    /**
     * Convert radians to degrees
     */
    public double radiansToDegrees(double radians) {
        double result = Math.toDegrees(radians);
        context.addToHistory("RAD_TO_DEG", "rad2deg(" + radians + ")", result);
        return result;
    }
    
    /**
     * Preprocess expression to substitute variables, memory, and last result
     */
    private String preprocessExpression(String expression) throws CalculationException {
        String processed = expression;
        
        // Replace last result reference ($_)
        processed = LAST_RESULT_PATTERN.matcher(processed)
            .replaceAll(String.valueOf(context.getLastResult()));
        
        // Replace memory reference ($M)
        Double memoryValue = context.memoryRecall();
        if (memoryValue != null) {
            processed = MEMORY_PATTERN.matcher(processed)
                .replaceAll(String.valueOf(memoryValue));
        }
        
        // Replace variable references ($varname)
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(processed);
        while (variableMatcher.find()) {
            String varName = variableMatcher.group(1);
            Double varValue = context.getVariable(varName);
            if (varValue != null) {
                processed = processed.replace("$" + varName, String.valueOf(varValue));
            } else {
                throw new CalculationException("Undefined variable: " + varName);
            }
        }
        
        return processed;
    }
    
    /**
     * Check if expression contains any undefined variables
     */
    public boolean hasUndefinedVariables(String expression) {
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (context.getVariable(varName) == null) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all undefined variables in an expression
     */
    public java.util.List<String> getUndefinedVariables(String expression) {
        java.util.List<String> undefined = new java.util.ArrayList<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (context.getVariable(varName) == null && !undefined.contains(varName)) {
                undefined.add(varName);
            }
        }
        return undefined;
    }
    
    /**
     * Custom exception for calculation errors
     */
    public static class CalculationException extends Exception {
        public CalculationException(String message) {
            super(message);
        }
        
        public CalculationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
