package io.helidon.webserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomAPIGatewayV2HTTPResponse {

    private int statusCode;
    private String body;

}
