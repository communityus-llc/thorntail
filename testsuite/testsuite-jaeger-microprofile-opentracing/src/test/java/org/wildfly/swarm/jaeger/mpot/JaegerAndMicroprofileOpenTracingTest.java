/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.swarm.jaeger.mpot;

import static org.junit.Assert.assertTrue;

import io.jaegertracing.Configuration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;


/**
 * @author Pavol Loffay
 */
@RunWith(Arquillian.class)
public class JaegerAndMicroprofileOpenTracingTest {

  @Deployment
  public static Archive createDeployment() {
    WARArchive deployment = ShrinkWrap.create(WARArchive.class);
    deployment.addClass(JaxrsResource.class);
    deployment.addClass(JaxrsApplication.class);
    deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    return deployment;
  }

  @BeforeClass
  public static void setup() throws Exception {
    System.setProperty(Configuration.JAEGER_SERVICE_NAME, "foo");
  }

  @AfterClass
  public static void tearDown() throws Exception {
    System.clearProperty(Configuration.JAEGER_SERVICE_NAME);
  }

  @Test
  public void test() throws IOException {
    String body = hitEndpoint("http://localhost:8080/");
    assertTrue(body.equals("io.jaegertracing.internal.JaegerSpan"));
  }

  private String hitEndpoint(String endpoint) throws IOException {
    InputStream response = new URL(endpoint).openStream();
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(response))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}
