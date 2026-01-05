package com.example.stomp;

import com.example.stomp.client.NepalPayStompClient;
import com.example.stomp.model.CheckTxnRequest;
import com.example.stomp.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class StompApplication {

    public static void main(String[] args) {
        SpringApplication.run(StompApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(NepalPayStompClient nepalPayStompClient) {
        return args -> {
            String plaintextData = "bnDFFRDLwHhQ8ZPvkcrYcUXZuJChYG";
            String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqtCttieqE5piPTSDLidMiXnxHO1b+Zp+y3sSM49vvw2EOH0vrXv/LEtB1nXCb92VC67QJvcCDVr89J2TiYkw8QDCFeZHpUv9PPSGs7TfRgofeB4zMhGcSPd0eE/gHen26vFiEuZs9SFWR1cXAwN0uDZjs7NoxJdlRxs8YB/2NOTi3gEEov7jSRKoLy/7X1n9Cpb5AwWraccFWWGtkM9nplUrb32Fdu6641YNP+GiB6PZpJF5CHSvBkUbGDfK+HkSwnCqEl0Jb2b4HXSTiVhH52jKLxARUnR1umON8ZY+G5nCjXT13Ip2mfO8hX+Vkc7afeidnffJXVboiGlti6ujywIDAQAB";

            try {
                String apiToken = RSAUtils.encryptMessageWithPublicKey(plaintextData, publicKey);
                log.info("Generated API Token: {}", apiToken);

                CheckTxnRequest request = CheckTxnRequest.builder()
                        .apiToken(apiToken)
                        .merchantId("Terminal1")
                        .requestId("2601010055142218IRM")
                        .username("imarkkumarinqr")
                        .build();

                nepalPayStompClient.connect(request);
            } catch (Exception e) {
                log.error("Failed to generate API token or connect", e);
            }
        };
    }
}
