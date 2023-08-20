package io.github.greetapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DDBService {

    private static final Logger LOGGER = Logger.getLogger(DDBService.class.getName());
    private static final Region REGION = Region.US_EAST_1;

    private DynamoDbClient ddb;

    public DDBService() {
        this.ddb = DynamoDbClient.builder()
                .region(REGION)
                .build();
    }

    public Optional<Map<String, AttributeValue>> getDynamoDBItem(String tableName, String key, String keyVal) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = this.ddb.getItem(request).item();
            if (returnedItem == null) {
                return Optional.empty();
            }

            return Optional.of(returnedItem);

        } catch (DynamoDbException e) {
            LOGGER.warning(e.getMessage());
            throw new AppException("Could not get item in table \"%s\"".formatted(tableName));
        }
    }

    public void putDynamoDBItem(String tableName, Map<String, AttributeValue> itemValue) {
        PutItemRequest request = PutItemRequest.builder()
                .item(itemValue)
                .tableName(tableName)
                .build();

        try {
            this.ddb.putItem(request);
        } catch (DynamoDbException e) {
            LOGGER.warning(e.getMessage());
            throw new AppException("Could not update item in table \"%s\"".formatted(tableName));
        }
    }

}
