
package io.github.greetapp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.json.JsonObject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import io.helidon.webserver.WebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class MainTest {

    private WebServer webServer;
    private WebClient webClient;

    private DDBService mockDDBService;
    private TokenUtils mockTokenUtils;
    private AwsHandlerWrapper spyAwsHandlerWrapper;
    private Main spyMain;

    @BeforeEach
    void startTheServer() {
        this.mockDDBService = mock(DDBService.class);
        this.mockTokenUtils = mock(TokenUtils.class);
        this.spyAwsHandlerWrapper = spy(new AwsHandlerWrapper(this.mockTokenUtils));
        this.spyMain = spy(new Main());

        doReturn(this.spyAwsHandlerWrapper)
                .when(this.spyMain).getAwsHandlerWrapper(any());

        doReturn(this.mockDDBService)
                .when(this.spyMain).getDDBService();

        doReturn(this.mockTokenUtils)
                .when(this.spyMain).getTokenUtils(any());

        doReturn("http://localhost:8080")
                .when(this.spyMain).getCorsOrigin1(any());

        doReturn(Optional.of(new UserInfo("greetapp-user", List.of())))
                .when(this.mockTokenUtils).getVerifiedUserInfo(any());

        this.webServer = this.spyMain.startServer().await();
        this.webClient = WebClient.builder()
                .baseUri("http://localhost:" + this.webServer.port())
                .addMediaSupport(JsonpSupport.create())
                .build();
    }

    @AfterEach
    void stopServer() throws ExecutionException, InterruptedException, TimeoutException {
        if (this.webServer != null) {
            this.webServer.shutdown()
                    .toCompletableFuture()
                    .get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    void testMetrics() {
        WebClientResponse response = this.webClient.get()
                .path("/metrics")
                .request()
                .await();
        assertEquals(200, response.status().code());
    }

    @Test
    void testHealth() {
        WebClientResponse response = this.webClient.get()
                .path("health")
                .request()
                .await();
        assertEquals(200, response.status().code());
    }

    @Test
    void testSimpleGreet() {
        Map<String, AttributeValue> item = Map.of(
                "type", AttributeValue.builder().s("greeting").build(),
                "text", AttributeValue.builder().s("Hello").build());

        when(this.mockDDBService.getDynamoDBItem(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(item));

        JsonObject jsonObject = this.webClient.get()
                .path("/greet")
                .headers(headers -> headers.add("x-amz-access-token", ""))
                .request(JsonObject.class)
                .await();
        assertEquals("Hello", jsonObject.getString("message"));
    }

}
