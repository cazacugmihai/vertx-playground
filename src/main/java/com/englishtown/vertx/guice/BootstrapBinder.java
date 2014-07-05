package com.englishtown.vertx.guice;

import com.google.inject.AbstractModule;
import de.frostcode.vertx.playground.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class BootstrapBinder extends AbstractModule
{
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  protected void configure()
  {
    LOG.info("Configuring binder");
    bind(PingService.class).toInstance(new PingService());
  }
}
