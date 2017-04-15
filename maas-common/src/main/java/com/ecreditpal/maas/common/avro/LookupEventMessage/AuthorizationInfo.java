/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.ecreditpal.maas.common.avro.LookupEventMessage;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class AuthorizationInfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2112205403143515895L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AuthorizationInfo\",\"namespace\":\"com.ecreditpal.maas.common.avro.LookupEventMessage\",\"fields\":[{\"name\":\"is_authorized\",\"type\":\"boolean\",\"default\":false},{\"name\":\"failure_message\",\"type\":\"string\",\"default\":\"\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public boolean is_authorized;
  @Deprecated public java.lang.CharSequence failure_message;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AuthorizationInfo() {}

  /**
   * All-args constructor.
   * @param is_authorized The new value for is_authorized
   * @param failure_message The new value for failure_message
   */
  public AuthorizationInfo(java.lang.Boolean is_authorized, java.lang.CharSequence failure_message) {
    this.is_authorized = is_authorized;
    this.failure_message = failure_message;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return is_authorized;
    case 1: return failure_message;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: is_authorized = (java.lang.Boolean)value$; break;
    case 1: failure_message = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'is_authorized' field.
   * @return The value of the 'is_authorized' field.
   */
  public java.lang.Boolean getIsAuthorized() {
    return is_authorized;
  }

  /**
   * Sets the value of the 'is_authorized' field.
   * @param value the value to set.
   */
  public void setIsAuthorized(java.lang.Boolean value) {
    this.is_authorized = value;
  }

  /**
   * Gets the value of the 'failure_message' field.
   * @return The value of the 'failure_message' field.
   */
  public java.lang.CharSequence getFailureMessage() {
    return failure_message;
  }

  /**
   * Sets the value of the 'failure_message' field.
   * @param value the value to set.
   */
  public void setFailureMessage(java.lang.CharSequence value) {
    this.failure_message = value;
  }

  /**
   * Creates a new AuthorizationInfo RecordBuilder.
   * @return A new AuthorizationInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder newBuilder() {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder();
  }

  /**
   * Creates a new AuthorizationInfo RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AuthorizationInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder newBuilder(com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder other) {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder(other);
  }

  /**
   * Creates a new AuthorizationInfo RecordBuilder by copying an existing AuthorizationInfo instance.
   * @param other The existing instance to copy.
   * @return A new AuthorizationInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder newBuilder(com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo other) {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder(other);
  }

  /**
   * RecordBuilder for AuthorizationInfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AuthorizationInfo>
    implements org.apache.avro.data.RecordBuilder<AuthorizationInfo> {

    private boolean is_authorized;
    private java.lang.CharSequence failure_message;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.is_authorized)) {
        this.is_authorized = data().deepCopy(fields()[0].schema(), other.is_authorized);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.failure_message)) {
        this.failure_message = data().deepCopy(fields()[1].schema(), other.failure_message);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing AuthorizationInfo instance
     * @param other The existing instance to copy.
     */
    private Builder(com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.is_authorized)) {
        this.is_authorized = data().deepCopy(fields()[0].schema(), other.is_authorized);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.failure_message)) {
        this.failure_message = data().deepCopy(fields()[1].schema(), other.failure_message);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'is_authorized' field.
      * @return The value.
      */
    public java.lang.Boolean getIsAuthorized() {
      return is_authorized;
    }

    /**
      * Sets the value of the 'is_authorized' field.
      * @param value The value of 'is_authorized'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder setIsAuthorized(boolean value) {
      validate(fields()[0], value);
      this.is_authorized = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'is_authorized' field has been set.
      * @return True if the 'is_authorized' field has been set, false otherwise.
      */
    public boolean hasIsAuthorized() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'is_authorized' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder clearIsAuthorized() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'failure_message' field.
      * @return The value.
      */
    public java.lang.CharSequence getFailureMessage() {
      return failure_message;
    }

    /**
      * Sets the value of the 'failure_message' field.
      * @param value The value of 'failure_message'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder setFailureMessage(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.failure_message = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'failure_message' field has been set.
      * @return True if the 'failure_message' field has been set, false otherwise.
      */
    public boolean hasFailureMessage() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'failure_message' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.AuthorizationInfo.Builder clearFailureMessage() {
      failure_message = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public AuthorizationInfo build() {
      try {
        AuthorizationInfo record = new AuthorizationInfo();
        record.is_authorized = fieldSetFlags()[0] ? this.is_authorized : (java.lang.Boolean) defaultValue(fields()[0]);
        record.failure_message = fieldSetFlags()[1] ? this.failure_message : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  private static final org.apache.avro.io.DatumWriter
    WRITER$ = new org.apache.avro.specific.SpecificDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  private static final org.apache.avro.io.DatumReader
    READER$ = new org.apache.avro.specific.SpecificDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}