package com.github.sttk.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

public class RuntimeReasonedExceptionTest {
  private RuntimeReasonedExceptionTest() {}

  record IndexOutOfRange(int index, int min, int max) {}

  @Test
  void testGetMessage() {
    var exc = new ReasonedException(new IndexOutOfRange(4, 0, 3));

    var rtexc = exc.toRuntimeException();

    assertThat(rtexc.getMessage()).isEqualTo(
      "com.github.sttk.exception.ReasonedException: " +
      "IndexOutOfRange{index=4, min=0, max=3}"
    );

    //rtexc.printStackTrace();
  }
}
