package sttk.reasonederror;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

import java.io.IOException;


public class ReasonedExceptionTest {

  enum Error {
    FailToDoSomething,
    InvalidState,
  }

  enum Param {
    name1,
    name2,
  }


  @Test
  public void should_create_and_throw_an_exception_with_a_reason() {
    try {
      throw ReasonedException.by(Error.FailToDoSomething);
    } catch (ReasonedException re) {
      assertThat(re.getReason()).isEqualTo(Error.FailToDoSomething);
      assertThat(re.getSituation()).isEmpty();
      assertThat(re.getCause()).isNull();
      assertThat(re.getMessage()).isEqualTo(
        "reason=FailToDoSomething");
    }
  }


  @Test
  public void should_create_and_throw_an_exception_with_a_reason_and_a_cause() {
    final var cause = new IOException("Message");
    try {
      throw ReasonedException.by(Error.FailToDoSomething, cause);
    } catch (ReasonedException re) {
      assertThat(re.getReason()).isEqualTo(Error.FailToDoSomething);
      assertThat(re.getSituation()).isEmpty();
      assertThat(re.getCause()).isEqualTo(cause);
      assertThat(re.getMessage()).isEqualTo(
        "reason=FailToDoSomething, " +
        "cause=java.io.IOException: Message");
    }
  }


  @Test
  public void should_create_and_throw_an_exception_with_a_reason_and_situation_parameters() {
    try {
      throw ReasonedException
        .with(Param.name1, "value1")
        .with("name2", 1234)
        .by(Error.FailToDoSomething);
    } catch (ReasonedException re) {
      assertThat(re.getReason()).isEqualTo(Error.FailToDoSomething);
      assertThat(re.getSituation()).hasSize(2);
      assertThat((String) re.getSituationValue("name1"))
        .isEqualTo("value1");
      assertThat((String) re.getSituationValue(Param.name1))
        .isEqualTo("value1");
      assertThat((Integer) re.getSituationValue("name2"))
        .isEqualTo(1234);
      assertThat((Integer) re.getSituationValue(Param.name2))
        .isEqualTo(1234);
      assertThat(re.getCause()).isNull();
      assertThat(re.getMessage()).isEqualTo(
        "reason=FailToDoSomething, " +
        "name1=value1, name2=1234");
    }
  }


  @Test
  public void should_create_and_throw_an_exception_with_a_reason_and_a_cause_and_situation_parameters() {
    final var cause = new IOException("Message");
    try {
      throw ReasonedException
        .with("name1", "value1")
        .with(Param.name2, 1234)
        .by(Error.FailToDoSomething, cause);
    } catch (ReasonedException re) {
      assertThat(re.getReason()).isEqualTo(Error.FailToDoSomething);
      assertThat(re.getSituation()).hasSize(2);
      assertThat((String) re.getSituationValue("name1"))
        .isEqualTo("value1");
      assertThat((String) re.getSituationValue(Param.name1))
        .isEqualTo("value1");
      assertThat((Integer) re.getSituationValue("name2"))
        .isEqualTo(1234);
      assertThat((Integer) re.getSituationValue(Param.name2))
        .isEqualTo(1234);
      assertThat(re.getCause()).isEqualTo(cause);
      assertThat(re.getMessage()).isEqualTo(
        "reason=FailToDoSomething, " +
        "name1=value1, name2=1234, " +
        "cause=java.io.IOException: Message");
    }
  }

  @Test
  public void should_print_message_when_cause_is_a_ReasonedException() {
    final var cause0 = new IOException("Message");
    try {
      throw ReasonedException
        .with("aaa", "bbb")
        .by(Error.InvalidState, cause0);
    } catch (ReasonedException cause1) {
      try {
        throw ReasonedException
          .with("name1", "value1")
          .with(Param.name2, 1234)
          .by(Error.FailToDoSomething, cause1);
      } catch (ReasonedException re) {
        assertThat(re.getReason()).isEqualTo(Error.FailToDoSomething);
        assertThat(re.getSituation()).hasSize(2);
        assertThat((String) re.getSituationValue("name1"))
          .isEqualTo("value1");
        assertThat((String) re.getSituationValue(Param.name1))
          .isEqualTo("value1");
        assertThat((Integer) re.getSituationValue("name2"))
          .isEqualTo(1234);
        assertThat((Integer) re.getSituationValue(Param.name2))
          .isEqualTo(1234);
        assertThat(re.getCause()).isEqualTo(cause1);
        assertThat(re.getMessage()).isEqualTo(
          "reason=FailToDoSomething, " +
          "name1=value1, name2=1234, " +
          "cause=sttk.reasonederror.ReasonedException: " +
          "reason=InvalidState, " +
          "aaa=bbb, " +
          "cause=java.io.IOException: Message");
      }
    }
  }
}
