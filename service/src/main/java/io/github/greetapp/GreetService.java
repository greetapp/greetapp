package io.github.greetapp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 * curl -X GET http://localhost:8080/greet
 * curl -X PUT -H "x-amz-access-token: eyJraW..." --data '{"message":"Hello"}' http://localhost:8080/greet
 */
public class GreetService implements Service {

    private static final Logger LOGGER = Logger.getLogger(GreetService.class.getName());
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private DDBService ddb;
    private AwsHandlerWrapper awsHandlerWrapper;

    GreetService(DDBService ddb, AwsHandlerWrapper awsHandlerWrapper) {
        this.ddb = ddb;
        this.awsHandlerWrapper = awsHandlerWrapper;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getDefaultMessageHandler);
        rules.put("/", Handler.create(JsonObject.class, this::setDefaultMessageHandler));
    }

    private void getDefaultMessageHandler(ServerRequest request, ServerResponse response) {
        awsHandlerWrapper.handle(request, response, List.of(), (res, err) -> {
            String greeting = this.getGreetingMessage();

            LOGGER.info("Greeting message is " + greeting);

            res.accept(JSON.createObjectBuilder()
                    .add("message", greeting)
                    .build());
        });
    }

    private void setDefaultMessageHandler(ServerRequest request, ServerResponse response, JsonObject json) {
        awsHandlerWrapper.handle(request, response, List.of("admin"), (res, err) -> {
            String newGreeting = json.getString("message");

            this.setGreetingMessage(newGreeting);
            res.accept(JSON.createObjectBuilder()
                    .add("status", "OK")
                    .build());
        });
    }

    private String getGreetingMessage() {
        String greetingAttName = "text";

        return this.ddb.getDynamoDBItem("Message", "type", "greeting").map(returnedItem -> {
            AttributeValue text = returnedItem.get(greetingAttName);
            if (text == null) {
                System.out.format("No attribute named '%s' on the item\n", greetingAttName);
            }

            return text.s();
        }).orElse("");
    }

    private void setGreetingMessage(String greeting) {
        String typeAttName = "type";
        String textAttName = "text";

        HashMap<String, AttributeValue> itemValue = new HashMap<>();
        itemValue.put(typeAttName, AttributeValue.builder()
                .s("greeting")
                .build());
        itemValue.put(textAttName, AttributeValue.builder()
                .s(greeting)
                .build());

        this.ddb.putDynamoDBItem("Message", itemValue);
        LOGGER.info("Greeting message was updated to " + greeting);
    }

}
