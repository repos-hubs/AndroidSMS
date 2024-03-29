// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ResponseProtoc.proto

package com.kindroid.kincent.protoc;

public final class ResponseProtoc {
  private ResponseProtoc() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class ResponseContext extends
      com.google.protobuf.GeneratedMessage {
    // Use ResponseContext.newBuilder() to construct.
    private ResponseContext() {
      initFields();
    }
    private ResponseContext(boolean noInit) {}
    
    private static final ResponseContext defaultInstance;
    public static ResponseContext getDefaultInstance() {
      return defaultInstance;
    }
    
    public ResponseContext getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kindroid.kincent.protoc.ResponseProtoc.internal_static_com_kindroid_kincent_protoc_ResponseContext_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kindroid.kincent.protoc.ResponseProtoc.internal_static_com_kindroid_kincent_protoc_ResponseContext_fieldAccessorTable;
    }
    
    // required int32 result = 1;
    public static final int RESULT_FIELD_NUMBER = 1;
    private boolean hasResult;
    private int result_ = 0;
    public boolean hasResult() { return hasResult; }
    public int getResult() { return result_; }
    
    // optional int32 errNO = 2;
    public static final int ERRNO_FIELD_NUMBER = 2;
    private boolean hasErrNO;
    private int errNO_ = 0;
    public boolean hasErrNO() { return hasErrNO; }
    public int getErrNO() { return errNO_; }
    
    // optional string errMsg = 3;
    public static final int ERRMSG_FIELD_NUMBER = 3;
    private boolean hasErrMsg;
    private java.lang.String errMsg_ = "";
    public boolean hasErrMsg() { return hasErrMsg; }
    public java.lang.String getErrMsg() { return errMsg_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      if (!hasResult) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasResult()) {
        output.writeInt32(1, getResult());
      }
      if (hasErrNO()) {
        output.writeInt32(2, getErrNO());
      }
      if (hasErrMsg()) {
        output.writeString(3, getErrMsg());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasResult()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getResult());
      }
      if (hasErrNO()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, getErrNO());
      }
      if (hasErrMsg()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(3, getErrMsg());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext result;
      
      // Construct using com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext();
        return builder;
      }
      
      protected com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDescriptor();
      }
      
      public com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext getDefaultInstanceForType() {
        return com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext) {
          return mergeFrom((com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext other) {
        if (other == com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDefaultInstance()) return this;
        if (other.hasResult()) {
          setResult(other.getResult());
        }
        if (other.hasErrNO()) {
          setErrNO(other.getErrNO());
        }
        if (other.hasErrMsg()) {
          setErrMsg(other.getErrMsg());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setResult(input.readInt32());
              break;
            }
            case 16: {
              setErrNO(input.readInt32());
              break;
            }
            case 26: {
              setErrMsg(input.readString());
              break;
            }
          }
        }
      }
      
      
      // required int32 result = 1;
      public boolean hasResult() {
        return result.hasResult();
      }
      public int getResult() {
        return result.getResult();
      }
      public Builder setResult(int value) {
        result.hasResult = true;
        result.result_ = value;
        return this;
      }
      public Builder clearResult() {
        result.hasResult = false;
        result.result_ = 0;
        return this;
      }
      
      // optional int32 errNO = 2;
      public boolean hasErrNO() {
        return result.hasErrNO();
      }
      public int getErrNO() {
        return result.getErrNO();
      }
      public Builder setErrNO(int value) {
        result.hasErrNO = true;
        result.errNO_ = value;
        return this;
      }
      public Builder clearErrNO() {
        result.hasErrNO = false;
        result.errNO_ = 0;
        return this;
      }
      
      // optional string errMsg = 3;
      public boolean hasErrMsg() {
        return result.hasErrMsg();
      }
      public java.lang.String getErrMsg() {
        return result.getErrMsg();
      }
      public Builder setErrMsg(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasErrMsg = true;
        result.errMsg_ = value;
        return this;
      }
      public Builder clearErrMsg() {
        result.hasErrMsg = false;
        result.errMsg_ = getDefaultInstance().getErrMsg();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.kindroid.kincent.protoc.ResponseContext)
    }
    
    static {
      defaultInstance = new ResponseContext(true);
      com.kindroid.kincent.protoc.ResponseProtoc.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.kindroid.kincent.protoc.ResponseContext)
  }
  
  public static final class Response extends
      com.google.protobuf.GeneratedMessage {
    // Use Response.newBuilder() to construct.
    private Response() {
      initFields();
    }
    private Response(boolean noInit) {}
    
    private static final Response defaultInstance;
    public static Response getDefaultInstance() {
      return defaultInstance;
    }
    
    public Response getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kindroid.kincent.protoc.ResponseProtoc.internal_static_com_kindroid_kincent_protoc_Response_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kindroid.kincent.protoc.ResponseProtoc.internal_static_com_kindroid_kincent_protoc_Response_fieldAccessorTable;
    }
    
    // required .com.kindroid.kincent.protoc.ResponseContext context = 1;
    public static final int CONTEXT_FIELD_NUMBER = 1;
    private boolean hasContext;
    private com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext context_;
    public boolean hasContext() { return hasContext; }
    public com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext getContext() { return context_; }
    
    // optional .com.kindroid.kincent.protoc.FeedbackResponse feedbackResponse = 4;
    public static final int FEEDBACKRESPONSE_FIELD_NUMBER = 4;
    private boolean hasFeedbackResponse;
    private com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse feedbackResponse_;
    public boolean hasFeedbackResponse() { return hasFeedbackResponse; }
    public com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse getFeedbackResponse() { return feedbackResponse_; }
    
    private void initFields() {
      context_ = com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDefaultInstance();
      feedbackResponse_ = com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.getDefaultInstance();
    }
    public final boolean isInitialized() {
      if (!hasContext) return false;
      if (!getContext().isInitialized()) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasContext()) {
        output.writeMessage(1, getContext());
      }
      if (hasFeedbackResponse()) {
        output.writeMessage(4, getFeedbackResponse());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasContext()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getContext());
      }
      if (hasFeedbackResponse()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getFeedbackResponse());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.kindroid.kincent.protoc.ResponseProtoc.Response parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.kindroid.kincent.protoc.ResponseProtoc.Response prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.kindroid.kincent.protoc.ResponseProtoc.Response result;
      
      // Construct using com.kindroid.kincent.protoc.ResponseProtoc.Response.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.kindroid.kincent.protoc.ResponseProtoc.Response();
        return builder;
      }
      
      protected com.kindroid.kincent.protoc.ResponseProtoc.Response internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.kindroid.kincent.protoc.ResponseProtoc.Response();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.kindroid.kincent.protoc.ResponseProtoc.Response.getDescriptor();
      }
      
      public com.kindroid.kincent.protoc.ResponseProtoc.Response getDefaultInstanceForType() {
        return com.kindroid.kincent.protoc.ResponseProtoc.Response.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.kindroid.kincent.protoc.ResponseProtoc.Response build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.kindroid.kincent.protoc.ResponseProtoc.Response buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.kindroid.kincent.protoc.ResponseProtoc.Response buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        com.kindroid.kincent.protoc.ResponseProtoc.Response returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.kindroid.kincent.protoc.ResponseProtoc.Response) {
          return mergeFrom((com.kindroid.kincent.protoc.ResponseProtoc.Response)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.kindroid.kincent.protoc.ResponseProtoc.Response other) {
        if (other == com.kindroid.kincent.protoc.ResponseProtoc.Response.getDefaultInstance()) return this;
        if (other.hasContext()) {
          mergeContext(other.getContext());
        }
        if (other.hasFeedbackResponse()) {
          mergeFeedbackResponse(other.getFeedbackResponse());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.Builder subBuilder = com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.newBuilder();
              if (hasContext()) {
                subBuilder.mergeFrom(getContext());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setContext(subBuilder.buildPartial());
              break;
            }
            case 34: {
              com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.Builder subBuilder = com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.newBuilder();
              if (hasFeedbackResponse()) {
                subBuilder.mergeFrom(getFeedbackResponse());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setFeedbackResponse(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // required .com.kindroid.kincent.protoc.ResponseContext context = 1;
      public boolean hasContext() {
        return result.hasContext();
      }
      public com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext getContext() {
        return result.getContext();
      }
      public Builder setContext(com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.hasContext = true;
        result.context_ = value;
        return this;
      }
      public Builder setContext(com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.Builder builderForValue) {
        result.hasContext = true;
        result.context_ = builderForValue.build();
        return this;
      }
      public Builder mergeContext(com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext value) {
        if (result.hasContext() &&
            result.context_ != com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDefaultInstance()) {
          result.context_ =
            com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.newBuilder(result.context_).mergeFrom(value).buildPartial();
        } else {
          result.context_ = value;
        }
        result.hasContext = true;
        return this;
      }
      public Builder clearContext() {
        result.hasContext = false;
        result.context_ = com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.getDefaultInstance();
        return this;
      }
      
      // optional .com.kindroid.kincent.protoc.FeedbackResponse feedbackResponse = 4;
      public boolean hasFeedbackResponse() {
        return result.hasFeedbackResponse();
      }
      public com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse getFeedbackResponse() {
        return result.getFeedbackResponse();
      }
      public Builder setFeedbackResponse(com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.hasFeedbackResponse = true;
        result.feedbackResponse_ = value;
        return this;
      }
      public Builder setFeedbackResponse(com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.Builder builderForValue) {
        result.hasFeedbackResponse = true;
        result.feedbackResponse_ = builderForValue.build();
        return this;
      }
      public Builder mergeFeedbackResponse(com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse value) {
        if (result.hasFeedbackResponse() &&
            result.feedbackResponse_ != com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.getDefaultInstance()) {
          result.feedbackResponse_ =
            com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.newBuilder(result.feedbackResponse_).mergeFrom(value).buildPartial();
        } else {
          result.feedbackResponse_ = value;
        }
        result.hasFeedbackResponse = true;
        return this;
      }
      public Builder clearFeedbackResponse() {
        result.hasFeedbackResponse = false;
        result.feedbackResponse_ = com.kindroid.kincent.protoc.FeedbackProtoc.FeedbackResponse.getDefaultInstance();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.kindroid.kincent.protoc.Response)
    }
    
    static {
      defaultInstance = new Response(true);
      com.kindroid.kincent.protoc.ResponseProtoc.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.kindroid.kincent.protoc.Response)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_kindroid_kincent_protoc_ResponseContext_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_kindroid_kincent_protoc_ResponseContext_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_kindroid_kincent_protoc_Response_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_kindroid_kincent_protoc_Response_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024ResponseProtoc.proto\022\033com.kindroid.kin" +
      "cent.protoc\032\024FeedbackProtoc.proto\"@\n\017Res" +
      "ponseContext\022\016\n\006result\030\001 \002(\005\022\r\n\005errNO\030\002 " +
      "\001(\005\022\016\n\006errMsg\030\003 \001(\t\"\222\001\n\010Response\022=\n\007cont" +
      "ext\030\001 \002(\0132,.com.kindroid.kincent.protoc." +
      "ResponseContext\022G\n\020feedbackResponse\030\004 \001(" +
      "\0132-.com.kindroid.kincent.protoc.Feedback" +
      "ResponseB\035\n\033com.kindroid.kincent.protoc"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_kindroid_kincent_protoc_ResponseContext_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_kindroid_kincent_protoc_ResponseContext_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_kindroid_kincent_protoc_ResponseContext_descriptor,
              new java.lang.String[] { "Result", "ErrNO", "ErrMsg", },
              com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.class,
              com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext.Builder.class);
          internal_static_com_kindroid_kincent_protoc_Response_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_com_kindroid_kincent_protoc_Response_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_kindroid_kincent_protoc_Response_descriptor,
              new java.lang.String[] { "Context", "FeedbackResponse", },
              com.kindroid.kincent.protoc.ResponseProtoc.Response.class,
              com.kindroid.kincent.protoc.ResponseProtoc.Response.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.kindroid.kincent.protoc.FeedbackProtoc.getDescriptor(),
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
