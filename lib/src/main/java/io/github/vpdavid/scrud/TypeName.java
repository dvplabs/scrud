/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author david
 */
@RequiredArgsConstructor
public class TypeName {
  private final String name;
  private Pattern p = Pattern.compile(".+\\.(?!\\.)(.+)");
  
  public String getName() {
    var matcher = p.matcher(name);
    matcher.find();
    return matcher.group(1).replace("$", ".");
  }
  
  public String getFullName() {
    return name;
  }
  
}
