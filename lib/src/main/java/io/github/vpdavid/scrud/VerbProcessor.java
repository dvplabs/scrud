package io.github.vpdavid.scrud;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author david
 */
@RequiredArgsConstructor
public class VerbProcessor {
  
  @Getter
  private final Verb verb;
  private final Configuration conf;
  private final String templateName;
  
  public String generateSourceCode(Method method) 
      throws MalformedTemplateNameException, ParseException, IOException, TemplateException {
    var template = conf.getTemplate(templateName);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    template.process(Map.of("method", method), new PrintWriter(out));
    return out.toString();
  }
}
