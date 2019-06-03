/*
 * Copyright (c) 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
package org.dacapo.harness;

import java.io.File;
import java.lang.reflect.Method;

import org.dacapo.harness.Benchmark;
import org.dacapo.parser.Config;

/**
 * Dacapo benchmark harness for tradesoap.
 * 
 * date:  $Date: 2009-12-24 11:19:36 +1100 (Thu, 24 Dec 2009) $
 * id: $Id: Tradesoap.java 738 2009-12-24 00:19:36Z steveb-oss $
 */
public class Tradesoap extends Benchmark {
  private Method initializeMethod;
  private Method shutdownMethod;

  public Tradesoap(Config config, File scratch, File data) throws Exception {
    super(config, scratch, data, false);
    Class<?> clazz = Class.forName("org.dacapo.daytrader.Launcher", true, loader);
    this.initializeMethod = clazz.getMethod("initialize", new Class[] { File.class, Integer.TYPE, String.class, Boolean.TYPE });
    this.method = clazz.getMethod("performIteration", new Class[] {});
    this.shutdownMethod = clazz.getMethod("shutdown", new Class[] {});
  }

  @Override
  protected void prepare() throws Exception {
    unpackZipFileResource("dat/daytrader.zip", scratch);
  }

  @Override
  protected void prepare(String size) throws Exception {
    String[] args = config.preprocessArgs(size, scratch, data);
    String dtSize = "medium";
    if (args.length == 1)
      dtSize = args[0];

    initializeMethod.invoke(null, scratch, config.getThreadCount(size), dtSize, false);
  }

  public void cleanup() {
    try {
      shutdownMethod.invoke(null);
    }catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Shutting down Wildfly...");
    if (!getPreserve()) {
      deleteTree(new File(scratch, "wildfly-16.0.0.Final"));
      deleteTree(new File(scratch, "jar"));
    }
  }

  public void iterate(String size) throws Exception {
    if (getVerbose())
      System.out.println("tradesoap benchmark starting");
    method.invoke(null);
  }
}
