package org.togetherjava.event.elevator;

import java.io.Serial;

public final class SimulationException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public SimulationException(String message) {
    super(message);
  }
}
