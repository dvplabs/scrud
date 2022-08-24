package io.github.vpdavid.scrud;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.mapping;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author david
 */
@SupportedAnnotationTypes("io.github.vpdavid.scrud.Crud")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class CrudGenerator extends AbstractProcessor {

  private final Pattern PATH_RESOURCE_NAME_PATTERN = Pattern.compile("^.*/([^/]+)$");
  private final String PACKAGE_DECLARATION = "package %s;\n\n";
  private final String COMMOM_IMPORTS
      = "import java.util.*;\n"
      + "import java.util.stream.*;\n"
      + "import javax.persistence.*;\n"
      + "import javax.persistence.criteria.Order;\n"
      + "import org.springframework.beans.factory.annotation.Autowired;\n"
      + "import org.springframework.data.domain.*;\n"
      + "import org.springframework.http.HttpStatus;\n"
      + "import org.springframework.transaction.annotation.Transactional;\n"
      + "import org.springframework.web.bind.annotation.*;\n\n";
  private final String INPUT_IMPORTS
      = "import %s;\n"
      + "import %s;\n\n";
  private final String CLASS_DECLARATION
      = "@RestController\n"
      + "@RequestMapping(path = \"%s\")"
      + "public class %sCrudController {"
      + "\t@Autowired\n\tprivate EntityManager entityManager;\n"
      + "\t@Autowired\n\tprivate BasicMapper mapper;\n\n";

  Configuration freeMarkerCfg;

  public CrudGenerator() throws IOException {
    super();
    freeMarkerCfg = configFreemarker();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
    try {
      var elements = set.stream()
          .flatMap(e -> re.getElementsAnnotatedWith(e).stream())
          .collect(toList());

      for (var el : elements) {
        var packageName = ((PackageElement) el.getEnclosingElement()).getQualifiedName().toString();
        var val = getAnnotationValues(el);

        var methods = el.getEnclosedElements().stream()
            .filter(element -> element.getKind() == ElementKind.METHOD)
            .map(method -> new Method(method, val.model, val.dto))
            .flatMap(method -> val.verbs.stream().map(verb -> new Pair(method, verb)))
            .filter(pair -> pair.getMethod().validFor(pair.getVerb()))
            .collect(groupingBy(Pair::getVerb,
                mapping(Pair::getMethod, toList())));

        writeToFile(packageName, val, methods);
      }
      return true;
    }
    catch (Exception ex) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
      return false;
    }
  }

  private AnnotationValues getAnnotationValues(Element el) {
    var mirror = el.getAnnotationMirrors().stream()
        .filter(a -> Crud.class.getName().equals(a.getAnnotationType().toString()))
        .findFirst()
        .orElseThrow();

    var defaults = AnnotationMirrors.getAnnotationValuesWithDefaults(mirror).entrySet()
        .stream()
        .collect(toMap(e -> e.getKey().getSimpleName().toString(), e -> e.getValue()));

    var values = new AnnotationValues();
    values.resource = (String) defaults.get("resource").getValue();
    values.verbs = ((List<?>) defaults.get("verbs").getValue()).stream()
        .map(Object::toString)
        .map(ss -> Verb.valueOf(new TypeName(ss).getName()))
        .collect(toList());
    values.model = new TypeName(defaults.get("model").getValue().toString());
    values.dto = new TypeName(defaults.get("dto").getValue().toString());
    return values;
  }

  private void writeToFile(
      String packageName,
      AnnotationValues val,
      Map<Verb, List<Method>> methods) throws IOException, TemplateException {
    String className = findResourceName(val.resource);
    var file = processingEnv.getFiler().createSourceFile(packageName + format(".%sCrudController", className));

    try (var writer = new PrintWriter(file.openWriter())) {
      writer.println(format(PACKAGE_DECLARATION, packageName));
      writer.println(COMMOM_IMPORTS);
      writer.println(format(INPUT_IMPORTS, val.model.getFullName(), val.dto.getFullName()));
      writer.println(format(CLASS_DECLARATION, val.resource, className));

      for (var v : val.verbs) {
        var src = v.generateSource(freeMarkerCfg, methods.get(v).get(0));
        writer.print("\n\n" + src);
      }

      writer.println("}");
    }
  }

  private String findResourceName(String resource) {
    var matcher = PATH_RESOURCE_NAME_PATTERN.matcher(resource);
    matcher.find();
    var resourceName = matcher.group(1);
    String className = resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1);
    return className;
  }

  @AllArgsConstructor
  @Getter
  static class Pair {

    Method method;
    Verb verb;
  }

  static class AnnotationValues {

    String resource;
    List<Verb> verbs;
    TypeName model;
    TypeName dto;
  }

  static Configuration configFreemarker() throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    cfg.setClassForTemplateLoading(CrudGenerator.class, "/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
    return cfg;
  }
}
