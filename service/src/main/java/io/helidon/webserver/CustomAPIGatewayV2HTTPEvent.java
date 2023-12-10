package io.helidon.webserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomAPIGatewayV2HTTPEvent {

    private String rawPath;
    private Map<String, String> headers;
    private String body;
    private RequestContext requestContext;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class RequestContext {

        private String domainName;
        private Http http;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        @Builder
        public static class Http {

            private String method;
            private String protocol;

        }

    }

}
