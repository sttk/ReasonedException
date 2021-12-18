/*
 * ReasonedException class.
 * Copyright (C) 2021 Takayuki Sato All Rights Reserved.
 */
package sttk.reasonederror;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import sttk.reasonederror.notify.CreationNotifier;


/**
 * An exception class with reason.
 *
 * This class has an enum value which indicates a reason by which this
 * exception is caused.
 * This class can also have supplemental informations to help to know error
 * situation where this exception is caused.
 *
 * This class has no public constructor and the only one way to instantiate
 * this class is using static {@link #by} methods.
 *
 * The example code of creating and throwing this exception is as follows:
 * <pre>{@code
 *   enum Error {
 *     FailToDoSomething,
 *   }
 *
 *   throw ReasonedException
 *     .with("name", "value")  // set a situation parameter if required.
 *     .by(Error.FailToSomething); 
 * }</pre>
 *
 * If {@link CreationHandler} objects are registered with
 * {@link ReasonedExceptionConfiguration}, these handlers are notified that a
 * {@link ReasonedException} is created with {@link #by} method.
 */
public final class ReasonedException extends Exception {

  /** The serial version UID. */
  private static final long serialVersionUID = 6388732673631321326L;

  /** The reason by which this exception is caused. */
  private final Enum<?> reason;

  /** The map for error situation parameters. */
  private final Map<String, Object> situation;

  /** The stack trace for the location of occurrence. */
  private final StackTraceElement trace;


  /**
   * The constructor which takes a reason of this exception as an argument.
   * The access modifier of this constructor is protected, and the static
   * {@link #by} method uses this constructor to instantiate this class.
   *
   * @param reason  A reason of this exception.
   * @param map  A map which contains situation parameters.
   */
  protected ReasonedException(final Enum<?> reason,
      final Map<String, Object> map) {
    this.reason = reason;
    this.situation = map;

    var traces = getStackTrace();
    setStackTrace(Arrays.copyOfRange(traces, 1, traces.length));

    this.trace = traces[1];
  }


  /**
   * The constructor which takes a reason and a cause exception of this
   * exception as arguments.
   * The access modifier of this constructor is protected, and the static
   * {@link #by} method uses this constructor to instantiate this class.
   *
   * @param reason  A reason of this exception.
   * @param cause  A cause exception.
   * @param map  A map which contains situation parameters.
   */
  protected ReasonedException(final Enum<?> reason, final Throwable cause,
      final Map<String, Object> map) {
    super(cause);
    this.reason = reason;
    this.situation = map;

    var traces = getStackTrace();
    setStackTrace(Arrays.copyOfRange(traces, 1, traces.length));

    this.trace = traces[1];
  }


  /**
   * Gets the reason of this exception.
   *
   * @return  The reason of this exception.
   */
  public Enum<?> getReason() {
    return this.reason;
  }


  /**
   * Gets an unmodifiable map which contains error situation parameters.
   *
   * @return  A map of error situation parameters
   */
  public Map<String, Object> getSituation() {
    return this.situation;
  }


  /**
   * Gets a error situation parameter value by the specified name.
   *
   * @param <T> The type of the returned value.
   * @param name  A error situation parameter name.
   * @return  A error situation parameter value.
   */
  public <T> T getSituationValue(final String name) {
    @SuppressWarnings("unchecked")
    final T value = (T) this.situation.get(name);
    return value;
  }


  /**
   * Gets an error situation parameter value by the name of the specified enum
   * value.
   *
   * @param <T> The type of the returned value.
   * @param name  A enum value for an error situation parameter.
   * @return  An error situation parameter value.
   */
  public <T> T getSituationValue(final Enum<?> name) {
    return getSituationValue(name.name());
  }


  /**
   * Gets a message represents this exception.
   *
   * @return  A message.
   */
  public String getMessage() {
    final var buf = new StringBuilder()
      .append("reason=")
      .append(getReason().name());

    for (var entry : this.situation.entrySet()) {
      buf.append(", ")
         .append(entry.getKey() + "=" + entry.getValue());
    }

    if (getCause() != null) {
      buf.append(", ")
         .append("cause=" + getCause());
    }

    return buf.toString();
  }


  /**
   * Returns the fully qualified name of the class of this error occurrence.
   *
   * @return  The fully qualified name of the class of this error occurerce.
   */
  public String getClassName() {
    return trace.getClassName();
  }


  /**
   * Returns the name of the method of this error occurrence.
   *
   * @return  The name of the method of this error occurrence.
   */
  public String getMethodName() {
    return trace.getMethodName();
  }


  /**
   * Returns the name of the source file of this error occurrence.
   *
   * @return  The name of the source file of this error occurrence.
   */
  public String getFileName() {
    return trace.getFileName();
  }


  /**
   * Returns the line number of the source file of this error occurrence.
   *
   * @return  The line number of the source file of this error occurrence.
   */
  public int getLineNumber() {
    return trace.getLineNumber();
  }


  /**
   * Creates an instance of this class with the specified reason.
   *
   * If {@link CreationHandler} objects are registered with
   * {@link ReasonedExceptionConfiguration}, these handlers are notified that a
   * {@link ReasonedException} is created with this method.
   *
   * @param reason  A reason of this exception.
   * @return  A new {@link ReasonedException} object.
   */
  public static ReasonedException by(final Enum<?> reason) {
    var re = new ReasonedException(reason, Collections.emptyMap());
    ReasonedExceptionConfiguration.notifier.notify(re);
    return re;
  }


  /**
   * Creates an instance of this class with the specified reason and cause.
   *
   * If {@link CreationHandler} objects are registered with
   * {@link ReasonedExceptionConfiguration}, these handlers are notified that a
   * {@link ReasonedException} is created with this method.
   *
   * @param reason  A reason of this exception.
   * @param cause  A cause exception.
   * @return  A new {@link ReasonedException} object.
   */
  public static ReasonedException by(final Enum<?> reason,
      final Throwable cause) {
    var re = new ReasonedException(reason, cause, Collections.emptyMap());
    ReasonedExceptionConfiguration.notifier.notify(re);
    return re;
  }


  /**
   * Creates a builder for this class and set an error situation parameter.
   *
   * @param name  An error situation parameter name.
   * @param value  An error situation parameter value.
   * @return  A {@link Builder} object.
   */
  public static Builder with(final String name, final Object value) {
    return new Builder().with(name, value);
  }


  /**
   * Creates a builder for this class and set an error situation parameter.
   *
   * @param name  An error situation parameter name.
   * @param value  An error situation parameter value.
   * @return  A {@link Builder} object.
   */
  public static Builder with(final Enum<?> name, final Object value) {
    return new Builder().with(name, value);
  }


  /**
   * The builder class of {@link ReasonedException}.<br>
   */
  public static class Builder {

    /** A map to store situation parameters. */
    private Map<String, Object> situation = new LinkedHashMap<>();

    /**
     * Creates a new instance of this class with no arguments.
     */
    protected Builder() {
    }


    /**
     * Sets a pair of name and value for an error situation parameter.
     *
     * @param name  A error situation parameter name.
     * @param value  A error situation parameter value.
     * @return  This builder instance.
     */
    public Builder with(final String name, final Object value) {
      this.situation.put(name, value);
      return this;
    }


    /**
     * Sets a pair of enum value and value for an error situation parameter.
     *
     * @param name  A error situation parameter enum value.
     * @param value  A error situation parameter value.
     * @return  This builder instance.
     */
    public Builder with(final Enum<?> name, final Object value) {
      this.situation.put(name.name(), value);
      return this;
    }


    /**
     * Builds a {@link ReasonedException} instance.
     *
     * If {@link CreationHandler} objects are registered with
     * {@link ReasonedExceptionConfiguration}, these handlers are notified that
     * a {@link ReasonedException} is created with this method.
     *
     * @param reason  A reason of this exception.
     * @return  A new {@link ReasonedException} object.
     */
    public ReasonedException by(final Enum<?> reason) {
      final var map =  Collections.unmodifiableMap(this.situation);
      final var re = new ReasonedException(reason, map);
      this.situation = null;
      ReasonedExceptionConfiguration.notifier.notify(re);
      return re;
    }


    /**
     * Builds a {@link ReasonedException} instance with a cause exception.
     *
     * If {@link CreationHandler} objects are registered with
     * {@link ReasonedExceptionConfiguration}, these handlers are notified that
     * a {@link ReasonedException} is created with this method.
     *
     * @param reason  A reason of this exception.
     * @param cause  A cause exception.
     * @return  A new {@link ReasonedException} object.
     */
    public ReasonedException by(final Enum<?> reason, final Throwable cause) {
      final var map =  Collections.unmodifiableMap(this.situation);
      final var re = new ReasonedException(reason, cause, map);
      this.situation = null;
      ReasonedExceptionConfiguration.notifier.notify(re);
      return re;
    }
  }
}
