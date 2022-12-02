package io.github.dvplabs.scrud;

/**
 *
 * @author david
 */
public interface ConflictDetector {
  
  boolean isNameClash(String typeName);
}
