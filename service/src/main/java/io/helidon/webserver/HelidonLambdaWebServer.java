package io.helidon.webserver;

import io.helidon.common.context.Context;
import io.helidon.common.reactive.Single;
import io.helidon.media.common.MediaContext;
import io.helidon.media.common.MessageBodyReaderContext;
import io.helidon.media.common.MessageBodyWriterContext;
import io.helidon.media.jsonp.JsonpSupport;

public class HelidonLambdaWebServer implements WebServer {

    private static final MediaContext DEFAULT_MEDIA_SUPPORT = MediaContext.create();

    private final Context contextualRegistry;
    private final MessageBodyReaderContext readerContext;
    private final MessageBodyWriterContext writerContext;

    public HelidonLambdaWebServer() {
        contextualRegistry = Context.create();
        readerContext = MessageBodyReaderContext.create(DEFAULT_MEDIA_SUPPORT.readerContext());
        writerContext = MessageBodyWriterContext.create(DEFAULT_MEDIA_SUPPORT.writerContext());
        JsonpSupport.create().register(readerContext, writerContext);
    }

    @Override
    public ServerConfiguration configuration() {
        return null;
    }

    @Override
    public Single<WebServer> start() {
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    @Override
    public Single<WebServer> whenShutdown() {
        throw new UnsupportedOperationException("Unimplemented method 'whenShutdown'");
    }

    @Override
    public Single<WebServer> shutdown() {
        throw new UnsupportedOperationException("Unimplemented method 'shutdown'");
    }

    @Override
    public boolean isRunning() {
        throw new UnsupportedOperationException("Unimplemented method 'isRunning'");
    }

    @Override
    public Context context() {
        return contextualRegistry;
    }

    @Override
    public MessageBodyReaderContext readerContext() {
        return readerContext;
    }

    @Override
    public MessageBodyWriterContext writerContext() {
        return writerContext;
    }

    @Override
    public int port(String socketName) {
        throw new UnsupportedOperationException("Unimplemented method 'port'");
    }

    @Override
    public boolean hasTls(String socketName) {
        throw new UnsupportedOperationException("Unimplemented method 'hasTls'");
    }

    @Override
    public void updateTls(WebServerTls tls) {
        throw new UnsupportedOperationException("Unimplemented method 'updateTls'");
    }

    @Override
    public void updateTls(WebServerTls tls, String socketName) {
        throw new UnsupportedOperationException("Unimplemented method 'updateTls'");
    }

}
