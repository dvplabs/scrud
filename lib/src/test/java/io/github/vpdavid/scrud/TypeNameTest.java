/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author david
 */
public class TypeNameTest {
  
  @Test
  void getSimpleName() {
    assertEquals("Data", new TypeName("com.data.Data").getSimpleName());
    assertEquals("Info.Data", new TypeName("com.data.Info$Data").getSimpleName());
  }

  @Test
  void getFullName() {
    assertEquals("com.data.Info", new TypeName("com.data.Info").getFullName());
    assertEquals("com.data.Info.Data", new TypeName("com.data.Info.Data").getFullName());
  }
  
  @Test
  void getPackageName() {
    assertEquals("com.data", new TypeName("com.data.Info").getPackageName());
  }
  
  @Test
  void getClashingName() {
    assertEquals("com.data.Type", new TypeName("com.data.Type", dep -> true).getName());
  }
  
  @Test
  void getNonClashingName() {
    assertEquals("Type", new TypeName("com.data.Type", dep -> false).getName());
  }
}
