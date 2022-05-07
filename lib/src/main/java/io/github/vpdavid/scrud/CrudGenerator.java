/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author david
 */
@SupportedAnnotationTypes("io.github.vpdavid.scrud.Crud")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CrudGenerator extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
    throw new UnsupportedOperationException("Not supported yet."); 
  }
  
}
