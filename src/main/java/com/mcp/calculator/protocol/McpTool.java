package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * MCP Tool definition following the MCP specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpTool {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("inputSchema")
    private ToolInputSchema inputSchema;
    
    public McpTool() {
    }
    
    public McpTool(String name, String description, ToolInputSchema inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ToolInputSchema getInputSchema() {
        return inputSchema;
    }
    
    public void setInputSchema(ToolInputSchema inputSchema) {
        this.inputSchema = inputSchema;
    }
    
    /**
     * Tool input schema definition
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolInputSchema {
        
        @JsonProperty("type")
        private String type = "object";
        
        @JsonProperty("properties")
        private Map<String, ToolProperty> properties;
        
        @JsonProperty("required")
        private List<String> required;
        
        public ToolInputSchema() {
        }
        
        public ToolInputSchema(Map<String, ToolProperty> properties, List<String> required) {
            this.properties = properties;
            this.required = required;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Map<String, ToolProperty> getProperties() {
            return properties;
        }
        
        public void setProperties(Map<String, ToolProperty> properties) {
            this.properties = properties;
        }
        
        public List<String> getRequired() {
            return required;
        }
        
        public void setRequired(List<String> required) {
            this.required = required;
        }
    }
    
    /**
     * Tool property definition
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolProperty {
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("enum")
        private List<String> enumValues;
        
        public ToolProperty() {
        }
        
        public ToolProperty(String type, String description) {
            this.type = type;
            this.description = description;
        }
        
        public ToolProperty(String type, String description, List<String> enumValues) {
            this.type = type;
            this.description = description;
            this.enumValues = enumValues;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public List<String> getEnumValues() {
            return enumValues;
        }
        
        public void setEnumValues(List<String> enumValues) {
            this.enumValues = enumValues;
        }
    }
}
