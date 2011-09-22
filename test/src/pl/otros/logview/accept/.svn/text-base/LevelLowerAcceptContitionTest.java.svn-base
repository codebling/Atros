package pl.otros.logview.accept;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.Test;

import pl.otros.logview.LogData;

public class LevelLowerAcceptContitionTest {

  @Test
  public void testAccept() {
    // given
    LogData ld = new LogData();
    ld.setLevel(Level.INFO);

    // when
    // then
    assertFalse(new LevelLowerAcceptContition(Level.FINEST).accept(ld));
    assertFalse(new LevelLowerAcceptContition(Level.FINER).accept(ld));
    assertFalse(new LevelLowerAcceptContition(Level.FINE).accept(ld));
    assertFalse(new LevelLowerAcceptContition(Level.CONFIG).accept(ld));
    assertFalse(new LevelLowerAcceptContition(Level.INFO).accept(ld));
    assertTrue(new LevelLowerAcceptContition(Level.WARNING).accept(ld));
    assertTrue(new LevelLowerAcceptContition(Level.SEVERE).accept(ld));
  }

}
