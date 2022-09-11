/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author david
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class TypeName {
  private final String name;
  @EqualsAndHashCode.Exclude
  private final Pattern CLASS_NAME_PATTERN = Pattern.compile(".+\\.(?!\\.)(.+)");
  @EqualsAndHashCode.Exclude
  private final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("(.+)\\.(?!\\.).+");
  
  public String getSimpleName() {
    var matcher = CLASS_NAME_PATTERN.matcher(name);
    matcher.find();
    return matcher.group(1).replace("$", ".");
  }
  
  public String getFullName() {
    return name;
  }

  public String getPackageName() {
    var matcher = PACKAGE_NAME_PATTERN.matcher(name);
    matcher.find();
    return matcher.group(1);
  }
  
}
