# NepalPay STOMP Integration

This project provides a Spring Boot implementation of a STOMP (Simple Text Oriented Messaging Protocol) client designed to integrate with the NepalPay WebSocket server for transaction status checking.

## Architecture & Components

The implementation is structured around the following core components:

### 1. [StompClientConfig](file:///c:/Users/LEGION/.gemini/antigravity/stomp/src/main/java/com/example/stomp/config/StompClientConfig.java)
- Configures the `WebSocketStompClient` bean.
- Sets up `MappingJackson2MessageConverter` for JSON-to-Object mapping and `ByteArrayMessageConverter` for handling raw byte payloads.

### 2. [NepalPayStompClient](file:///c:/Users/LEGION/.gemini/antigravity/stomp/src/main/java/com/example/stomp/client/NepalPayStompClient.java)
- The main service responsible for managing the WebSocket connection.
- **Endpoints**:
    - `WS_URL`: `wss://ws.nepalpay.com.np/nqrws`
    - `SUBSCRIBE_END_POINT`: `/user/nqrws/check-txn-status`
    - `DATA_SEND_END_POINT`: `/nqrws/check-txn-status`
- **Session Handler**: An inner class `NepalPayStompSessionHandler` manages the lifecycle of the connection, handles subscriptions upon successful connection, and processes incoming messages.

### 3. [CheckTxnRequest](file:///c:/Users/LEGION/.gemini/antigravity/stomp/src/main/java/com/example/stomp/model/CheckTxnRequest.java)
- A POJO representing the request payload sent to the NepalPay server.
- Fields include `api_token`, `merchant_id`, `request_id`, and `username`.

### 4. [RSAUtils](file:///c:/Users/LEGION/.gemini/antigravity/stomp/src/main/java/com/example/stomp/utils/RSAUtils.java)
- Utility class for RSA encryption, useful for securing sensitive data in transit if required by the protocol.

## Setup & Prerequisites

- **Java**: JDK 17 or higher.
- **Dependencies**:
    - `spring-boot-starter-websocket`
    - `lombok`
    - `jackson-databind`

## Usage Guide

### Integration Example

To use the client in your application, inject it and call the `connect` method:

```java
@Autowired
private NepalPayStompClient stompClient;

public void checkStatus() {
    CheckTxnRequest request = CheckTxnRequest.builder()
            .apiToken("YOUR_API_TOKEN")
            .merchantId("YOUR_MERCHANT_ID")
            .requestId("UNIQUE_REQUEST_ID")
            .username("YOUR_USERNAME")
            .build();

    stompClient.connect(request);
}
```

### Connection Flow

1.  **Connection**: `stompClient.connect(requestData)` initiates an asynchronous connection to the NepalPay WebSocket server.
2.  **Subscription**: Once connected, the `afterConnected` callback automatically subscribes to `/user/nqrws/check-txn-status`.
3.  **Sending Data**: Immediately after subscribing, the `CheckTxnRequest` is sent to `/nqrws/check-txn-status`.
4.  **Message Handling**: Responses from the server are caught in the `handleFrame` method and logged.

## Security

The client uses RSA for payload encryption if necessary. You can use `RSAUtils.encryptMessageWithPublicKey` to encrypt strings before including them in your request.

## Error Handling

The `NepalPayStompSessionHandler` includes basic error handling for:
- `handleException`: Logs STOMP-specific errors.
- `handleTransportError`: Logs underlying WebSocket transport issues.

## Resource Disposal

The `@PreDestroy` annotated `disconnect()` method ensures that the WebSocket session is gracefully closed when the application context is destroyed.
