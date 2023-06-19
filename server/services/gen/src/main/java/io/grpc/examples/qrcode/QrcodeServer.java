/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.qrcode;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.examples.qrcode.GeneratorGrpc;
import io.grpc.examples.qrcode.QrReply;
import io.grpc.examples.qrcode.QrRequest;
import io.grpc.examples.qrcode.Qrcode;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Generator} server.
 */
public class QrcodeServer {
  private static final Logger logger = Logger.getLogger(QrcodeServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
        .addService(new GeneratorImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown
        // hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          QrcodeServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon
   * threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final QrcodeServer server = new QrcodeServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class GeneratorImpl extends GeneratorGrpc.GeneratorImplBase {

    @Override
    public void qrGenerator(QrRequest req, StreamObserver<QrReply> responseObserver) {
      Qrcode qrcode = new Qrcode();
      QrReply reply = QrReply.newBuilder().setMessage(qrcode.createoriginal()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    public void getkeiretu(QrRequest req, StreamObserver<QrReply> responseObserver) {
      int[] keiretu = new int[26];
      String[] strkeiretu = new String[keiretu.length];
      for (int i = 0; i < keiretu.length; i++) {
        strkeiretu[i] = String.valueOf(keiretu[i]);
      }
      QrReply reply = QrReply.newBuilder().setMessage(Arrays.toString(strkeiretu)).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    public void getotp(QrRequest req, StreamObserver<QrReply> responseObserver) {
      Qrcode qrcode = new Qrcode();
      QrReply reply = QrReply.newBuilder().setMessage(qrcode.createerror(req.getName())).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
