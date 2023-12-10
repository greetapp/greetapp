package io.helidon.webserver;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.helidon.common.http.DataChunk;
import io.helidon.common.reactive.BufferedEmittingPublisher;

public class HelidonLambdaHandlerAdapter {

    private Routing routing;
    private WebServer webServer;

    public HelidonLambdaHandlerAdapter(Routing routing, WebServer webServer) {
        this.routing = routing;
        this.webServer = webServer;
    }

    public CustomAPIGatewayV2HTTPResponse handleEvent(CustomAPIGatewayV2HTTPEvent event)
            throws InterruptedException, ExecutionException, TimeoutException {
        HelidonLambdaRequest request = HelidonLambdaRequest.fromAPIGatewayV2HTTPEvent(event, this.webServer);
        HelidonLambdaResponse response = new HelidonLambdaResponse();

        this.routing.route(request, response);
        ((BufferedEmittingPublisher<DataChunk>) request.bodyPublisher()).complete();
        if (response.whenCompleted().get(3, TimeUnit.SECONDS) instanceof HelidonLambdaResponse completedRes) {
            return new CustomAPIGatewayV2HTTPResponse(response.getStatusCode(), response.getBody());
        }
        throw new IllegalArgumentException("Handler not found for route " + event.getRawPath());
    }

}
