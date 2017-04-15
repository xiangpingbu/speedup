/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.ecreditpal.maas.common.avro.LookupEventMessage;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class RequestInfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2000793293743575375L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"RequestInfo\",\"namespace\":\"com.ecreditpal.maas.common.avro.LookupEventMessage\",\"fields\":[{\"name\":\"api_name\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"request_path\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"request_method\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"query_params\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"path_params\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"form_params\",\"type\":\"string\",\"default\":\"\"},{\"name\":\"request_body\",\"type\":\"string\",\"default\":\"\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence api_name;
  @Deprecated public java.lang.CharSequence request_path;
  @Deprecated public java.lang.CharSequence request_method;
  @Deprecated public java.lang.CharSequence query_params;
  @Deprecated public java.lang.CharSequence path_params;
  @Deprecated public java.lang.CharSequence form_params;
  @Deprecated public java.lang.CharSequence request_body;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public RequestInfo() {}

  /**
   * All-args constructor.
   * @param api_name The new value for api_name
   * @param request_path The new value for request_path
   * @param request_method The new value for request_method
   * @param query_params The new value for query_params
   * @param path_params The new value for path_params
   * @param form_params The new value for form_params
   * @param request_body The new value for request_body
   */
  public RequestInfo(java.lang.CharSequence api_name, java.lang.CharSequence request_path, java.lang.CharSequence request_method, java.lang.CharSequence query_params, java.lang.CharSequence path_params, java.lang.CharSequence form_params, java.lang.CharSequence request_body) {
    this.api_name = api_name;
    this.request_path = request_path;
    this.request_method = request_method;
    this.query_params = query_params;
    this.path_params = path_params;
    this.form_params = form_params;
    this.request_body = request_body;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return api_name;
    case 1: return request_path;
    case 2: return request_method;
    case 3: return query_params;
    case 4: return path_params;
    case 5: return form_params;
    case 6: return request_body;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: api_name = (java.lang.CharSequence)value$; break;
    case 1: request_path = (java.lang.CharSequence)value$; break;
    case 2: request_method = (java.lang.CharSequence)value$; break;
    case 3: query_params = (java.lang.CharSequence)value$; break;
    case 4: path_params = (java.lang.CharSequence)value$; break;
    case 5: form_params = (java.lang.CharSequence)value$; break;
    case 6: request_body = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'api_name' field.
   * @return The value of the 'api_name' field.
   */
  public java.lang.CharSequence getApiName() {
    return api_name;
  }

  /**
   * Sets the value of the 'api_name' field.
   * @param value the value to set.
   */
  public void setApiName(java.lang.CharSequence value) {
    this.api_name = value;
  }

  /**
   * Gets the value of the 'request_path' field.
   * @return The value of the 'request_path' field.
   */
  public java.lang.CharSequence getRequestPath() {
    return request_path;
  }

  /**
   * Sets the value of the 'request_path' field.
   * @param value the value to set.
   */
  public void setRequestPath(java.lang.CharSequence value) {
    this.request_path = value;
  }

  /**
   * Gets the value of the 'request_method' field.
   * @return The value of the 'request_method' field.
   */
  public java.lang.CharSequence getRequestMethod() {
    return request_method;
  }

  /**
   * Sets the value of the 'request_method' field.
   * @param value the value to set.
   */
  public void setRequestMethod(java.lang.CharSequence value) {
    this.request_method = value;
  }

  /**
   * Gets the value of the 'query_params' field.
   * @return The value of the 'query_params' field.
   */
  public java.lang.CharSequence getQueryParams() {
    return query_params;
  }

  /**
   * Sets the value of the 'query_params' field.
   * @param value the value to set.
   */
  public void setQueryParams(java.lang.CharSequence value) {
    this.query_params = value;
  }

  /**
   * Gets the value of the 'path_params' field.
   * @return The value of the 'path_params' field.
   */
  public java.lang.CharSequence getPathParams() {
    return path_params;
  }

  /**
   * Sets the value of the 'path_params' field.
   * @param value the value to set.
   */
  public void setPathParams(java.lang.CharSequence value) {
    this.path_params = value;
  }

  /**
   * Gets the value of the 'form_params' field.
   * @return The value of the 'form_params' field.
   */
  public java.lang.CharSequence getFormParams() {
    return form_params;
  }

  /**
   * Sets the value of the 'form_params' field.
   * @param value the value to set.
   */
  public void setFormParams(java.lang.CharSequence value) {
    this.form_params = value;
  }

  /**
   * Gets the value of the 'request_body' field.
   * @return The value of the 'request_body' field.
   */
  public java.lang.CharSequence getRequestBody() {
    return request_body;
  }

  /**
   * Sets the value of the 'request_body' field.
   * @param value the value to set.
   */
  public void setRequestBody(java.lang.CharSequence value) {
    this.request_body = value;
  }

  /**
   * Creates a new RequestInfo RecordBuilder.
   * @return A new RequestInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder newBuilder() {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder();
  }

  /**
   * Creates a new RequestInfo RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new RequestInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder newBuilder(com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder other) {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder(other);
  }

  /**
   * Creates a new RequestInfo RecordBuilder by copying an existing RequestInfo instance.
   * @param other The existing instance to copy.
   * @return A new RequestInfo RecordBuilder
   */
  public static com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder newBuilder(com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo other) {
    return new com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder(other);
  }

  /**
   * RecordBuilder for RequestInfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<RequestInfo>
    implements org.apache.avro.data.RecordBuilder<RequestInfo> {

    private java.lang.CharSequence api_name;
    private java.lang.CharSequence request_path;
    private java.lang.CharSequence request_method;
    private java.lang.CharSequence query_params;
    private java.lang.CharSequence path_params;
    private java.lang.CharSequence form_params;
    private java.lang.CharSequence request_body;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.api_name)) {
        this.api_name = data().deepCopy(fields()[0].schema(), other.api_name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.request_path)) {
        this.request_path = data().deepCopy(fields()[1].schema(), other.request_path);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.request_method)) {
        this.request_method = data().deepCopy(fields()[2].schema(), other.request_method);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.query_params)) {
        this.query_params = data().deepCopy(fields()[3].schema(), other.query_params);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.path_params)) {
        this.path_params = data().deepCopy(fields()[4].schema(), other.path_params);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.form_params)) {
        this.form_params = data().deepCopy(fields()[5].schema(), other.form_params);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.request_body)) {
        this.request_body = data().deepCopy(fields()[6].schema(), other.request_body);
        fieldSetFlags()[6] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing RequestInfo instance
     * @param other The existing instance to copy.
     */
    private Builder(com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.api_name)) {
        this.api_name = data().deepCopy(fields()[0].schema(), other.api_name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.request_path)) {
        this.request_path = data().deepCopy(fields()[1].schema(), other.request_path);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.request_method)) {
        this.request_method = data().deepCopy(fields()[2].schema(), other.request_method);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.query_params)) {
        this.query_params = data().deepCopy(fields()[3].schema(), other.query_params);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.path_params)) {
        this.path_params = data().deepCopy(fields()[4].schema(), other.path_params);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.form_params)) {
        this.form_params = data().deepCopy(fields()[5].schema(), other.form_params);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.request_body)) {
        this.request_body = data().deepCopy(fields()[6].schema(), other.request_body);
        fieldSetFlags()[6] = true;
      }
    }

    /**
      * Gets the value of the 'api_name' field.
      * @return The value.
      */
    public java.lang.CharSequence getApiName() {
      return api_name;
    }

    /**
      * Sets the value of the 'api_name' field.
      * @param value The value of 'api_name'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setApiName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.api_name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'api_name' field has been set.
      * @return True if the 'api_name' field has been set, false otherwise.
      */
    public boolean hasApiName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'api_name' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearApiName() {
      api_name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'request_path' field.
      * @return The value.
      */
    public java.lang.CharSequence getRequestPath() {
      return request_path;
    }

    /**
      * Sets the value of the 'request_path' field.
      * @param value The value of 'request_path'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setRequestPath(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.request_path = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'request_path' field has been set.
      * @return True if the 'request_path' field has been set, false otherwise.
      */
    public boolean hasRequestPath() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'request_path' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearRequestPath() {
      request_path = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'request_method' field.
      * @return The value.
      */
    public java.lang.CharSequence getRequestMethod() {
      return request_method;
    }

    /**
      * Sets the value of the 'request_method' field.
      * @param value The value of 'request_method'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setRequestMethod(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.request_method = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'request_method' field has been set.
      * @return True if the 'request_method' field has been set, false otherwise.
      */
    public boolean hasRequestMethod() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'request_method' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearRequestMethod() {
      request_method = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'query_params' field.
      * @return The value.
      */
    public java.lang.CharSequence getQueryParams() {
      return query_params;
    }

    /**
      * Sets the value of the 'query_params' field.
      * @param value The value of 'query_params'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setQueryParams(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.query_params = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'query_params' field has been set.
      * @return True if the 'query_params' field has been set, false otherwise.
      */
    public boolean hasQueryParams() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'query_params' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearQueryParams() {
      query_params = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'path_params' field.
      * @return The value.
      */
    public java.lang.CharSequence getPathParams() {
      return path_params;
    }

    /**
      * Sets the value of the 'path_params' field.
      * @param value The value of 'path_params'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setPathParams(java.lang.CharSequence value) {
      validate(fields()[4], value);
      this.path_params = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'path_params' field has been set.
      * @return True if the 'path_params' field has been set, false otherwise.
      */
    public boolean hasPathParams() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'path_params' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearPathParams() {
      path_params = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'form_params' field.
      * @return The value.
      */
    public java.lang.CharSequence getFormParams() {
      return form_params;
    }

    /**
      * Sets the value of the 'form_params' field.
      * @param value The value of 'form_params'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setFormParams(java.lang.CharSequence value) {
      validate(fields()[5], value);
      this.form_params = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'form_params' field has been set.
      * @return True if the 'form_params' field has been set, false otherwise.
      */
    public boolean hasFormParams() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'form_params' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearFormParams() {
      form_params = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'request_body' field.
      * @return The value.
      */
    public java.lang.CharSequence getRequestBody() {
      return request_body;
    }

    /**
      * Sets the value of the 'request_body' field.
      * @param value The value of 'request_body'.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder setRequestBody(java.lang.CharSequence value) {
      validate(fields()[6], value);
      this.request_body = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'request_body' field has been set.
      * @return True if the 'request_body' field has been set, false otherwise.
      */
    public boolean hasRequestBody() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'request_body' field.
      * @return This builder.
      */
    public com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo.Builder clearRequestBody() {
      request_body = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public RequestInfo build() {
      try {
        RequestInfo record = new RequestInfo();
        record.api_name = fieldSetFlags()[0] ? this.api_name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.request_path = fieldSetFlags()[1] ? this.request_path : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.request_method = fieldSetFlags()[2] ? this.request_method : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.query_params = fieldSetFlags()[3] ? this.query_params : (java.lang.CharSequence) defaultValue(fields()[3]);
        record.path_params = fieldSetFlags()[4] ? this.path_params : (java.lang.CharSequence) defaultValue(fields()[4]);
        record.form_params = fieldSetFlags()[5] ? this.form_params : (java.lang.CharSequence) defaultValue(fields()[5]);
        record.request_body = fieldSetFlags()[6] ? this.request_body : (java.lang.CharSequence) defaultValue(fields()[6]);
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