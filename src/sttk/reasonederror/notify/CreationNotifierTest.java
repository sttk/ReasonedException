package sttk.reasonederror.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.ArrayList;

import sttk.reasonederror.CreationHandler;
import sttk.reasonederror.ReasonedException;


public class CreationNotifierTest {

  @Test
  public void should_add_handlers_and_fix() {
    final var notifier = new CreationNotifier();
    assertThat(notifier.isFixed()).isFalse();
    assertThat(notifier.syncHandlers).isEmpty();
    assertThat(notifier.asyncHandlers).isEmpty();

    final CreationHandler h1 = (re, odt) -> {};
    notifier.addSyncHandler(h1);
    assertThat(notifier.isFixed()).isFalse();
    assertThat(notifier.syncHandlers).containsExactly(h1);
    assertThat(notifier.asyncHandlers).isEmpty();

    final CreationHandler h2 = (re, odt) -> {};
    notifier.addSyncHandler(h2);
    assertThat(notifier.isFixed()).isFalse();
    assertThat(notifier.syncHandlers).containsExactly(h1, h2);
    assertThat(notifier.asyncHandlers).isEmpty();

    final CreationHandler h3 = (re, odt) -> {};
    notifier.addAsyncHandler(h3);
    assertThat(notifier.isFixed()).isFalse();
    assertThat(notifier.syncHandlers).containsExactly(h1, h2);
    assertThat(notifier.asyncHandlers).containsExactly(h3);

    final CreationHandler h4 = (re, odt) -> {};
    notifier.addAsyncHandler(h4);
    assertThat(notifier.isFixed()).isFalse();
    assertThat(notifier.syncHandlers).containsExactly(h1, h2);
    assertThat(notifier.asyncHandlers).containsExactly(h3, h4);

    notifier.fix();

    final CreationHandler h5 = (re, odt) -> {};
    notifier.addSyncHandler(h5);
    assertThat(notifier.isFixed()).isTrue();
    assertThat(notifier.syncHandlers).containsExactly(h1, h2);
    assertThat(notifier.asyncHandlers).containsExactly(h3, h4);

    final CreationHandler h6 = (re, odt) -> {};
    notifier.addAsyncHandler(h6);
    assertThat(notifier.isFixed()).isTrue();
    assertThat(notifier.syncHandlers).containsExactly(h1, h2);
    assertThat(notifier.asyncHandlers).containsExactly(h3, h4);
  }

  enum Error {
    FailToDoSomething,
  }

  @RunWith(Enclosed.class)
  public static class Notify {
    @Test
    public void should_do_nothing_when_no_handlers() {
      final var notifier = new CreationNotifier();
      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
        } catch (Exception e) {
          fail(e.toString());
        }
      }
    }

    @Test
    public void should_execute_sync_handlers() {
      final var logs = new ArrayList<String>();

      final var notifier = new CreationNotifier();
      notifier.addSyncHandler((re, odt) -> {
        logs.add(re.getReason().name());
      });

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).isEmpty();

      notifier.fix();

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).containsOnly("FailToDoSomething");
    }

    @Test
    public void should_execute_async_handlers() {
      final var logs = new ArrayList<String>();

      final var notifier = new CreationNotifier();
      notifier.addAsyncHandler((re, odt) -> {
        logs.add(re.getReason().name());
      });

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).isEmpty();

      notifier.fix();

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).containsOnly("FailToDoSomething");
    }

    @Test
    public void should_execute_sync_and_asynchronouos_handlers() {
      final var logs = new ArrayList<String>();
      final var notifier = new CreationNotifier();
      notifier.addAsyncHandler((re, odt) -> {
        logs.add("Async: " + re.getReason());
      });
      notifier.addSyncHandler((re, odt) -> {
        logs.add("Sync: " + re.getReason());
      });

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).isEmpty();

      notifier.fix();

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).containsOnly(
        "Async: FailToDoSomething",
        "Sync: FailToDoSomething"
      );
    }

    @Test
    public void should_execute_all_async_handlers_even_if_one_of_the_handlers_failed() {
      final var logs = new ArrayList<String>();
      final var notifier = new CreationNotifier();
      notifier.addAsyncHandler((re, odt) -> {
        logs.add("Async: " + re.getReason() + " (1)");
      });
      notifier.addAsyncHandler((re, odt) -> {
        throw new RuntimeException();
      });
      notifier.addAsyncHandler((re, odt) -> {
        logs.add("Async: " + re.getReason() + " (3)");
      });

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).isEmpty();

      notifier.fix();

      try {
        throw ReasonedException.by(Error.FailToDoSomething);
      } catch (ReasonedException re) {
        try {
          notifier.notify(re);
          Thread.sleep(100);
        } catch (Exception e) {
          fail(e.toString());
        }
      }

      assertThat(logs).containsOnly(
        "Async: FailToDoSomething (1)",
        "Async: FailToDoSomething (3)"
      );
    }
  }
}
