package io.github.vpdavid.scrud;

/**
 *
 * @author david
 */
public @interface Crud {
  String resource();
  Method[] methods() default {Method.GET_ALL, Method.GET, Method.POST, Method.PUT, Method.DELETE};
}
