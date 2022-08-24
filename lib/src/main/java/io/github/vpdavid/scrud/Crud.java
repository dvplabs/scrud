package io.github.vpdavid.scrud;

/**
 *
 * @author david
 */
public @interface Crud {
  String resource();
  Verb[] verbs() default { Verb.GET_ALL, Verb.GET, Verb.POST, Verb.PUT, Verb.DELETE };
  Class<?> model();
  Class<?> dto();
}
