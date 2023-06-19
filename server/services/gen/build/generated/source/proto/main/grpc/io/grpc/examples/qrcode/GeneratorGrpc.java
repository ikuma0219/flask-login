package io.grpc.examples.qrcode;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The greeting service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.55.1)",
    comments = "Source: qrcode.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GeneratorGrpc {

  private GeneratorGrpc() {}

  public static final String SERVICE_NAME = "qrcode.Generator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getQrGeneratorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QrGenerator",
      requestType = io.grpc.examples.qrcode.QrRequest.class,
      responseType = io.grpc.examples.qrcode.QrReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getQrGeneratorMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply> getQrGeneratorMethod;
    if ((getQrGeneratorMethod = GeneratorGrpc.getQrGeneratorMethod) == null) {
      synchronized (GeneratorGrpc.class) {
        if ((getQrGeneratorMethod = GeneratorGrpc.getQrGeneratorMethod) == null) {
          GeneratorGrpc.getQrGeneratorMethod = getQrGeneratorMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QrGenerator"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrReply.getDefaultInstance()))
              .setSchemaDescriptor(new GeneratorMethodDescriptorSupplier("QrGenerator"))
              .build();
        }
      }
    }
    return getQrGeneratorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getGetkeiretuMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Getkeiretu",
      requestType = io.grpc.examples.qrcode.QrRequest.class,
      responseType = io.grpc.examples.qrcode.QrReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getGetkeiretuMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply> getGetkeiretuMethod;
    if ((getGetkeiretuMethod = GeneratorGrpc.getGetkeiretuMethod) == null) {
      synchronized (GeneratorGrpc.class) {
        if ((getGetkeiretuMethod = GeneratorGrpc.getGetkeiretuMethod) == null) {
          GeneratorGrpc.getGetkeiretuMethod = getGetkeiretuMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Getkeiretu"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrReply.getDefaultInstance()))
              .setSchemaDescriptor(new GeneratorMethodDescriptorSupplier("Getkeiretu"))
              .build();
        }
      }
    }
    return getGetkeiretuMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getGetotpMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Getotp",
      requestType = io.grpc.examples.qrcode.QrRequest.class,
      responseType = io.grpc.examples.qrcode.QrReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest,
      io.grpc.examples.qrcode.QrReply> getGetotpMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply> getGetotpMethod;
    if ((getGetotpMethod = GeneratorGrpc.getGetotpMethod) == null) {
      synchronized (GeneratorGrpc.class) {
        if ((getGetotpMethod = GeneratorGrpc.getGetotpMethod) == null) {
          GeneratorGrpc.getGetotpMethod = getGetotpMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.qrcode.QrRequest, io.grpc.examples.qrcode.QrReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Getotp"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.qrcode.QrReply.getDefaultInstance()))
              .setSchemaDescriptor(new GeneratorMethodDescriptorSupplier("Getotp"))
              .build();
        }
      }
    }
    return getGetotpMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GeneratorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeneratorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeneratorStub>() {
        @java.lang.Override
        public GeneratorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeneratorStub(channel, callOptions);
        }
      };
    return GeneratorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GeneratorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeneratorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeneratorBlockingStub>() {
        @java.lang.Override
        public GeneratorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeneratorBlockingStub(channel, callOptions);
        }
      };
    return GeneratorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GeneratorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeneratorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeneratorFutureStub>() {
        @java.lang.Override
        public GeneratorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeneratorFutureStub(channel, callOptions);
        }
      };
    return GeneratorFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    default void qrGenerator(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQrGeneratorMethod(), responseObserver);
    }

    /**
     */
    default void getkeiretu(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetkeiretuMethod(), responseObserver);
    }

    /**
     */
    default void getotp(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetotpMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Generator.
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class GeneratorImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GeneratorGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Generator.
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GeneratorStub
      extends io.grpc.stub.AbstractAsyncStub<GeneratorStub> {
    private GeneratorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeneratorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeneratorStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public void qrGenerator(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQrGeneratorMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getkeiretu(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetkeiretuMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getotp(io.grpc.examples.qrcode.QrRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetotpMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Generator.
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GeneratorBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GeneratorBlockingStub> {
    private GeneratorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeneratorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeneratorBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public io.grpc.examples.qrcode.QrReply qrGenerator(io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQrGeneratorMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.grpc.examples.qrcode.QrReply getkeiretu(io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetkeiretuMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.grpc.examples.qrcode.QrReply getotp(io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetotpMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Generator.
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GeneratorFutureStub
      extends io.grpc.stub.AbstractFutureStub<GeneratorFutureStub> {
    private GeneratorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeneratorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeneratorFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.qrcode.QrReply> qrGenerator(
        io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQrGeneratorMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.qrcode.QrReply> getkeiretu(
        io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetkeiretuMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.qrcode.QrReply> getotp(
        io.grpc.examples.qrcode.QrRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetotpMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QR_GENERATOR = 0;
  private static final int METHODID_GETKEIRETU = 1;
  private static final int METHODID_GETOTP = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QR_GENERATOR:
          serviceImpl.qrGenerator((io.grpc.examples.qrcode.QrRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply>) responseObserver);
          break;
        case METHODID_GETKEIRETU:
          serviceImpl.getkeiretu((io.grpc.examples.qrcode.QrRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply>) responseObserver);
          break;
        case METHODID_GETOTP:
          serviceImpl.getotp((io.grpc.examples.qrcode.QrRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.qrcode.QrReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getQrGeneratorMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.grpc.examples.qrcode.QrRequest,
              io.grpc.examples.qrcode.QrReply>(
                service, METHODID_QR_GENERATOR)))
        .addMethod(
          getGetkeiretuMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.grpc.examples.qrcode.QrRequest,
              io.grpc.examples.qrcode.QrReply>(
                service, METHODID_GETKEIRETU)))
        .addMethod(
          getGetotpMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.grpc.examples.qrcode.QrRequest,
              io.grpc.examples.qrcode.QrReply>(
                service, METHODID_GETOTP)))
        .build();
  }

  private static abstract class GeneratorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GeneratorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.grpc.examples.qrcode.QrcodeProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Generator");
    }
  }

  private static final class GeneratorFileDescriptorSupplier
      extends GeneratorBaseDescriptorSupplier {
    GeneratorFileDescriptorSupplier() {}
  }

  private static final class GeneratorMethodDescriptorSupplier
      extends GeneratorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GeneratorMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GeneratorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GeneratorFileDescriptorSupplier())
              .addMethod(getQrGeneratorMethod())
              .addMethod(getGetkeiretuMethod())
              .addMethod(getGetotpMethod())
              .build();
        }
      }
    }
    return result;
  }
}
