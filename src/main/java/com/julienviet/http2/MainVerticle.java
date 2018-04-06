package com.julienviet.http2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        vertx.createHttpServer().requestHandler(req -> {
            req.response().end("Hello World");
        }).listen(8080);
        System.out.println("HTTP server started on port 8080");

        long now = System.currentTimeMillis();

    }

}
