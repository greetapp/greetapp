package io.helidon.webserver;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;

import io.helidon.common.http.DataChunk;
import io.helidon.common.http.Http.RequestMethod;
import io.helidon.common.http.Http.Version;
import io.helidon.common.reactive.BufferedEmittingPublisher;
import io.helidon.common.reactive.Single;

public class HelidonLambdaRequest implements BareRequest {

    private static long requestCount = 0;

    private URI uri;
    private RequestMethod method;
    private Version version;
    private BufferedEmittingPublisher<DataChunk> bodyPublisher;
    private long requestId;
    private Map<String, List<String>> headers;
    private WebServer webServer;

    public static HelidonLambdaRequest fromAPIGatewayV2HTTPEvent(CustomAPIGatewayV2HTTPEvent event,
            WebServer webServer) {
        String uriStr = "https://" + event.getRequestContext().getDomainName() + event.getRawPath();
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI: " + uriStr);
        }
        RequestMethod method = RequestMethod.create(event.getRequestContext().getHttp().getMethod());
        Version version = Version.create(event.getRequestContext().getHttp().getProtocol());
        Map<String, List<String>> headers = new HashMap<>();
        for (Map.Entry<String, String> entry : event.getHeaders().entrySet()) {
            if (!headers.containsKey(entry.getKey())) {
                headers.put(entry.getKey(), new ArrayList<>());
            }
            headers.get(entry.getKey())
                    .add(entry.getValue());
        }
        String body = event.getBody();
        return new HelidonLambdaRequest(uri, method, version, headers, body, webServer);
    }

    public HelidonLambdaRequest(URI uri, RequestMethod method, Version version, Map<String, List<String>> headers,
            String body, WebServer webServer) {
        this.uri = uri;
        this.method = method;
        this.version = version;
        this.bodyPublisher = BufferedEmittingPublisher.create();
        this.bodyPublisher.emit(DataChunk.create((body == null ? "" : body).getBytes(StandardCharsets.UTF_8)));
        this.requestId = requestCount++;
        this.headers = headers;
        this.webServer = webServer;
    }

    @Override
    public WebServer webServer() {
        return this.webServer;
    }

    @Override
    public SocketConfiguration socketConfiguration() {
        throw new UnsupportedOperationException("Unimplemented method 'socketConfiguration'");
    }

    @Override
    public RequestMethod method() {
        return method;
    }

    @Override
    public Version version() {
        return version;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public String localAddress() {
        throw new UnsupportedOperationException("Unimplemented method 'localAddress'");
    }

    @Override
    public int localPort() {
        throw new UnsupportedOperationException("Unimplemented method 'localPort'");
    }

    @Override
    public String remoteAddress() {
        throw new UnsupportedOperationException("Unimplemented method 'remoteAddress'");
    }

    @Override
    public int remotePort() {
        throw new UnsupportedOperationException("Unimplemented method 'remotePort'");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Unimplemented method 'isSecure'");
    }

    @Override
    public Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public Publisher<DataChunk> bodyPublisher() {
        return bodyPublisher;
    }

    @Override
    public long requestId() {
        return requestId;
    }

    @Override
    public Single<Void> closeConnection() {
        throw new UnsupportedOperationException("Unimplemented method 'closeConnection'");
    }

}
