/*
 * ReasonedExceptionConfiguration class.
 * Copyright (C) 2021 Takayuki Sato All Rights Reserved.
 */
package sttk.reasonederror;

import sttk.reasonederror.notify.CreationNotifier;

/**
 * This is a cofiguration class which configures behaviors of
 * {@link ReasonedException}.
 * This class can register {@link CreationHandler}s which execute some
 * processes synchronously or asynchronously when a {@link ReasonedException}
 * is created.
 */
public final class ReasonedExceptionConfiguration {

  /**
   * The {@link CreationNotifier} object which manages
   * {@link CreationHandler}s.
   */
  protected static final CreationNotifier notifier = new CreationNotifier();


  /**
   * Constructs an instance of this clsss with no argument.
   */
  public ReasonedExceptionConfiguration() {
  }


  /**
   * Adds a {@link CreationHandler} object which is executed synchronously
   * just after  a {@link ReasonedException} is created.
   * Handlers added with this method are executed in the order of addition
   * and stop if one of the handlers throws a {@link RuntimeException} or
   * {@link Error}.
   *
   * @param handler  A {@link CreationHandler} object.
   */
  public void addSyncHandler(final CreationHandler handler) {
    notifier.addSyncHandler(handler);
  }


  /**
   * Adds a {@link CreationHandler} object which is executed asynchronously
   * just after a {@link ReasonedException} is created.
   * Handlers does not stop even if one of the handlers throws a
   * {@link RuntimeException} or {@link Error}.
   *
   * @param handler  A {@link CreationHandler} object.
   */
  public void addAsyncHandler(final CreationHandler handler) {
    notifier.addAsyncHandler(handler);
  }


  /**
   * Fixes this configuration.
   * After this method is called, all configurations for
   * {@link ReasonedException} are disabled to change,
   * and enable to notify that a {@link ReasonedException} is created.
   */
  public void fix() {
    notifier.fix();
  }
}
