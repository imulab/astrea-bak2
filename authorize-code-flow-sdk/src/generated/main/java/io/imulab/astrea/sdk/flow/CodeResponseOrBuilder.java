// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: authorize_code_service.proto

package io.imulab.astrea.sdk.flow;

public interface CodeResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:client.CodeResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool success = 1;</code>
   */
  boolean getSuccess();

  /**
   * <code>.client.CodePackage data = 2;</code>
   */
  boolean hasData();
  /**
   * <code>.client.CodePackage data = 2;</code>
   */
  io.imulab.astrea.sdk.flow.CodePackage getData();
  /**
   * <code>.client.CodePackage data = 2;</code>
   */
  io.imulab.astrea.sdk.flow.CodePackageOrBuilder getDataOrBuilder();

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
