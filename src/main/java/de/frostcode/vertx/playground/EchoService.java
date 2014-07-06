package de.frostcode.vertx.playground;

public class EchoService
{
  public String echo(String message)
  {
    return "Received: " + message;
  }
}
