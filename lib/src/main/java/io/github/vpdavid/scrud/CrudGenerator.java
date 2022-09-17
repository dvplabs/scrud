package io.github.vpdavid.scrud;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.format;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.mapping;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
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
  private final String PACKAGE_DECLARATION = "package %s;\n";
  private final String COMMOM_IMPORTS
      = "import java.util.*;\n"
      + "import java.util.stream.*;\n"
      + "import javax.persistence.*;\n"
      + "import javax.persistence.criteria.Order;\n"
      + "import org.springframework.beans.factory.annotation.Autowired;\n"
      + "import org.springframework.data.domain.*;\n"
      + "import org.springframework.http.HttpStatus;\n"
      + "import org.springframework.transaction.annotation.Transactional;\n"
      + "import org.springframework.web.bind.annotation.*;\n";
  private final String INPUT_IMPORTS
      = "import %s;\n"
      + "import %s;\n";
  private final String CLASS_DECLARATION
      = "@RestController\n"
      + "@RequestMapping(path = \"%s\")\n"
      + "public class %sCrudController {\n"
      + "  @Autowired\n"
      + "  private EntityManager entityManager;\n"
      + "  @Autowired\n"
      + "  private %s mapper;\n";

  Map<Verb, VerbProcessor> processors = new HashMap<>();

  public CrudGenerator() throws IOException {
    super();
    var freeMarkerCfg = configFreemarker();
    processors.put(Verb.GET, new VerbProcessor(Verb.GET, freeMarkerCfg, "get.tpl"));
    processors.put(Verb.GET_ALL, new VerbProcessor(Verb.GET_ALL, freeMarkerCfg, "get-all.tpl"));
    processors.put(Verb.POST, new VerbProcessor(Verb.POST, freeMarkerCfg, "post.tpl"));
    processors.put(Verb.PUT, new VerbProcessor(Verb.PUT, freeMarkerCfg, "put.tpl"));
    processors.put(Verb.DELETE, new VerbProcessor(Verb.DELETE, freeMarkerCfg, "delete.tpl"));
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
    try {
      var elements = set.stream()
          .flatMap(e -> re.getElementsAnnotatedWith(e).stream())
          .collect(toList());

      for (var el : elements) {
        var val = getAnnotationValues(el);
        var methods = findAvailableMethods(el, val);
        writeToFile(new TypeName(((TypeElement) el).getQualifiedName().toString()), val, methods);
      }
      return true;
    }
    catch (Exception ex) {
      var problem = new StringWriter();
      ex.printStackTrace(new PrintWriter(problem));
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, problem.toString());
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
    values.verbProcessors = ((List<?>) defaults.get("verbs").getValue()).stream()
        .map(Object::toString)
        .map(ss -> Verb.valueOf(new TypeName(ss).getSimpleName()))
        .map(processors::get)
        .collect(toList());
    values.model = new TypeName(defaults.get("model").getValue().toString());
    values.dto = new TypeName(defaults.get("dto").getValue().toString());
    return values;
  }
  
  private Map<VerbProcessor, List<Method>> findAvailableMethods(Element el, AnnotationValues val) {
    return el.getEnclosedElements().stream()
            .filter(element -> element.getKind() == ElementKind.METHOD)
            .map(methodDef -> new Method(methodDef, val.model.getFullName(), val.dto.getFullName()))
            .flatMap(method -> val.verbProcessors.stream().map(verb -> new Pair(method, verb)))
            .filter(pair -> pair.processor.getVerb().validFor(pair.method))
            .collect(groupingBy(Pair::getProcessor,
                mapping(Pair::getMethod, toList())));
  }

  private void writeToFile(
      TypeName clazz,
      AnnotationValues val,
      Map<VerbProcessor, List<Method>> methods) throws IOException, TemplateException {
    String className = findResourceName(val.resource);
    var file = processingEnv.getFiler().createSourceFile(
        clazz.getPackageName() + format(".%sCrudController", className));
    
    var verbs = val.verbProcessors.stream()
        .map(processor -> new Pair(methods.get(processor).get(0), processor))
        .collect(toList());
    
    var extraImports = verbs.stream()
          .flatMap(p -> p.method.getNonModelAndDtoParams().stream())
          .distinct()
          .sorted()
          .map(i -> "import " + i + ";")
          .collect(joining("\n"));

    try ( var writer = new PrintWriter(file.openWriter())) {
      writer.println(format(PACKAGE_DECLARATION, clazz.getPackageName()));
      writer.println(COMMOM_IMPORTS);
      writer.println(format(INPUT_IMPORTS, val.model.getFullName(), val.dto.getFullName()));
      writer.println(extraImports);
      writer.println("");
      writer.println(format(CLASS_DECLARATION, val.resource, className, clazz.getSimpleName()));
      
      for (var v : verbs) {
        var src = v.processor.generateSourceCode(v.method);
        writer.println(src);
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
    VerbProcessor processor;
  }

  static class AnnotationValues {

    String resource;
    List<VerbProcessor> verbProcessors;
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
