package io.github.greetapp;

import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.cors.CrossOriginConfig;

public class Main {

    protected Main() {
    }

    public static void main(final String[] args) {
        new Main().startServer();
    }

    Single<WebServer> startServer() {
        LogConfig.configureRuntime();
        Config config = Config.create();

        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();

        Single<WebServer> webserver = server.start();

        webserver.thenAccept(ws -> {
            System.out.println("WEB server is up! http://localhost:" + ws.port() + "/greet");
            ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
        })
                .exceptionallyAccept(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                });

        return webserver;
    }

    private Routing createRouting(Config config) {
        DDBService ddbService = this.getDDBService();
        TokenUtils tokenUtils = this.getTokenUtils(config);
        AwsHandlerWrapper awsHandlerWrapper = this.getAwsHandlerWrapper(tokenUtils);

        GreetService greetService = new GreetService(ddbService, awsHandlerWrapper);

        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())
                .build();

        CorsSupport corsSupport = CorsSupport.builder()
                .addCrossOrigin(CrossOriginConfig.builder()
                        .allowOrigins(this.getCorsOrigin1(config), this.getCorsOrigin2(config))
                        .allowMethods("GET", "PUT")
                        .build())
                .addCrossOrigin(CrossOriginConfig.create())
                .build();

        Routing.Builder builder = Routing.builder()
                .register(MetricsSupport.create()) // Metrics at "/metrics"
                .register(health) // Health at "/health"
                .register("/greet", corsSupport, greetService);

        return builder.build();
    }

    protected String getCorsOrigin1(Config config) {
        return readProperty(config, "cors.origin1");
    }

    protected String getCorsOrigin2(Config config) {
        return readProperty(config, "cors.origin2");
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

    private static String readProperty(Config config, String key) {
        return config.get(key).asString()
                .orElseThrow(() -> new AppException("Property not found in configuration: %s".formatted(key)));
    }

}
