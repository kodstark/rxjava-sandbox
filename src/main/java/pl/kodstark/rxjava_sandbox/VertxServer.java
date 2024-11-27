package pl.kodstark.rxjava_sandbox;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class VertxServer extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VertxServer.class);

  private final int listenPort;

  private HttpServer server;

  @Override
  public void start(Promise<Void> startPromise) {
    server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route("/hello").handler(ctx -> ctx.response().end("Hello from Vert.x!"));
    router.route("/stream").handler(ctx -> new FileStreamer(ctx).streamFile());
    server.requestHandler(router);
    server.listen(
        listenPort,
        ar -> {
          if (ar.succeeded()) {
            LOG.info("Vert.x HTTP server started on port {}", ar.result().actualPort());
            startPromise.complete();
          } else {
            startPromise.fail(ar.cause());
          }
        });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    if (server != null) {
      server.close(
          ar -> {
            if (ar.succeeded()) {
              LOG.info("Vert.x HTTP server stopped on {}", server.actualPort());
              stopPromise.complete();
            } else {
              stopPromise.fail(ar.cause());
            }
          });
    } else {
      stopPromise.complete();
    }
  }
}
