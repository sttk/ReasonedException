/*
 * CreationHandler class.
 * Copyright (C) 2021 Takayuki Sato All Rights Reserved.
 *
 */
package sttk.reasonederror;

import java.time.OffsetDateTime;

/**
 * This class is an {@link ReasonedException} creation handler.<br>
 */
@FunctionalInterface
public interface CreationHandler {

  /**
   * Handles an {@link ReasonedException} object which will be created right
   * after.
   *
   * @param exc  A created {@link ReasonedException} object.
   * @param dttm  A {@link OffsetDateTime} object holding date and time
   *   that <i>exc</i> is created.
   */
  void handle(ReasonedException exc, OffsetDateTime dttm);
}
