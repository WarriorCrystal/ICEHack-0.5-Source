package org.yaml.snakeyaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class Yaml {
  protected final Resolver resolver;
  
  private String name;
  
  protected BaseConstructor constructor;
  
  protected Representer representer;
  
  protected DumperOptions dumperOptions;
  
  protected LoaderOptions loadingConfig;
  
  public Yaml() {
    this((BaseConstructor)new Constructor(), new Representer(), new DumperOptions(), new LoaderOptions(), new Resolver());
  }
  
  public Yaml(DumperOptions paramDumperOptions) {
    this((BaseConstructor)new Constructor(), new Representer(), paramDumperOptions);
  }
  
  public Yaml(LoaderOptions paramLoaderOptions) {
    this((BaseConstructor)new Constructor(), new Representer(), new DumperOptions(), paramLoaderOptions);
  }
  
  public Yaml(Representer paramRepresenter) {
    this((BaseConstructor)new Constructor(), paramRepresenter);
  }
  
  public Yaml(BaseConstructor paramBaseConstructor) {
    this(paramBaseConstructor, new Representer());
  }
  
  public Yaml(BaseConstructor paramBaseConstructor, Representer paramRepresenter) {
    this(paramBaseConstructor, paramRepresenter, initDumperOptions(paramRepresenter));
  }
  
  private static DumperOptions initDumperOptions(Representer paramRepresenter) {
    DumperOptions dumperOptions = new DumperOptions();
    dumperOptions.setDefaultFlowStyle(paramRepresenter.getDefaultFlowStyle());
    dumperOptions.setDefaultScalarStyle(paramRepresenter.getDefaultScalarStyle());
    dumperOptions.setAllowReadOnlyProperties(paramRepresenter.getPropertyUtils().isAllowReadOnlyProperties());
    dumperOptions.setTimeZone(paramRepresenter.getTimeZone());
    return dumperOptions;
  }
  
  public Yaml(Representer paramRepresenter, DumperOptions paramDumperOptions) {
    this((BaseConstructor)new Constructor(), paramRepresenter, paramDumperOptions, new LoaderOptions(), new Resolver());
  }
  
  public Yaml(BaseConstructor paramBaseConstructor, Representer paramRepresenter, DumperOptions paramDumperOptions) {
    this(paramBaseConstructor, paramRepresenter, paramDumperOptions, new LoaderOptions(), new Resolver());
  }
  
  public Yaml(BaseConstructor paramBaseConstructor, Representer paramRepresenter, DumperOptions paramDumperOptions, LoaderOptions paramLoaderOptions) {
    this(paramBaseConstructor, paramRepresenter, paramDumperOptions, paramLoaderOptions, new Resolver());
  }
  
  public Yaml(BaseConstructor paramBaseConstructor, Representer paramRepresenter, DumperOptions paramDumperOptions, Resolver paramResolver) {
    this(paramBaseConstructor, paramRepresenter, paramDumperOptions, new LoaderOptions(), paramResolver);
  }
  
  public Yaml(BaseConstructor paramBaseConstructor, Representer paramRepresenter, DumperOptions paramDumperOptions, LoaderOptions paramLoaderOptions, Resolver paramResolver) {
    if (!paramBaseConstructor.isExplicitPropertyUtils()) {
      paramBaseConstructor.setPropertyUtils(paramRepresenter.getPropertyUtils());
    } else if (!paramRepresenter.isExplicitPropertyUtils()) {
      paramRepresenter.setPropertyUtils(paramBaseConstructor.getPropertyUtils());
    } 
    this.constructor = paramBaseConstructor;
    this.constructor.setAllowDuplicateKeys(paramLoaderOptions.isAllowDuplicateKeys());
    if (paramDumperOptions.getIndent() <= paramDumperOptions.getIndicatorIndent())
      throw new YAMLException("Indicator indent must be smaller then indent."); 
    paramRepresenter.setDefaultFlowStyle(paramDumperOptions.getDefaultFlowStyle());
    paramRepresenter.setDefaultScalarStyle(paramDumperOptions.getDefaultScalarStyle());
    paramRepresenter.getPropertyUtils()
      .setAllowReadOnlyProperties(paramDumperOptions.isAllowReadOnlyProperties());
    paramRepresenter.setTimeZone(paramDumperOptions.getTimeZone());
    this.representer = paramRepresenter;
    this.dumperOptions = paramDumperOptions;
    this.loadingConfig = paramLoaderOptions;
    this.resolver = paramResolver;
    this.name = "Yaml:" + System.identityHashCode(this);
  }
  
  public String dump(Object paramObject) {
    ArrayList<Object> arrayList = new ArrayList(1);
    arrayList.add(paramObject);
    return dumpAll(arrayList.iterator());
  }
  
  public Node represent(Object paramObject) {
    return this.representer.represent(paramObject);
  }
  
  public String dumpAll(Iterator<? extends Object> paramIterator) {
    StringWriter stringWriter = new StringWriter();
    dumpAll(paramIterator, stringWriter, null);
    return stringWriter.toString();
  }
  
  public void dump(Object paramObject, Writer paramWriter) {
    ArrayList<Object> arrayList = new ArrayList(1);
    arrayList.add(paramObject);
    dumpAll(arrayList.iterator(), paramWriter, null);
  }
  
  public void dumpAll(Iterator<? extends Object> paramIterator, Writer paramWriter) {
    dumpAll(paramIterator, paramWriter, null);
  }
  
  private void dumpAll(Iterator<? extends Object> paramIterator, Writer paramWriter, Tag paramTag) {
    Serializer serializer = new Serializer((Emitable)new Emitter(paramWriter, this.dumperOptions), this.resolver, this.dumperOptions, paramTag);
    try {
      serializer.open();
      while (paramIterator.hasNext()) {
        Node node = this.representer.represent(paramIterator.next());
        serializer.serialize(node);
      } 
      serializer.close();
    } catch (IOException iOException) {
      throw new YAMLException(iOException);
    } 
  }
  
  public String dumpAs(Object paramObject, Tag paramTag, DumperOptions.FlowStyle paramFlowStyle) {
    DumperOptions.FlowStyle flowStyle = this.representer.getDefaultFlowStyle();
    if (paramFlowStyle != null)
      this.representer.setDefaultFlowStyle(paramFlowStyle); 
    ArrayList<Object> arrayList = new ArrayList(1);
    arrayList.add(paramObject);
    StringWriter stringWriter = new StringWriter();
    dumpAll(arrayList.iterator(), stringWriter, paramTag);
    this.representer.setDefaultFlowStyle(flowStyle);
    return stringWriter.toString();
  }
  
  public String dumpAsMap(Object paramObject) {
    return dumpAs(paramObject, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
  }
  
  public List<Event> serialize(Node paramNode) {
    SilentEmitter silentEmitter = new SilentEmitter();
    Serializer serializer = new Serializer(silentEmitter, this.resolver, this.dumperOptions, null);
    try {
      serializer.open();
      serializer.serialize(paramNode);
      serializer.close();
    } catch (IOException iOException) {
      throw new YAMLException(iOException);
    } 
    return silentEmitter.getEvents();
  }
  
  private static class SilentEmitter implements Emitable {
    private List<Event> events = new ArrayList<Event>(100);
    
    public List<Event> getEvents() {
      return this.events;
    }
    
    public void emit(Event param1Event) throws IOException {
      this.events.add(param1Event);
    }
    
    private SilentEmitter() {}
  }
  
  public <T> T load(String paramString) {
    return (T)loadFromReader(new StreamReader(paramString), Object.class);
  }
  
  public <T> T load(InputStream paramInputStream) {
    return (T)loadFromReader(new StreamReader((Reader)new UnicodeReader(paramInputStream)), Object.class);
  }
  
  public <T> T load(Reader paramReader) {
    return (T)loadFromReader(new StreamReader(paramReader), Object.class);
  }
  
  public <T> T loadAs(Reader paramReader, Class<T> paramClass) {
    return (T)loadFromReader(new StreamReader(paramReader), paramClass);
  }
  
  public <T> T loadAs(String paramString, Class<T> paramClass) {
    return (T)loadFromReader(new StreamReader(paramString), paramClass);
  }
  
  public <T> T loadAs(InputStream paramInputStream, Class<T> paramClass) {
    return (T)loadFromReader(new StreamReader((Reader)new UnicodeReader(paramInputStream)), paramClass);
  }
  
  private Object loadFromReader(StreamReader paramStreamReader, Class<?> paramClass) {
    Composer composer = new Composer((Parser)new ParserImpl(paramStreamReader), this.resolver);
    this.constructor.setComposer(composer);
    return this.constructor.getSingleData(paramClass);
  }
  
  public Iterable<Object> loadAll(Reader paramReader) {
    Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(paramReader)), this.resolver);
    this.constructor.setComposer(composer);
    Iterator<Object> iterator = new Iterator() {
        public boolean hasNext() {
          return Yaml.this.constructor.checkData();
        }
        
        public Object next() {
          return Yaml.this.constructor.getData();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    return new YamlIterable(iterator);
  }
  
  private static class YamlIterable implements Iterable<Object> {
    private Iterator<Object> iterator;
    
    public YamlIterable(Iterator<Object> param1Iterator) {
      this.iterator = param1Iterator;
    }
    
    public Iterator<Object> iterator() {
      return this.iterator;
    }
  }
  
  public Iterable<Object> loadAll(String paramString) {
    return loadAll(new StringReader(paramString));
  }
  
  public Iterable<Object> loadAll(InputStream paramInputStream) {
    return loadAll((Reader)new UnicodeReader(paramInputStream));
  }
  
  public Node compose(Reader paramReader) {
    Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(paramReader)), this.resolver);
    this.constructor.setComposer(composer);
    return composer.getSingleNode();
  }
  
  public Iterable<Node> composeAll(Reader paramReader) {
    final Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(paramReader)), this.resolver);
    this.constructor.setComposer(composer);
    Iterator<Node> iterator = new Iterator<Node>() {
        public boolean hasNext() {
          return composer.checkNode();
        }
        
        public Node next() {
          return composer.getNode();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    return new NodeIterable(iterator);
  }
  
  private static class NodeIterable implements Iterable<Node> {
    private Iterator<Node> iterator;
    
    public NodeIterable(Iterator<Node> param1Iterator) {
      this.iterator = param1Iterator;
    }
    
    public Iterator<Node> iterator() {
      return this.iterator;
    }
  }
  
  public void addImplicitResolver(Tag paramTag, Pattern paramPattern, String paramString) {
    this.resolver.addImplicitResolver(paramTag, paramPattern, paramString);
  }
  
  public String toString() {
    return this.name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String paramString) {
    this.name = paramString;
  }
  
  public Iterable<Event> parse(Reader paramReader) {
    final ParserImpl parser = new ParserImpl(new StreamReader(paramReader));
    Iterator<Event> iterator = new Iterator<Event>() {
        public boolean hasNext() {
          return (parser.peekEvent() != null);
        }
        
        public Event next() {
          return parser.getEvent();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    return new EventIterable(iterator);
  }
  
  private static class EventIterable implements Iterable<Event> {
    private Iterator<Event> iterator;
    
    public EventIterable(Iterator<Event> param1Iterator) {
      this.iterator = param1Iterator;
    }
    
    public Iterator<Event> iterator() {
      return this.iterator;
    }
  }
  
  public void setBeanAccess(BeanAccess paramBeanAccess) {
    this.constructor.getPropertyUtils().setBeanAccess(paramBeanAccess);
    this.representer.getPropertyUtils().setBeanAccess(paramBeanAccess);
  }
  
  public void addTypeDescription(TypeDescription paramTypeDescription) {
    this.constructor.addTypeDescription(paramTypeDescription);
    this.representer.addTypeDescription(paramTypeDescription);
  }
}
