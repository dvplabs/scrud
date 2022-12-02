package io.github.dvplabs.scrud;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.format;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
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
import java.util.stream.Stream;
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
@SupportedAnnotationTypes("io.github.dvplabs.scrud.Crud")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class CrudGenerator extends AbstractProcessor {

  private final Pattern ENUM_NAME_PATTERN = Pattern.compile("^.*\\.([^.]+)$");
  private final String PACKAGE_DECLARATION = "package %s;\n";
  private final List<String> BASIC_IMPORTS = List.of(
      "javax.annotation.processing.Generated",
      "org.springframework.web.bind.annotation.RestController",
      "org.springframework.web.bind.annotation.RequestMapping",
      "org.springframework.beans.factory.annotation.Autowired",
      "javax.persistence.EntityManager");
  private final String CLASS_DECLARATION
      = "@Generated(value = \"io.github.dvplabs.scrud.CrudGenerator\", date = \"%s\")\n"
      + "@RestController\n"
      + "@RequestMapping(path = \"%s\")\n"
      + "public class %sCrudController {\n"
      + "  @Autowired\n"
      + "  private EntityManager entityManager;\n"
      + "  @Autowired\n"
      + "  private %s mapper;\n";

  Map<Verb, VerbProcessor> processors = new HashMap<>();
  private final Clock clock;

  public CrudGenerator() throws IOException {
    this(Clock.systemDefaultZone());
  }
  
  public CrudGenerator(Clock clock) throws IOException {
    super();
    this.clock = clock;
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
        .map(this::getEnumName)
        .map(processors::get)
        .collect(toList());
    values.model = new TypeName(defaults.get("model").getValue().toString());
    values.dto = new TypeName(defaults.get("dto").getValue().toString());
    return values;
  }
  
  private List<Pair> findAvailableMethods(Element el, AnnotationValues val) {
    var foundMethods = el.getEnclosedElements().stream()
        .filter(element -> element.getKind() == ElementKind.METHOD)
        .map(methodDef -> new Method(methodDef, val.model.getFullName(), val.dto.getFullName()))
        .flatMap(method -> val.verbProcessors.stream().map(verb -> new Pair(method, verb)))
        .filter(pair -> pair.processor.getVerb().validFor(pair.method))
        .collect(groupingBy(Pair::getProcessor,
            mapping(Pair::getMethod, toList())));
    
    var missingVerbs = val.verbProcessors.stream()
        .filter(processor -> !foundMethods.containsKey(processor))
        .map(processor -> processor.getVerb().getMissingMethodMsg())
        .collect(toList());
    
    if (!missingVerbs.isEmpty()) {
      throw new InvalidCrudDefinitionException(String.join("\n", missingVerbs));
    }
    
    var tooMany = val.verbProcessors.stream()
        .filter(processor -> foundMethods.get(processor).size() > 1)
        .map(processor -> processor.getVerb().getTooManyMethodsMsg(foundMethods.get(processor)))
        .collect(toList());
    
    if (!tooMany.isEmpty()) {
      throw new InvalidCrudDefinitionException(String.join("\n", tooMany));
    }
    
    var verbs = val.verbProcessors.stream()
        .map(processor -> new Pair(foundMethods.get(processor).get(0), processor))
        .collect(toList());
    return verbs;
  }

  private void writeToFile(
      TypeName clazz,
      AnnotationValues val,
      List<Pair> methods) throws IOException, TemplateException {
    String className = findResourceName(val.resource);
    var file = processingEnv.getFiler().createSourceFile(
        clazz.getPackageName() + format(".%sCrudController", className));
    
    var conciliator = new DependencyConciliator();
    conciliator.addDependencies(Stream.of(
          BASIC_IMPORTS.stream(),
          methods.stream().flatMap(p -> p.processor.getVerb().getDependencies().stream()),
          methods.stream().flatMap(p -> p.method.getDependencies().stream()))
        .flatMap(Function.identity())
        .collect(toList()));    
    
    var imports = conciliator.getNonClashing().stream()
        .map(dep -> "import " + dep + ";")
        .sorted()
        .collect(joining("\n"));

    try (var writer = new PrintWriter(file.openWriter())) {
      writer.println(format(PACKAGE_DECLARATION, clazz.getPackageName()));
      writer.println(imports);
      writer.println("");
      writer.println(format(CLASS_DECLARATION, 
          nowFormatted(), 
          val.resource, 
          className, 
          clazz.getSimpleName()));
      
      for (var v : methods) {
        var src = v.processor.generateSourceCode(v.method, conciliator);
        writer.println(src);
      }

      writer.println("}");
    }
  }
  
  private String nowFormatted() {
    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd H:m:s")
        .format(LocalDateTime.now(clock));
  }

  private String findResourceName(String resource) {
    return Arrays.stream(resource.split("/"))
        .map(s -> s.replaceAll("^[0-9]+", ""))
        .filter(s -> s.length() > 0)
        .map(this::capitalize)
        .collect(joining());
  }
  
  private String capitalize(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }
  
  private Verb getEnumName(String clazz) {
    var matcher = ENUM_NAME_PATTERN.matcher(clazz);
    matcher.find();
    return Verb.valueOf(matcher.group(1));
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
