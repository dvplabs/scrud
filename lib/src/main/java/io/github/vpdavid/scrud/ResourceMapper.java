/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

public interface ResourceMapper<M, D> {
  
  D toDto(M model);
    
  void updateEntity(M model, D dto);
  
}
