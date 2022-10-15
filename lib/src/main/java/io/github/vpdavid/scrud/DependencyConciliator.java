/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author david
 */
public class DependencyConciliator implements ConflictDetector {
  private static Pattern nameRegex = Pattern.compile("^.*\\.(?!\\.)([A-Z][a-zA-Z0-9]*)$");
  private Map<String, Map<String, Type>> dependencies = new HashMap<>();

  public void addDependencies(List<String> list) {
    for (var dep : list) {
      var name = findSimpleName(dep);
      
      if (dependencies.containsKey(name)) {
        var depObj = dependencies.get(name);
        if (!depObj.containsKey(dep)) {
          depObj.put(dep, Type.CLASH);
        }
      } else {
        var map = new HashMap<String, Type>();
        map.put(dep, Type.NO_CLASH);
        dependencies.put(name, map);
      }
    }
  }
  
  @Override
  public boolean isNameClash(String typeName) {
    var name = findSimpleName(typeName);
    return dependencies.containsKey(name) && dependencies.get(name).get(typeName) == Type.CLASH;
  }
  
  static String findSimpleName(String typeName) {
    var matcher = nameRegex.matcher(typeName);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Typename " + typeName + " is not valid");
    }
    return matcher.group(1);
  }
  
  public List<String> getNonClashing() {
    return dependencies.values().stream()
        .flatMap(map -> map.entrySet().stream())
        .filter(entry -> entry.getValue().equals(Type.NO_CLASH))
        .map(Entry::getKey)
        .collect(toList());
  }
  
  static enum Type {
    CLASH, NO_CLASH
  }
  
}
