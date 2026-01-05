package com.example.stomp.client;

import com.example.stomp.model.CheckTxnRequest;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class NepalPayStompClient {

    private static final String WS_URL = "ws://192.168.1.74:8080/nqrws"; //"wss://ws.nepalpay.com.np/nqrws";
    private static final String SUBSCRIBE_END_POINT = "/user/nqrws/check-txn-status";
    private static final String DATA_SEND_END_POINT = "/nqrws/check-txn-status";

    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public NepalPayStompClient(WebSocketStompClient stompClient) {
        this.stompClient = stompClient;
    }

    public void connect(CheckTxnRequest requestData) {
        try {
            log.info("Connecting to STOMP server at {}", WS_URL);
            stompSession = stompClient.connectAsync(WS_URL, new NepalPayStompSessionHandler(requestData)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while connecting to STOMP server", e);
            Thread.currentThread().interrupt();
        }
    }

    private class NepalPayStompSessionHandler extends StompSessionHandlerAdapter {
        private final CheckTxnRequest requestData;

        public NepalPayStompSessionHandler(CheckTxnRequest requestData) {
            this.requestData = requestData;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("Connected to STOMP server. Session ID: {}", session.getSessionId());

            // Subscribe to endpoint
            session.subscribe(SUBSCRIBE_END_POINT, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return byte[].class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (payload instanceof byte[]) {
                        log.info("Received Message: {}", new String((byte[]) payload));
                    } else {
                        log.info("Received Message: {}", payload);
                    }
                }
            });
            log.info("Subscribed to {}", SUBSCRIBE_END_POINT);

            // Send data
            log.info("Sending request data: {}", requestData);
            session.send(DATA_SEND_END_POINT, requestData);
        }

        @Override
        public void handleException(StompSession session, org.springframework.messaging.simp.stomp.StompCommand command,
                StompHeaders headers, byte[] payload, Throwable exception) {
            log.error("STOMP Exception: {}", exception.getMessage(), exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            log.error("Transport Error: {}", exception.getMessage(), exception);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
            log.info("Disconnected from STOMP server");
        }
    }
}
