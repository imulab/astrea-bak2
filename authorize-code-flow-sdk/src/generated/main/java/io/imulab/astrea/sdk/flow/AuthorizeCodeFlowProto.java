// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: authorize_code_service.proto

package io.imulab.astrea.sdk.flow;

public final class AuthorizeCodeFlowProto {
  private AuthorizeCodeFlowProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_CodeRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_CodeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_CodeRequest_Client_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_CodeRequest_Client_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_CodeRequest_Session_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_CodeRequest_Session_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_CodeResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_CodeResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_TokenRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_TokenRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_TokenRequest_Client_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_TokenRequest_Client_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_TokenResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_TokenResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_CodePackage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_CodePackage_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_TokenPackage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_TokenPackage_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_Failure_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_Failure_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_client_Failure_HeadersEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_client_Failure_HeadersEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\034authorize_code_service.proto\022\006client\"\263" +
      "\003\n\013CodeRequest\022\n\n\002id\030\001 \001(\t\022\023\n\013requestTim" +
      "e\030\002 \001(\003\022\025\n\rresponseTypes\030\003 \003(\t\022\023\n\013redire" +
      "ctUri\030\004 \001(\t\022\r\n\005state\030\005 \001(\t\022\016\n\006scopes\030\006 \003" +
      "(\t\022*\n\006client\030\007 \001(\0132\032.client.CodeRequest." +
      "Client\022,\n\007session\030\010 \001(\0132\033.client.CodeReq" +
      "uest.Session\032Q\n\006Client\022\n\n\002id\030\001 \001(\t\022\025\n\rre" +
      "sponseTypes\030\002 \003(\t\022\024\n\014redirectUris\030\003 \003(\t\022" +
      "\016\n\006scopes\030\004 \003(\t\032\212\001\n\007Session\022\017\n\007subject\030\001" +
      " \001(\t\022\025\n\rgrantedScopes\030\002 \003(\t\022\032\n\022authentic" +
      "ationTime\030\003 \001(\003\022\021\n\tacrValues\030\004 \003(\t\022\r\n\005no" +
      "nce\030\005 \001(\t\022\031\n\021obfuscatedSubject\030\006 \001(\t\"d\n\014" +
      "CodeResponse\022\017\n\007success\030\001 \001(\010\022!\n\004data\030\002 " +
      "\001(\0132\023.client.CodePackage\022 \n\007failure\030\003 \001(" +
      "\0132\017.client.Failure\"\220\003\n\014TokenRequest\022\n\n\002i" +
      "d\030\001 \001(\t\022\023\n\013requestTime\030\002 \001(\003\022\021\n\tgrantTyp" +
      "e\030\003 \001(\t\022\014\n\004code\030\004 \001(\t\022\023\n\013redirectUri\030\005 \001" +
      "(\t\022+\n\006client\030\006 \001(\0132\033.client.TokenRequest" +
      ".Client\032\373\001\n\006Client\022\n\n\002id\030\001 \001(\t\022\022\n\ngrantT" +
      "ypes\030\002 \003(\t\022\024\n\014redirectUris\030\003 \003(\t\022\014\n\004jwks" +
      "\030\004 \001(\t\022\033\n\023sectorIdentifierUri\030\005 \001(\t\022\023\n\013s" +
      "ubjectType\030\006 \001(\t\022&\n\036idTokenSignedRespons" +
      "eAlgorithm\030\007 \001(\t\022)\n!idTokenEncryptedResp" +
      "onseAlgorithm\030\010 \001(\t\022(\n idTokenEncryptedR" +
      "esponseEncoding\030\t \001(\t\"f\n\rTokenResponse\022\017" +
      "\n\007success\030\001 \001(\010\022\"\n\004data\030\002 \001(\0132\024.client.T" +
      "okenPackage\022 \n\007failure\030\003 \001(\0132\017.client.Fa" +
      "ilure\"+\n\013CodePackage\022\014\n\004code\030\001 \001(\t\022\016\n\006sc" +
      "opes\030\002 \003(\t\"p\n\014TokenPackage\022\023\n\013accessToke" +
      "n\030\001 \001(\t\022\021\n\ttokenType\030\002 \001(\t\022\021\n\texpiresIn\030" +
      "\003 \001(\003\022\024\n\014refreshToken\030\004 \001(\t\022\017\n\007idToken\030\005" +
      " \001(\t\"\234\001\n\007Failure\022\r\n\005error\030\001 \001(\t\022\023\n\013descr" +
      "iption\030\002 \001(\t\022\016\n\006status\030\003 \001(\005\022-\n\007headers\030" +
      "\004 \003(\0132\034.client.Failure.HeadersEntry\032.\n\014H" +
      "eadersEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\t:" +
      "\0028\0012\204\001\n\021AuthorizeCodeFlow\0226\n\tAuthorize\022\023" +
      ".client.CodeRequest\032\024.client.CodeRespons" +
      "e\0227\n\010Exchange\022\024.client.TokenRequest\032\025.cl" +
      "ient.TokenResponseB5\n\031io.imulab.astrea.s" +
      "dk.flowB\026AuthorizeCodeFlowProtoP\001b\006proto" +
      "3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_client_CodeRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_client_CodeRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_CodeRequest_descriptor,
        new java.lang.String[] { "Id", "RequestTime", "ResponseTypes", "RedirectUri", "State", "Scopes", "Client", "Session", });
    internal_static_client_CodeRequest_Client_descriptor =
      internal_static_client_CodeRequest_descriptor.getNestedTypes().get(0);
    internal_static_client_CodeRequest_Client_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_CodeRequest_Client_descriptor,
        new java.lang.String[] { "Id", "ResponseTypes", "RedirectUris", "Scopes", });
    internal_static_client_CodeRequest_Session_descriptor =
      internal_static_client_CodeRequest_descriptor.getNestedTypes().get(1);
    internal_static_client_CodeRequest_Session_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_CodeRequest_Session_descriptor,
        new java.lang.String[] { "Subject", "GrantedScopes", "AuthenticationTime", "AcrValues", "Nonce", "ObfuscatedSubject", });
    internal_static_client_CodeResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_client_CodeResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_CodeResponse_descriptor,
        new java.lang.String[] { "Success", "Data", "Failure", });
    internal_static_client_TokenRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_client_TokenRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_TokenRequest_descriptor,
        new java.lang.String[] { "Id", "RequestTime", "GrantType", "Code", "RedirectUri", "Client", });
    internal_static_client_TokenRequest_Client_descriptor =
      internal_static_client_TokenRequest_descriptor.getNestedTypes().get(0);
    internal_static_client_TokenRequest_Client_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_TokenRequest_Client_descriptor,
        new java.lang.String[] { "Id", "GrantTypes", "RedirectUris", "Jwks", "SectorIdentifierUri", "SubjectType", "IdTokenSignedResponseAlgorithm", "IdTokenEncryptedResponseAlgorithm", "IdTokenEncryptedResponseEncoding", });
    internal_static_client_TokenResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_client_TokenResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_TokenResponse_descriptor,
        new java.lang.String[] { "Success", "Data", "Failure", });
    internal_static_client_CodePackage_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_client_CodePackage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_CodePackage_descriptor,
        new java.lang.String[] { "Code", "Scopes", });
    internal_static_client_TokenPackage_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_client_TokenPackage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_TokenPackage_descriptor,
        new java.lang.String[] { "AccessToken", "TokenType", "ExpiresIn", "RefreshToken", "IdToken", });
    internal_static_client_Failure_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_client_Failure_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_Failure_descriptor,
        new java.lang.String[] { "Error", "Description", "Status", "Headers", });
    internal_static_client_Failure_HeadersEntry_descriptor =
      internal_static_client_Failure_descriptor.getNestedTypes().get(0);
    internal_static_client_Failure_HeadersEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_client_Failure_HeadersEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
