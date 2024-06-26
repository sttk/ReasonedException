/*
 * ReasonedException class.
 * Copyright (C) 2024 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.exception;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.InvalidObjectException;

/**
 * Is the exception class that has a {@link Record} object indicating the
 * reason why this exception occurs.
 */
public final class ReasonedException extends Exception {

  /** The serial version ID. */
  private static final long serialVersionUID = 9030666566162337503L;

  /** The reason for this exception. */
  private transient Record reason;

  /**
   * Is the constructor which takes a {@link Record} object indicating the
   * reason for this excpetion.
   *
   * @param reason  A reason for this exception.
   */
  public ReasonedException(Record reason) {
    if (reason == null) {
      throw new NullPointerException("reason");
    }
    this.reason = reason;
  }

  /**
   * Is the constructor which takes a {@link Record} object indicating the
   * reason and {@link Throwable} object indicating the cause for this
   * excpetion.
   *
   * @param reason  A reason for this exception.
   * @param cause  A cause for this exception.
   */
  @SuppressWarnings("this-escape")
  public ReasonedException(Record reason, Throwable cause) {
    super(cause);

    if (reason == null) {
      throw new NullPointerException("reason");
    }
    this.reason = reason;
  }

  /**
   * Gets the reason for this exception.
   *
   * @return  The reason for this exception.
   */
  public Record getReason() {
    return this.reason;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    var c = this.reason.getClass().getSimpleName();
    var s = this.reason.toString();
    s = s.substring(c.length() + 1, s.length()-1);
    if (getCause() != null) {
      s += ", cause=" + this.getCause().toString();
    }
    return c + "{" + s + "}";
  }

  /**
   * Creats a {@link RuntimeReasonedException} object for methods that cannot
   * throw a {@link ReasonedException}.
   *
   * @return  A {@link RuntimeReasonedException} object.
   */
  public RuntimeReasonedException toRuntimeException() {
    return new RuntimeReasonedException(this);
  }

  /**
   * Writes a serial data of this exception to a stream.
   * Since the {@link Record} class is not serializable, this method will throw
   * a {@link NotSerializableException} if the {@code reason} field does not
   * inherit {@link Serializable} to make it serializable.
   *
   * @param out  An {@link ObjectOutputStream} to which data is written.
   * @throws IOException  if an I/O error occurs.
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    if (! (this.reason instanceof Serializable)) {
      throw new NotSerializableException(this.reason.getClass().getName());
    }
    out.defaultWriteObject();
    out.writeObject(this.reason);
  }

  /**
   * Reconstitutes the {@code ReasonedException} instance from a stream and
   * initialize the reason and cause properties when deserializing;
   * If the reason by deserialization is null, this method throws {@link
   * InvalidObjectException}.
   *
   * @param in  An {@link ObjectInputStream} from which data is read.
   * @throws IOException  if an I/O error occurs.
   * @throws ClassNotFoundException  if a serialized class cannot be loaded.
   */
  private void readObject(ObjectInputStream in)
    throws ClassNotFoundException, IOException
  {
    in.defaultReadObject();
    var reason = in.readObject();

    if (reason == null) {
      throw new InvalidObjectException("reason is null.");
    }
    this.reason = Record.class.cast(reason);
  }
}
