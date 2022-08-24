package io.github.vpdavid.scrud;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author david
 */
public enum Verb {
  GET_ALL("get-all.tpl"),
  GET("get.tpl"),
  POST("post.tpl"),
  PUT("put.tpl"), 
  DELETE("delete.tpl");
  
  private final String TEMPLATE_NAME;
  
  Verb(String templateName) {
    this.TEMPLATE_NAME = templateName;
  }
  
  String generateSource(Configuration conf, Method method) 
      throws MalformedTemplateNameException, ParseException, IOException, TemplateException {
    var template = conf.getTemplate(TEMPLATE_NAME);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    template.process(Map.of("method", method), new PrintWriter(out));
    return out.toString();
  }
}
