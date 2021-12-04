/*
 * CreationNotifier class.
 * Copyright (C) 2021 Takayuki Sato All Rights Reserved.
 */
package sttk.reasonederror.notify;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.time.OffsetDateTime;

import sttk.reasonederror.CreationHandler;
import sttk.reasonederror.ReasonedException;

/**
 * This class notifies to {@link CreationHandler} that a
 * {@link ReasonedException} will be thrown.
 * This class manages a list for exception handlers processed synchronously
 * and another for handlers processed asynchronously.
 *
 * The only one instance of this class is created by
 * {@link ExceptionConfiguration}.
 * Via this configuration class, {@link CreationHandler}s are registered to
 * the instance of this class.
 * Then by calling {@link #fix} method, this configuration is made
 * unconfigurable and possible notifications.
 *
 * This configuration should be done in an application startup process.
 */
public final class CreationNotifier {

  /** The flag meaning whether this object is fixed or not. */
  private boolean isFixed = false;

  /**
   * The list which holds {@link CreationHandler} objects which is executed
   * synchronously.
   */
  protected final List<CreationHandler> syncHandlers = new LinkedList<>();

  /**
   * The list which holds {@link CreationHandler} objects which is executed
   * asynchronously.
   */
  protected final List<CreationHandler> asyncHandlers = new LinkedList<>();


  /**
   * Constructs an instance of this clsss with no argument.
   */
  public CreationNotifier() {
  }


  /**
   * Checks whether this object is fixed or not.
   */
  public boolean isFixed() {
    return this.isFixed;
  }


  /**
   * Registers an exception handler processed in synchronously.
   * After calling {@link #fix} method, this method registers no more.
   *
   * @param handler  An exception handler.
   */
  public synchronized void addSyncHandler(final CreationHandler handler) {
    if (this.isFixed) {
      return;
    }
    this.syncHandlers.add(handler);
  }


  /**
   * Registers an exception handlers processed in asynchronously.
   * After calling {@link #fix} method, this method registers no more.
   *
   * @param handler  An exception handler.
   */
  public synchronized void addAsyncHandler(final CreationHandler handler) {
    if (this.isFixed) {
      return;
    }
    this.asyncHandlers.add(handler);
  }


  /**
   * Fixes this object.
   * This method makes it impossible to add more exception handlers to this
   * object, and possible to notify that a {@link ReasonedException} is
   * created.
   */
  public synchronized void fix() {
    this.isFixed = true;
  }


  /**
   * Notifies that a {@link ReasonedException} is created.
   * However, this method does nothing until this object is fixed.
   *
   * @param re  A {@link ReasonedException} object.
   */
  public void notify(final ReasonedException re) {
    if (!this.isFixed) {
      return;
    }

    final var now = OffsetDateTime.now();

    for (var handler : this.syncHandlers) {
      handler.handle(re, now);
    }

    if (!this.asyncHandlers.isEmpty()) {
      final var handlers = this.asyncHandlers;
      new Thread(() -> {
        for (var handler : handlers) {
          try {
            handler.handle(re, now);
          } catch (Throwable e) {}
        }
      }).start();
    }
  }
}
