package com.julienviet.scalableopenapis;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;

public class Http2Server {

  public static void main(String[] args) {

    // Create a Vert.x instance
    Vertx vertx = Vertx.vertx();

    // Generate the certificate for https
    SelfSignedCertificate cert = SelfSignedCertificate.create();

    // Start an HTTP/2 server that prints hello world!
    vertx.createHttpServer(new HttpServerOptions()
        .setSsl(true)
        .setUseAlpn(true)
        .setKeyCertOptions(cert.keyCertOptions())
        .setPort(8443))
        .requestHandler(request -> {
          request.response().end("Hello World");
        }).listen();
  }
}
