package io.github.greetapp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.helidon.webserver.CustomAPIGatewayV2HTTPEvent;
import io.helidon.webserver.CustomAPIGatewayV2HTTPResponse;
import io.helidon.webserver.HelidonLambdaHandlerAdapter;

public class LambdaMethodHandler
      implements RequestHandler<CustomAPIGatewayV2HTTPEvent, CustomAPIGatewayV2HTTPResponse> {

   private HelidonLambdaHandlerAdapter lambdaHelidonAdapter;

   public LambdaMethodHandler() {
      this(new Main());
   }

   public LambdaMethodHandler(Main main) {
      main.startServer((routing, webServer) -> {
         this.lambdaHelidonAdapter = new HelidonLambdaHandlerAdapter(routing, webServer);
      });
   }

   @Override
   public CustomAPIGatewayV2HTTPResponse handleRequest(CustomAPIGatewayV2HTTPEvent input, Context context) {
      try {
         return this.lambdaHelidonAdapter.handleEvent(input);
      } catch (Exception e) {
         return new CustomAPIGatewayV2HTTPResponse(
               500,
               "Unexpected exception: " + e.getClass().getName() + " " + e.getMessage());
      }
   }

}
