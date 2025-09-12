package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/**
 * MCP Request message following JSON-RPC 2.0 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpRequest extends McpMessage {
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Map<String, Object> params;
    
    public McpRequest() {
        super();
    }
    
    public McpRequest(String id, String method, Map<String, Object> params) {
        super(id);
        this.method = method;
        this.params = params;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
