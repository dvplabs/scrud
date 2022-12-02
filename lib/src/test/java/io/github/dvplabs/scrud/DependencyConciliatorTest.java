package io.github.dvplabs.scrud;

import io.github.dvplabs.scrud.DependencyConciliator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author david
 */
public class DependencyConciliatorTest {
  
  @Test
  void nameClash() {
    var dc = new DependencyConciliator();
    dc.addDependencies(List.of(
        "a.b.c.D",
        "e.f.G",
        "h.i.D"));
    assertFalse(dc.isNameClash("a.b.c.D"));
    assertTrue(dc.isNameClash("h.i.D"));
    assertFalse(dc.isNameClash("e.f.G"));
    
    dc.addDependencies(List.of(
        "j.k.l.G",
        "m.n.O",
        "p.q.r.G"));
    assertTrue(dc.isNameClash("j.k.l.G"));
    assertFalse(dc.isNameClash("e.f.G"));
    assertFalse(dc.isNameClash("m.n.O"));
    assertTrue(dc.isNameClash("p.q.r.G"));
  }
  
  @Test
  void nonClashingDependencies() {
    var dc = new DependencyConciliator();
    dc.addDependencies(List.of(
        "a.b.c.D",
        "e.f.G",
        "h.i.D"));
    assertEquals(List.of("a.b.c.D", "e.f.G"), dc.getNonClashing());
  }
  
  @Test
  void nonClashingWithRepeated() {
    var dc = new DependencyConciliator();
    dc.addDependencies(List.of(
        "a.b.C",
        "d.e.F",
        "a.b.C"));
    assertEquals(List.of("a.b.C", "d.e.F"), dc.getNonClashing());
  }
}
