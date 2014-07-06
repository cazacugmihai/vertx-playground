/*
 * Copyright 2013 Red Hat, Inc. Red Hat licenses this file to you under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
package com.mycompany.myproject.test.integration.java;

import org.junit.Test;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestUtils;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Example Java integration test that deploys the module that this project builds. Quite often in integration tests you
 * want to deploy the same module for all tests and you don't want tests to start before the module has been deployed.
 * This test demonstrates how to do that.
 */
public class ModuleIntegrationTest extends TestVerticle
{
  @Override
  protected void initialize()
  {
    container.config().putString("vertx.conf", "conf.json");
    container.config().putString("conf", "conf.json");
    super.initialize();
  }

  @Test
  public void testPingOverEventBus()
  {
    container.logger().info("in testPingOverEventBus()");
    vertx.eventBus().send("ping-address", "ping!", (Message<String> reply) ->
    {
      VertxAssert.assertEquals("pong!", reply.body());

      /*
       * If we get here, the test is complete You must always call `testComplete()` at the end. Remember that testing
       * is *asynchronous* so we cannot assume the test is complete by the time the test method has finished executing
       * like in standard synchronous tests
       */
      testComplete();
    });
  }

  @Test
  public void testPingOverHttp()
  {
    container.logger().info("in testPingOverHttp()");
    vertx.createHttpClient().setPort(8080).getNow("/", resp ->
    {
      assertEquals(200, resp.statusCode());
      resp.bodyHandler(buffer ->
      {
        VertxAssert.assertEquals("pong!", buffer.toString());
        testComplete();
      });
    });
  }

  @Test
  public void testPingOverWebsocket()
  {
    HttpClient client = vertx.createHttpClient().setPort(8080);
    client.connectWebsocket("/eventbus/websocket", websocket ->
    {
      // register
      String replyAddress = TestUtils.randomAlphaString(10);
      JsonObject msg = new JsonObject().putString("type", "register").putString("address", replyAddress);
      websocket.writeTextFrame(msg.encode());

      // send
      String knownMessage = "hello world";
      msg = new JsonObject().putString("type", "send").putString("address", "echo-address")
          .putString("replyAddress", replyAddress).putString("body", knownMessage);
      websocket.writeTextFrame(msg.encode());

      // verify
      websocket.dataHandler(buffer ->
      {
        JsonObject received = new JsonObject(buffer.toString());
        VertxAssert.assertEquals("Received: " + knownMessage, received.getString("body"));
        testComplete();
      });
    });
  }

  @Test
  public void testSomethingElse()
  {
    // Whatever
    testComplete();
  }

  @Override
  public void start()
  {
    // Make sure we call initialize() - this sets up the assert stuff so assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
    // don't have to hardcode it in your tests
    container.deployModule(System.getProperty("vertx.modulename"), asyncResult ->
    {
      // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
      if(asyncResult.failed())
      {
        container.logger().error(asyncResult.cause());
      }
      assertTrue(asyncResult.succeeded());
      assertNotNull("deploymentID should not be null", asyncResult.result());
      // If deployed correctly then start the tests!
      startTests();
    });
  }
}
