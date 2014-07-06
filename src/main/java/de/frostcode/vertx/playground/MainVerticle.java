/*
 * Copyright 2013 Red Hat, Inc. Red Hat licenses this file to you under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package de.frostcode.vertx.playground;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

import java.lang.invoke.MethodHandles;

public class MainVerticle extends Verticle
{
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final PingService pingService;
  private final EchoService echoService;

  @Inject
  public MainVerticle(final PingService pingService, final EchoService echoService)
  {
    this.pingService = pingService;
    this.echoService = echoService;
  }

  @Override
  public void start()
  {
    createEventBusPingHandler();
    createEventBusEchoHandler();

    final HttpServer httpServer = vertx.createHttpServer();

    createPingHttpHandler(httpServer);

    createSockJsEventBusBridge(httpServer);

    httpServer.listen(8080);

    LOG.info("MainVerticle started [counter to test redeployment: 1]");
  }

  private void createEventBusPingHandler()
  {
    vertx.eventBus().registerHandler("ping-address", message ->
    {
      message.reply(pingService.pong());
      LOG.info("Sent back pong");
    });
  }

  private void createEventBusEchoHandler()
  {
    vertx.eventBus().registerHandler("echo-address", message ->
    {
      message.reply(echoService.echo(message.body().toString()));
      LOG.info("Sent back echo");
    });
  }

  private void createPingHttpHandler(final HttpServer httpServer)
  {
    httpServer.requestHandler(req ->
    {
      req.response().end(pingService.pong());
      LOG.info("Sent back pong");
    });
  }

  private void createSockJsEventBusBridge(final HttpServer httpServer)
  {
    final JsonArray permitted = new JsonArray();
    permitted.add(new JsonObject()); // let everything through
    final SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
    sockJSServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);
  }
}
