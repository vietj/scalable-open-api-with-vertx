package com.julienviet.scalableopenapis;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class UsersAPITest {

    private Vertx vertx;
    private WebClient client;

    @Before
    public void before(TestContext ctx) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new OpenAPIServer(), ctx.asyncAssertSuccess());
        client = WebClient.create(vertx, new WebClientOptions()
            .setSsl(true)
            .setTrustAll(true)
            .setUseAlpn(true)
            .setProtocolVersion(HttpVersion.HTTP_2));
    }

    @After
    public void after(TestContext ctx) {
        vertx.close(ctx.asyncAssertSuccess());
    }

    @Test
    public void testListUser(TestContext ctx) {
        Async async = ctx.async();
        JsonArray expectedUsers = new JsonArray()
            .add(new JsonObject().put("firstName", "Julien").put("lastName", "Viet"))
            .add(new JsonObject().put("firstName", "Francesco").put("lastName", "Guardiani"));
        client.get(8443, "localhost", "/users")
            .as(BodyCodec.jsonArray())
            .send(ctx.asyncAssertSuccess(resp2 -> {
                ctx.assertEquals(200, resp2.statusCode());
                ctx.assertEquals(expectedUsers, resp2.body());
                async.complete();
            }));
    }

    @Test
    public void testAddUser(TestContext ctx) {
        Async async = ctx.async();
        JsonObject newUser = new JsonObject().put("firstName", "Dale").put("lastName", "Cooper");
        JsonArray expectedUsers = new JsonArray()
            .add(new JsonObject().put("firstName", "Julien").put("lastName", "Viet"))
            .add(new JsonObject().put("firstName", "Francesco").put("lastName", "Guardiani"))
            .add(newUser);
        client.post(8443, "localhost", "/users")
            .as(BodyCodec.string())
            .sendJsonObject(newUser, ctx.asyncAssertSuccess(resp1 -> {
                ctx.assertEquals(200, resp1.statusCode());
                ctx.assertEquals("2", resp1.body());
                client.get(8443, "localhost", "/users")
                    .as(BodyCodec.jsonArray())
                    .send(ctx.asyncAssertSuccess(resp2 -> {
                        ctx.assertEquals(200, resp2.statusCode());
                        ctx.assertEquals(expectedUsers, resp2.body());
                        async.complete();
                    }));
            }));
    }
}
