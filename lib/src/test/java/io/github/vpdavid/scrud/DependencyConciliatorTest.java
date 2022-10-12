/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

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
    assertFalse(dc.nameClash("a.b.c.D"));
    assertTrue(dc.nameClash("h.i.D"));
    assertFalse(dc.nameClash("e.f.G"));
    
    dc.addDependencies(List.of(
        "j.k.l.G",
        "m.n.O",
        "p.q.r.G"));
    assertTrue(dc.nameClash("j.k.l.G"));
    assertFalse(dc.nameClash("e.f.G"));
    assertFalse(dc.nameClash("m.n.O"));
    assertTrue(dc.nameClash("p.q.r.G"));
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
