// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: qrcode.proto

package io.grpc.examples.qrcode;

public final class QrcodeProto {
  private QrcodeProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_qrcode_QrRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_qrcode_QrRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_qrcode_QrReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_qrcode_QrReply_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014qrcode.proto\022\006qrcode\"\031\n\tQrRequest\022\014\n\004n" +
      "ame\030\001 \001(\t\"\032\n\007QrReply\022\017\n\007message\030\001 \001(\t2@\n" +
      "\tGenerator\0223\n\013QrGenerator\022\021.qrcode.QrReq" +
      "uest\032\017.qrcode.QrReply\"\000B.\n\027io.grpc.examp" +
      "les.qrcodeB\013QrcodeProtoP\001\242\002\003QRCb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_qrcode_QrRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_qrcode_QrRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_qrcode_QrRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_qrcode_QrReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_qrcode_QrReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_qrcode_QrReply_descriptor,
        new java.lang.String[] { "Message", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}