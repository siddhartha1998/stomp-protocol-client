package com.example.stomp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckTxnRequest {
    @JsonProperty("api_token")
    private String apiToken;
    
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("request_id")
    private String requestId;
    
    private String username;
}
