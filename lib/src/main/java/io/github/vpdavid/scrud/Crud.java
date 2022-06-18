package io.github.vpdavid.scrud;

/**
 *
 * @author david
 */
public @interface Crud {
  String resource();
  Method[] methods() default {Method.POST, Method.PUT, Method.DELETE, Method.GET, Method.GET_ALL};
}
