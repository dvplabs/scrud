package io.github.vpdavid.scrud;

/**
 *
 * @author david
 */
public @interface Crud {
  String resource();
  //Verb[] verbs() default {  };
  Class<?> model();
  Class<?> dto();
}
