package com.julienviet.http2;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
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
        vertx.deployVerticle(new MainVerticle(), ctx.asyncAssertSuccess());
        client = WebClient.create(vertx);
    }

    @After
    public void after(TestContext ctx) {
        vertx.close(ctx.asyncAssertSuccess());
    }
    @Test
    public void testUsers(TestContext ctx) {
        Async async = ctx.async();
        JsonObject expectedUser = new JsonObject().put("firstName", "Julien").put("lastName", "Viet");
        client.post(8080, "localhost", "/users")
            .as(BodyCodec.string())
            .sendJsonObject(expectedUser, ctx.asyncAssertSuccess(resp1 -> {
                ctx.assertEquals(200, resp1.statusCode());
                ctx.assertEquals("0", resp1.body());
                client.get(8080, "localhost", "/users")
                    .as(BodyCodec.jsonArray())
                    .send(ctx.asyncAssertSuccess(resp2 -> {
                        ctx.assertEquals(200, resp2.statusCode());
                        ctx.assertEquals(new JsonArray().add(expectedUser), resp2.body());
                        async.complete();
                    }));
            }));
    }
}
