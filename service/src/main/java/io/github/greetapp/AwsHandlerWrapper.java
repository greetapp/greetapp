package io.github.greetapp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.helidon.webserver.RequestHeaders;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;

public class AwsHandlerWrapper {

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private TokenUtils tokenUtils;

    public AwsHandlerWrapper(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    public void handle(ServerRequest request, ServerResponse response, List<String> rolesAllowed,
            BiConsumer<Consumer<JsonObject>, Consumer<Throwable>> handler) {
        try {
            if (!rolesAllowed.isEmpty()) {
                UserInfo userInfo = this.getVerifiedUserInfoFromHeader(request.headers())
                        .orElseThrow(() -> new AppException(401, "Unauthorized. Please provide access token."));

                if (!rolesAllowed.stream().anyMatch(
                        roleAllowed -> userInfo.getGroups().contains(roleAllowed))) {
                    throw new AppException(403, "Forbidden. You are not allowed to execute the action.");
                }
            }

            handler.accept((JsonObject jsonResponse) -> {
                if (jsonResponse != null) {
                    response.send(jsonResponse);
                }
            },
                    (Throwable t) -> {
                        handleErrorResponse(response, t);
                    });

        } catch (AppException e) {
            handleErrorResponse(response, e);
        }
    }

    protected Optional<UserInfo> getVerifiedUserInfoFromHeader(RequestHeaders headers) {
        return headers.first("x-amz-access-token")
                .map(accessToken -> {
                    UserInfo userInfo = tokenUtils.getVerifiedUserInfo(accessToken)
                            .orElseThrow(
                                    () -> new AppException(401,
                                            "Unauthorized. Could not validate provided access token."));
                    return userInfo;
                });
    }

    private static void handleErrorResponse(ServerResponse response, Throwable t) {
        AppException e = (t instanceof AppException ae) ? ae
                : (new AppException(500, "Unexpected error. " + t.getMessage()));
        JsonObject jsonResponse = wrapException(e);
        response.status(e.getStatus());
        response.send(jsonResponse);
    }

    private static JsonObject wrapException(AppException e) {
        return JSON.createObjectBuilder()
                .add("error", e.getMessage())
                .build();
    }

}
