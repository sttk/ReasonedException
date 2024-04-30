/*
 * RuntimeReasonedException class.
 * Copyright (C) 2024 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.exception;

/**
 * Is the exception class that has a {@link Record} object indicating the
 * reason why this exception occurs.
 */
public final class RuntimeReasonedException extends RuntimeException {

  /** The serial version ID. */
  private static final long serialVersionUID = -5330323943774792469L;

  /**
   * Is the constructor which takes a {@link ReasonedException} object.
   *
   * @param e  A {@link ReasonedException} object.
   */
  RuntimeReasonedException(ReasonedException e) {
    super(e);
  }

  /**
   * Gets the {@link ReasonedException} object that caused this exception.
   *
   * @return  The {@link ReasonedException} object that caused this exception.
   */
  public ReasonedException toReasonedException() {
    return ReasonedException.class.cast(getCause());
  }
}
