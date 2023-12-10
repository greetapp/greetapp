package io.github.greetapp;

import java.util.function.BiConsumer;

import io.helidon.config.Config;
import io.helidon.webserver.HelidonLambdaWebServer;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

public class Main {

    public void startServer(BiConsumer<Routing, WebServer> lambdaAdapterConsumer) {
        io.helidon.common.LogConfig.configureRuntime();
        Config config = Config.create();

        Routing routing = createRouting(config);
        WebServer webServer = new HelidonLambdaWebServer();

        lambdaAdapterConsumer.accept(routing, webServer);
    }

    private Routing createRouting(Config config) {
        DDBService ddbService = this.getDDBService();
        TokenUtils tokenUtils = this.getTokenUtils(config);
        AwsHandlerWrapper awsHandlerWrapper = this.getAwsHandlerWrapper(tokenUtils);

        GreetService greetService = new GreetService(ddbService, awsHandlerWrapper);

        Routing.Builder builder = Routing.builder()
                .register("/greet", greetService);

        return builder.build();
    }

    protected DDBService getDDBService() {
        return new DDBService();
    }

    protected TokenUtils getTokenUtils(Config config) {
        return new TokenUtils(config);
    }

    protected AwsHandlerWrapper getAwsHandlerWrapper(TokenUtils tokenUtils) {
        return new AwsHandlerWrapper(tokenUtils);
    }

}
