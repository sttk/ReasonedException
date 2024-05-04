package com.github.sttk.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.NotSerializableException;
import java.io.InvalidObjectException;

public class ReasonedExceptionTest {
  private ReasonedExceptionTest() {}

  record IndexOutOfRange(int index, int min, int max) {}
  record SerializableReason(int index, int min, int max)
    implements Serializable {}

  @Test
  void testConstructor_reason() {
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
    var reason = IndexOutOfRange.class.cast(exc.getReason());
    assertThat(reason.index()).isEqualTo(4);
    assertThat(reason.min()).isEqualTo(0);
    assertThat(reason.max()).isEqualTo(3);
    assertThat(exc.getCause()).isNull();

    //exc.printStackTrace();
  }

  @Test
  void testConstructor_reason_reasonIsNull() {
    try {
      new ReasonedException(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("reason");
    }
  }

  @Test
  void testConstructor_reasonAndCause() {
    var cause = new IndexOutOfBoundsException(4);
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3), cause);
    var reason = IndexOutOfRange.class.cast(exc.getReason());
    assertThat(reason.index()).isEqualTo(4);
    assertThat(reason.min()).isEqualTo(0);
    assertThat(reason.max()).isEqualTo(3);
    assertThat(exc.getCause()).isEqualTo(cause);

    //exc.printStackTrace();
  }

  @Test
  void testConstructor_reasonAndCause_reasonIsNull() {
    try {
      var cause = new IndexOutOfBoundsException(4);
      new ReasonedException(null, cause);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("reason");
    }
  }

  @Test
  void testGetMessage_reason() {
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
    assertThat(exc.getMessage()).isEqualTo(
      "IndexOutOfRange{index=4, min=0, max=3}"
    );
  }

  @Test
  void testGetMessage_reasonAndCause() {
    var cause = new IndexOutOfBoundsException(4);
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3), cause);
    assertThat(exc.getMessage()).isEqualTo(
      "IndexOutOfRange{index=4, min=0, max=3, cause=" +
      "java.lang.IndexOutOfBoundsException: Index out of range: 4}"
    );
  }

  @Test
  void testThrow_identifyReasonWithInstanceOf() {
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
    if (exc.getReason() instanceof IndexOutOfRange reason) {
      assertThat(reason.index()).isEqualTo(4);
      assertThat(reason.min()).isEqualTo(0);
      assertThat(reason.max()).isEqualTo(3);
    }
  }

  @Test
  void testThrow_identifyReasonWithSwitchExpression() {
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
    switch (exc.getReason()) {
      case IndexOutOfRange reason -> {
        assertThat(reason.index()).isEqualTo(4);
        assertThat(reason.min()).isEqualTo(0);
        assertThat(reason.max()).isEqualTo(3);
      }
      default -> fail();
    }
  }

  @Test
  void testToRuntimeException() {
    try {
      var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
      throw exc.toRuntimeException();
    } catch (RuntimeReasonedException e) {
      var re = e.toReasonedException();
      switch (re.getReason()) {
        case IndexOutOfRange reason -> {
          assertThat(reason.index()).isEqualTo(4);
          assertThat(reason.min()).isEqualTo(0);
          assertThat(reason.max()).isEqualTo(3);
        }
        default -> fail();
      }
    }
  }

  @Test
  void testSerialize_reasonIsSerializable_andNoCause() throws Exception {
    var bos = new ByteArrayOutputStream();
    var oos = new ObjectOutputStream(bos);
    try (oos) {
      var exc = new ReasonedException(new SerializableReason(4, 0, 3));
      oos.writeObject(exc);
    }

    var bytes = bos.toByteArray();
    var ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
    try (ois) {
      var obj = ois.readObject();
      assertThat(obj).isInstanceOf(ReasonedException.class);

      var exc = ReasonedException.class.cast(obj);
      var cause = exc.getCause();
      assertThat(cause).isNull();

      var robj = exc.getReason();
      assertThat(robj).isInstanceOf(SerializableReason.class);
      var reason = SerializableReason.class.cast(robj);
      assertThat(reason.index()).isEqualTo(4);
      assertThat(reason.min()).isEqualTo(0);
      assertThat(reason.max()).isEqualTo(3);
    }
  }

  @Test
  void testSerialize_reasonIsSerializable_andHasCause() throws Exception {
    var bos = new ByteArrayOutputStream();
    var oos = new ObjectOutputStream(bos);
    try (oos) {
      var cause = new IndexOutOfBoundsException(4);
      var exc = new ReasonedException(new SerializableReason(4, 0, 3), cause);
      oos.writeObject(exc);
    }

    var bytes = bos.toByteArray();
    var ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
    try (ois) {
      var obj = ois.readObject();
      assertThat(obj).isInstanceOf(ReasonedException.class);

      var exc = ReasonedException.class.cast(obj);
      var cause = exc.getCause();
      assertThat(cause).isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(cause.getMessage()).isEqualTo("Index out of range: 4");

      var robj = exc.getReason();
      assertThat(robj).isInstanceOf(SerializableReason.class);
      var reason = SerializableReason.class.cast(robj);
      assertThat(reason.index()).isEqualTo(4);
      assertThat(reason.min()).isEqualTo(0);
      assertThat(reason.max()).isEqualTo(3);
    }
  }

  @Test
  void testSerialize_reasonIsNotSerializable() throws Exception {
    var bos = new ByteArrayOutputStream();
    var oos = new ObjectOutputStream(bos);
    try (oos) {
      var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));
      oos.writeObject(exc);
      fail();
    } catch (NotSerializableException e) {
      assertThat(e.getMessage()).isEqualTo(IndexOutOfRange.class.getName());
    }
  }
}
