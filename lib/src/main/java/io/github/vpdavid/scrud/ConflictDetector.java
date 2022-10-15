/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

/**
 *
 * @author david
 */
public interface ConflictDetector {
  
  boolean isNameClash(String typeName);
}
