package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * MCP Response message following JSON-RPC 2.0 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpResponse extends McpMessage {
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private McpError error;
    
    public McpResponse() {
        super();
    }
    
    public McpResponse(String id, Object result) {
        super(id);
        this.result = result;
    }
    
    public McpResponse(String id, McpError error) {
        super(id);
        this.error = error;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public McpError getError() {
        return error;
    }
    
    public void setError(McpError error) {
        this.error = error;
    }
    
    public boolean isError() {
        return error != null;
    }
}
