// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: implicit_service.proto

package io.imulab.astrea.sdk.flow;

public interface TokenResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:client.TokenResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool success = 1;</code>
   */
  boolean getSuccess();

  /**
   * <code>.client.TokenPackage data = 2;</code>
   */
  boolean hasData();
  /**
   * <code>.client.TokenPackage data = 2;</code>
   */
  io.imulab.astrea.sdk.flow.TokenPackage getData();
  /**
   * <code>.client.TokenPackage data = 2;</code>
   */
  io.imulab.astrea.sdk.flow.TokenPackageOrBuilder getDataOrBuilder();

  /**
   * <code>.client.Failure failure = 3;</code>
   */
  boolean hasFailure();
  /**
   * <code>.client.Failure failure = 3;</code>
   */
  io.imulab.astrea.sdk.flow.Failure getFailure();
  /**
   * <code>.client.Failure failure = 3;</code>
   */
  io.imulab.astrea.sdk.flow.FailureOrBuilder getFailureOrBuilder();
}
