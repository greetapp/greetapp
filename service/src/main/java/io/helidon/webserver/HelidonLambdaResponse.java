package io.helidon.webserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscription;

import io.helidon.common.http.DataChunk;
import io.helidon.common.http.Http.ResponseStatus;
import io.helidon.common.reactive.Single;

public class HelidonLambdaResponse implements BareResponse {

    private final CompletableFuture<BareResponse> responseFuture;
    private final long BACKPRESSURE_BUFFER_SIZE = 5 * 1024 * 1024;
    private ServerResponseSubscription subscription;
    private ResponseStatus status;
    private String responseBody;

    public HelidonLambdaResponse() {
        this.responseFuture = new CompletableFuture<>();
    }

    @Override
    public void writeStatusAndHeaders(ResponseStatus status, Map<String, List<String>> headers)
            throws SocketClosedException, NullPointerException {
        this.status = status;
    }

    @Override
    public Single<BareResponse> whenHeadersCompleted() {
        return Single.empty();
    }

    @Override
    public Single<BareResponse> whenCompleted() {
        return Single.create(responseFuture);
    }

    @Override
    public void backpressureStrategy(BackpressureStrategy backpressureStrategy) {
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (this.subscription != null) {
            subscription.cancel();
            return;
        }

        this.subscription = BackpressureStrategy.UNBOUNDED.createSubscription(
                Objects.requireNonNull(subscription, "subscription is null"), BACKPRESSURE_BUFFER_SIZE);
        this.subscription.onSubscribe();
    }

    @Override
    public void onNext(DataChunk data) throws SocketClosedException {
        this.responseBody = new String(data.bytes(), StandardCharsets.UTF_8);
    }

    @Override
    public void onError(Throwable thr) {
        responseFuture.complete(this);
        this.subscription.cancel();
    }

    @Override
    public void onComplete() {
        responseFuture.complete(this);
    }

    @Override
    public long requestId() {
        throw new UnsupportedOperationException("Unimplemented method 'requestId'");
    }

    public int getStatusCode() {
        return this.status.code();
    }

    public String getBody() {
        return this.responseBody;
    }

}
