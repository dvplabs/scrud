/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.beans.Introspector;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

/**
 *
 * @author david
 */
@SupportedAnnotationTypes("io.github.vpdavid.scrud.Crud")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CrudGenerator extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
    try {
//      var annotation = set.stream().findFirst().get();
//      Class clazz = Class.forName(annotation.getQualifiedName().toString());
      var elements = set.stream()
          .flatMap(e -> re.getElementsAnnotatedWith(e).stream())
          .collect(toList());
      for (var el : elements) {
        var packageName = ((PackageElement)el.getEnclosingElement()).getQualifiedName().toString();
        var crudAnnotation = el.getAnnotation(Crud.class);
//        String name = crudAnnotation.resource();
//        String className = name.substring(0, 1).toUpperCase() + name.substring(1) + "CrudController";
        var file = processingEnv.getFiler().createSourceFile("io.github.vpdavid.scrud.ProductsCrudController");
        var writer = new PrintWriter(file.openWriter());
        writer.println("package io.github.vpdavid.scrud;");
        writer.println("public class ProductsCrudController { }");
        writer.close();
      }
      return true;
    }
    catch (IOException ex) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
      return false;
    }
  }
  
}
