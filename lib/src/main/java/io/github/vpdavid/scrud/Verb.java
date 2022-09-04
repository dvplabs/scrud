package io.github.vpdavid.scrud;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author david
 */
@RequiredArgsConstructor
public class Verb {
//
//  private static final Verb GET_ALL = new Verb("get-all.tpl");
//  private static final Verb GET = new Verb("get.tpl");
//  private static final Verb POST = new Verb("post.tpl");
//  private static final Verb PUT = new Verb("put.tpl");
//  private static final Verb DELETE = new Verb("delete.tpl");
  
  private final Configuration conf;
  private final String templateName;
  
  public String generateSourceCode(Method method) 
      throws MalformedTemplateNameException, ParseException, IOException, TemplateException {
    var template = conf.getTemplate(templateName);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    template.process(Map.of("method", method), new PrintWriter(out));
    return out.toString();
  }
  
//  public abstract boolean validFor(Method method);
}
