package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * MCP Tool call result following the MCP specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolCallResult {
    
    @JsonProperty("content")
    private List<Content> content;
    
    @JsonProperty("isError")
    private Boolean isError;
    
    public ToolCallResult() {
    }
    
    public ToolCallResult(List<Content> content) {
        this.content = content;
        this.isError = false;
    }
    
    public ToolCallResult(List<Content> content, boolean isError) {
        this.content = content;
        this.isError = isError;
    }
    
    public List<Content> getContent() {
        return content;
    }
    
    public void setContent(List<Content> content) {
        this.content = content;
    }
    
    public Boolean getIsError() {
        return isError;
    }
    
    public void setIsError(Boolean isError) {
        this.isError = isError;
    }
    
    /**
     * Content item in tool call result
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("text")
        private String text;
        
        public Content() {
        }
        
        public Content(String type, String text) {
            this.type = type;
            this.text = text;
        }
        
        public static Content text(String text) {
            return new Content("text", text);
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
}
