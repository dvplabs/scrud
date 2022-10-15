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
public class TypeName {
  private final String name;
  private final ConflictDetector conflictDetector;
  
  @EqualsAndHashCode.Exclude
  private final Pattern CLASS_NAME_PATTERN = Pattern.compile("^.+(?<!\\.[A-Z][A-Za-z]*)\\.(.+)$");
  @EqualsAndHashCode.Exclude
  private final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("(.+)\\.(?!\\.).+");

  public TypeName(String name, ConflictDetector conflictDetector) {
    this.name = name.replace('$', '.');
    this.conflictDetector = conflictDetector;
  }

  public TypeName(String name) {
    this(name, dep -> true);
  }
  
  public String getSimpleName() {
    var matcher = CLASS_NAME_PATTERN.matcher(name);
    matcher.find();
    return matcher.group(1);
  }
  
  public String getFullName() {
    return name;
  }

  public String getPackageName() {
    var matcher = PACKAGE_NAME_PATTERN.matcher(name);
    matcher.find();
    return matcher.group(1);
  }
  
  public String getName() {
    return conflictDetector.isNameClash(name) ? getFullName() : getSimpleName();
  }
  
}
