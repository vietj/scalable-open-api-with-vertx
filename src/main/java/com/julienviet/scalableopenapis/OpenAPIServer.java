package com.julienviet.scalableopenapis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.core.http.HttpServerOptions;

public class OpenAPIServer extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new OpenAPIServer());
    }

    private JsonArray users = new JsonArray()
        .add(new JsonObject()
            .put("firstName", "Julien")
            .put("lastName", "Viet"))
        .add(new JsonObject()
            .put("firstName", "Francesco")
            .put("lastName", "Guardiani")
        );

    @Override
    public void start(final Future<Void> bootstrapFuture) throws Exception {
        // Start creating the router factory from openapi.yaml
        OpenAPI3RouterFactory.create(vertx, "openapi.yaml", ar -> {
            // The router factory instantiation could fail
            if (ar.succeeded()) {
                OpenAPI3RouterFactory factory = ar.result();

                // Now you can use the factory to mount map endpoints to Vert.x handlers
                factory.addHandlerByOperationId("listUsers", (routingContext) -> {
                    RequestParameters params = routingContext.get("parsedParameters");
                    // Do what you want to retrieve the users with limit
                    routingContext
                            .response()
                            .setStatusCode(200)
                            .end(users.encode());
                });
                factory.addHandlerByOperationId("addUser", (routingContext) -> {
                    RequestParameters params = routingContext.get("parsedParameters");
                    String userId = "" + users.size();
                    JsonObject user = params.body().getJsonObject();
                    users.add(user);
                    // Do what you want to add user to your persistence layer
                    routingContext
                            .response()
                            .setStatusCode(200)
                            .end(userId);
                });

                // Generate the certificate for https
                SelfSignedCertificate cert = SelfSignedCertificate.create();

                // When you finish to map endpoints you can get the router with factory.getRouter()
                Router router = factory.getRouter();
                vertx.createHttpServer(new HttpServerOptions()
                    .setSsl(true)
                    .setUseAlpn(true)
                    .setKeyCertOptions(cert.keyCertOptions())
                    .setPort(8443))
                    .requestHandler(router::accept)
                    .listen();

                bootstrapFuture.complete();

            } else {
                bootstrapFuture.fail(ar.cause());
            }
        });
    }

}
