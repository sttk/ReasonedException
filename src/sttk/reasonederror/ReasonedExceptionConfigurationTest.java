package sttk.reasonederror;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;
import org.junit.BeforeClass;

import java.util.List;
import java.util.ArrayList;


public class ReasonedExceptionConfigurationTest {

  static final List<String> syncLogger = new ArrayList<>();
  static final List<String> asyncLogger = new ArrayList<>();

  static final ReasonedExceptionConfiguration config;
  static {
    config = new ReasonedExceptionConfiguration();
  }

  @BeforeClass
  public static void configure() {
    config.addSyncHandler((exc, dttm) -> {
      syncLogger.add("1." + exc.getReason());
    });
    config.addSyncHandler((exc, dttm) -> {
      syncLogger.add("2." + exc.getReason());
    });
    config.addAsyncHandler((exc, dttm) -> {
      asyncLogger.add("3." + exc.getReason());
    });
    config.addAsyncHandler((exc, dttm) -> {
      asyncLogger.add("4." + exc.getReason());
    });
    config.fix();
  }

  enum Error {
    FailToDoSomething,
    InvalidValue,
  }


  @Test
  public void should_notify_exceptions_are_created_and_thrown() {
    try {
      throw ReasonedException.by(Error.FailToDoSomething);
    } catch (ReasonedException e) {
      assertThat(e.getReason()).isEqualTo(Error.FailToDoSomething);

      try {
        Thread.sleep(100);
      } catch (Exception e2) {}

      assertThat(syncLogger).containsExactly(
        "1." + Error.FailToDoSomething,
        "2." + Error.FailToDoSomething);

      assertThat(asyncLogger).containsExactly(
        "3." + Error.FailToDoSomething,
        "4." + Error.FailToDoSomething);
    }
  }
}
