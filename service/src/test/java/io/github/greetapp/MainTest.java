
package io.github.greetapp;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import io.helidon.webserver.CustomAPIGatewayV2HTTPEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class MainTest {

    private DDBService mockDDBService;
    private TokenUtils mockTokenUtils;
    private AwsHandlerWrapper spyAwsHandlerWrapper;
    private Main spyMain;
    private LambdaMethodHandler spyLambdaMethodHandler;

    @BeforeEach
    void startTheServer() {
        this.mockDDBService = mock(DDBService.class);
        this.mockTokenUtils = mock(TokenUtils.class);
        this.spyMain = spy(new Main());
        this.spyAwsHandlerWrapper = spy(new AwsHandlerWrapper(this.mockTokenUtils));

        doReturn(this.spyAwsHandlerWrapper)
                .when(this.spyMain).getAwsHandlerWrapper(any());

        doReturn(this.mockDDBService)
                .when(this.spyMain).getDDBService();

        doReturn(this.mockTokenUtils)
                .when(this.spyMain).getTokenUtils(any());

        doReturn(Optional.of(new UserInfo("greetapp-user", List.of())))
                .when(this.mockTokenUtils).getVerifiedUserInfo(any());

        this.spyLambdaMethodHandler = spy(new LambdaMethodHandler(this.spyMain));
    }

    @Test
    void testSimpleGreet() {
        Map<String, AttributeValue> item = Map.of(
                "type", AttributeValue.builder().s("greeting").build(),
                "text", AttributeValue.builder().s("Hello").build());

        when(this.mockDDBService.getDynamoDBItem(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(item));

        CustomAPIGatewayV2HTTPEvent input = CustomAPIGatewayV2HTTPEvent.builder()
                .requestContext(CustomAPIGatewayV2HTTPEvent.RequestContext.builder()
                        .http(CustomAPIGatewayV2HTTPEvent.RequestContext.Http.builder()
                                .method("GET")
                                .protocol("HTTP/1.1")
                                .build())
                        .domainName("example.com")
                        .build())
                .headers(Map.of("x-amz-access-token", ""))
                .rawPath("/greet")
                .build();

        Context context = Mockito.mock(Context.class);
        LambdaLogger lambdaLogger = Mockito.mock(LambdaLogger.class);
        Mockito.when(context.getLogger())
                .thenReturn(lambdaLogger);

        var res = this.spyLambdaMethodHandler.handleRequest(input, context);

        assertEquals(200, res.getStatusCode());
        assertEquals("{\"message\":\"Hello\"}", res.getBody());
    }

}
