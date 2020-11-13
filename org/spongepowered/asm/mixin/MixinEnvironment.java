package org.spongepowered.asm.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.mixin.extensibility.IEnvironmentTokenProvider;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.obfuscation.RemapperChain;
import org.spongepowered.asm.service.ILegacyClassTransformer;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.ITokenProvider;
import org.spongepowered.asm.util.JavaVersion;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.perf.Profiler;

public final class MixinEnvironment implements ITokenProvider {
  public static final class Phase {
    static final Phase NOT_INITIALISED = new Phase(-1, "NOT_INITIALISED");
    
    public static final Phase PREINIT = new Phase(0, "PREINIT");
    
    public static final Phase INIT = new Phase(1, "INIT");
    
    public static final Phase DEFAULT = new Phase(2, "DEFAULT");
    
    static final List<Phase> phases = (List<Phase>)ImmutableList.of(PREINIT, INIT, DEFAULT);
    
    final int ordinal;
    
    final String name;
    
    private MixinEnvironment environment;
    
    private Phase(int param1Int, String param1String) {
      this.ordinal = param1Int;
      this.name = param1String;
    }
    
    public String toString() {
      return this.name;
    }
    
    public static Phase forName(String param1String) {
      for (Phase phase : phases) {
        if (phase.name.equals(param1String))
          return phase; 
      } 
      return null;
    }
    
    MixinEnvironment getEnvironment() {
      if (this.ordinal < 0)
        throw new IllegalArgumentException("Cannot access the NOT_INITIALISED environment"); 
      if (this.environment == null)
        this.environment = new MixinEnvironment(this); 
      return this.environment;
    }
  }
  
  public enum Side {
    UNKNOWN {
      protected boolean detect() {
        return false;
      }
    },
    CLIENT {
      protected boolean detect() {
        String str = MixinService.getService().getSideName();
        return "CLIENT".equals(str);
      }
    },
    SERVER {
      protected boolean detect() {
        String str = MixinService.getService().getSideName();
        return ("SERVER".equals(str) || "DEDICATEDSERVER".equals(str));
      }
    };
    
    protected abstract boolean detect();
  }
  
  public enum Option {
    DEBUG_ALL("debug"),
    DEBUG_EXPORT((String)DEBUG_ALL, "export"),
    DEBUG_EXPORT_FILTER((String)DEBUG_EXPORT, "filter", false),
    DEBUG_EXPORT_DECOMPILE((String)DEBUG_EXPORT, Inherit.ALLOW_OVERRIDE, (Option)"decompile"),
    DEBUG_EXPORT_DECOMPILE_THREADED((String)DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, (Option)"async"),
    DEBUG_VERIFY((String)DEBUG_ALL, "verify"),
    DEBUG_VERBOSE((String)DEBUG_ALL, "verbose"),
    DEBUG_INJECTORS((String)DEBUG_ALL, "countInjections"),
    DEBUG_STRICT((String)DEBUG_ALL, Inherit.INDEPENDENT, (Option)"strict"),
    DEBUG_UNIQUE((String)DEBUG_STRICT, "unique"),
    DEBUG_TARGETS((String)DEBUG_STRICT, "targets"),
    DEBUG_PROFILER((String)DEBUG_ALL, Inherit.ALLOW_OVERRIDE, (Option)"profiler"),
    DUMP_TARGET_ON_FAILURE("dumpTargetOnFailure"),
    CHECK_ALL("checks"),
    CHECK_IMPLEMENTS((String)CHECK_ALL, "interfaces"),
    CHECK_IMPLEMENTS_STRICT((String)CHECK_IMPLEMENTS, Inherit.ALLOW_OVERRIDE, (Option)"strict"),
    IGNORE_CONSTRAINTS("ignoreConstraints"),
    HOT_SWAP("hotSwap"),
    ENVIRONMENT((String)Inherit.ALWAYS_FALSE, "env"),
    OBFUSCATION_TYPE((String)ENVIRONMENT, Inherit.ALWAYS_FALSE, (Option)"obf"),
    DISABLE_REFMAP((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"disableRefMap"),
    REFMAP_REMAP((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"remapRefMap"),
    REFMAP_REMAP_RESOURCE((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"refMapRemappingFile", (Inherit)""),
    REFMAP_REMAP_SOURCE_ENV((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"refMapRemappingEnv", (Inherit)"searge"),
    IGNORE_REQUIRED((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"ignoreRequired"),
    DEFAULT_COMPATIBILITY_LEVEL((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"compatLevel"),
    SHIFT_BY_VIOLATION_BEHAVIOUR((String)ENVIRONMENT, Inherit.INDEPENDENT, (Option)"shiftByViolation", (Inherit)"warn"),
    INITIALISER_INJECTION_MODE("initialiserInjectionMode", "default");
    
    private static final String PREFIX = "mixin";
    
    final Option parent;
    
    final Inherit inheritance;
    
    final String property;
    
    final String defaultValue;
    
    final boolean isFlag;
    
    final int depth;
    
    private enum Inherit {
      INHERIT, ALLOW_OVERRIDE, INDEPENDENT, ALWAYS_FALSE;
    }
    
    Option(Option param1Option, Inherit param1Inherit, String param1String1, boolean param1Boolean, String param1String2) {
      this.parent = param1Option;
      this.inheritance = param1Inherit;
      this.property = ((param1Option != null) ? param1Option.property : "mixin") + "." + param1String1;
      this.defaultValue = param1String2;
      this.isFlag = param1Boolean;
      byte b = 0;
      for (; param1Option != null; b++)
        param1Option = param1Option.parent; 
      this.depth = b;
    }
    
    Option getParent() {
      return this.parent;
    }
    
    String getProperty() {
      return this.property;
    }
    
    public String toString() {
      return this.isFlag ? String.valueOf(getBooleanValue()) : getStringValue();
    }
    
    private boolean getLocalBooleanValue(boolean param1Boolean) {
      return Boolean.parseBoolean(System.getProperty(this.property, Boolean.toString(param1Boolean)));
    }
    
    private boolean getInheritedBooleanValue() {
      return (this.parent != null && this.parent.getBooleanValue());
    }
    
    final boolean getBooleanValue() {
      if (this.inheritance == Inherit.ALWAYS_FALSE)
        return false; 
      boolean bool = getLocalBooleanValue(false);
      if (this.inheritance == Inherit.INDEPENDENT)
        return bool; 
      boolean bool1 = (bool || getInheritedBooleanValue()) ? true : false;
      return (this.inheritance == Inherit.INHERIT) ? bool1 : getLocalBooleanValue(bool1);
    }
    
    final String getStringValue() {
      return (this.parent == null || this.parent.getBooleanValue()) ? System.getProperty(this.property, this.defaultValue) : this.defaultValue;
    }
    
    <E extends Enum<E>> E getEnumValue(E param1E) {
      String str = System.getProperty(this.property, param1E.name());
      try {
        return Enum.valueOf((Class)param1E.getClass(), str.toUpperCase());
      } catch (IllegalArgumentException illegalArgumentException) {
        return param1E;
      } 
    }
  }
  
  public enum CompatibilityLevel {
    JAVA_6(6, 50, false),
    JAVA_7(7, 51, false) {
      boolean isSupported() {
        return (JavaVersion.current() >= 1.7D);
      }
    },
    JAVA_8(8, 52, true) {
      boolean isSupported() {
        return (JavaVersion.current() >= 1.8D);
      }
    },
    JAVA_9(9, 53, true) {
      boolean isSupported() {
        return false;
      }
    };
    
    private static final int CLASS_V1_9 = 53;
    
    private final int ver;
    
    private final int classVersion;
    
    private final boolean supportsMethodsInInterfaces;
    
    private CompatibilityLevel maxCompatibleLevel;
    
    CompatibilityLevel(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.ver = param1Int1;
      this.classVersion = param1Int2;
      this.supportsMethodsInInterfaces = param1Boolean;
    }
    
    private void setMaxCompatibleLevel(CompatibilityLevel param1CompatibilityLevel) {
      this.maxCompatibleLevel = param1CompatibilityLevel;
    }
    
    boolean isSupported() {
      return true;
    }
    
    public int classVersion() {
      return this.classVersion;
    }
    
    public boolean supportsMethodsInInterfaces() {
      return this.supportsMethodsInInterfaces;
    }
    
    public boolean isAtLeast(CompatibilityLevel param1CompatibilityLevel) {
      return (param1CompatibilityLevel == null || this.ver >= param1CompatibilityLevel.ver);
    }
    
    public boolean canElevateTo(CompatibilityLevel param1CompatibilityLevel) {
      if (param1CompatibilityLevel == null || this.maxCompatibleLevel == null)
        return true; 
      return (param1CompatibilityLevel.ver <= this.maxCompatibleLevel.ver);
    }
    
    public boolean canSupport(CompatibilityLevel param1CompatibilityLevel) {
      if (param1CompatibilityLevel == null)
        return true; 
      return param1CompatibilityLevel.canElevateTo(this);
    }
  }
  
  static class TokenProviderWrapper implements Comparable<TokenProviderWrapper> {
    private static int nextOrder = 0;
    
    private final int priority;
    
    private final int order;
    
    private final IEnvironmentTokenProvider provider;
    
    private final MixinEnvironment environment;
    
    public TokenProviderWrapper(IEnvironmentTokenProvider param1IEnvironmentTokenProvider, MixinEnvironment param1MixinEnvironment) {
      this.provider = param1IEnvironmentTokenProvider;
      this.environment = param1MixinEnvironment;
      this.order = nextOrder++;
      this.priority = param1IEnvironmentTokenProvider.getPriority();
    }
    
    public int compareTo(TokenProviderWrapper param1TokenProviderWrapper) {
      if (param1TokenProviderWrapper == null)
        return 0; 
      if (param1TokenProviderWrapper.priority == this.priority)
        return param1TokenProviderWrapper.order - this.order; 
      return param1TokenProviderWrapper.priority - this.priority;
    }
    
    public IEnvironmentTokenProvider getProvider() {
      return this.provider;
    }
    
    Integer getToken(String param1String) {
      return this.provider.getToken(param1String, this.environment);
    }
  }
  
  static class MixinLogger {
    static MixinAppender appender = new MixinAppender("MixinLogger", null, null);
    
    public MixinLogger() {
      Logger logger = (Logger)LogManager.getLogger("FML");
      appender.start();
      logger.addAppender((Appender)appender);
    }
    
    static class MixinAppender extends AbstractAppender {
      protected MixinAppender(String param2String, Filter param2Filter, Layout<? extends Serializable> param2Layout) {
        super(param2String, param2Filter, param2Layout);
      }
      
      public void append(LogEvent param2LogEvent) {
        if (param2LogEvent.getLevel() == Level.DEBUG && "Validating minecraft".equals(param2LogEvent.getMessage().getFormat()))
          MixinEnvironment.gotoPhase(MixinEnvironment.Phase.INIT); 
      }
    }
  }
  
  static class MixinAppender extends AbstractAppender {
    protected MixinAppender(String param1String, Filter param1Filter, Layout<? extends Serializable> param1Layout) {
      super(param1String, param1Filter, param1Layout);
    }
    
    public void append(LogEvent param1LogEvent) {
      if (param1LogEvent.getLevel() == Level.DEBUG && "Validating minecraft".equals(param1LogEvent.getMessage().getFormat()))
        MixinEnvironment.gotoPhase(MixinEnvironment.Phase.INIT); 
    }
  }
  
  private static final Set<String> excludeTransformers = Sets.newHashSet((Object[])new String[] { "net.minecraftforge.fml.common.asm.transformers.EventSubscriptionTransformer", "cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer", "net.minecraftforge.fml.common.asm.transformers.TerminalTransformer", "cpw.mods.fml.common.asm.transformers.TerminalTransformer" });
  
  private static MixinEnvironment currentEnvironment;
  
  private static Phase currentPhase = Phase.NOT_INITIALISED;
  
  private static CompatibilityLevel compatibility = Option.DEFAULT_COMPATIBILITY_LEVEL.<CompatibilityLevel>getEnumValue(CompatibilityLevel.JAVA_6);
  
  private static boolean showHeader = true;
  
  private static final Logger logger = LogManager.getLogger("mixin");
  
  private static final Profiler profiler = new Profiler();
  
  private final IMixinService service;
  
  private final Phase phase;
  
  private final String configsKey;
  
  private final boolean[] options;
  
  private final Set<String> tokenProviderClasses = new HashSet<String>();
  
  private final List<TokenProviderWrapper> tokenProviders = new ArrayList<TokenProviderWrapper>();
  
  private final Map<String, Integer> internalTokens = new HashMap<String, Integer>();
  
  private final RemapperChain remappers = new RemapperChain();
  
  private Side side;
  
  private List<ILegacyClassTransformer> transformers;
  
  private String obfuscationContext = null;
  
  MixinEnvironment(Phase paramPhase) {
    this.service = MixinService.getService();
    this.phase = paramPhase;
    this.configsKey = "mixin.configs." + this.phase.name.toLowerCase();
    String str = getVersion();
    if (str == null || !"0.7.4".equals(str))
      throw new MixinException("Environment conflict, mismatched versions or you didn't call MixinBootstrap.init()"); 
    this.service.checkEnv(this);
    this.options = new boolean[(Option.values()).length];
    for (Option option : Option.values())
      this.options[option.ordinal()] = option.getBooleanValue(); 
    if (showHeader) {
      showHeader = false;
      printHeader(str);
    } 
  }
  
  private void printHeader(Object paramObject) {
    String str1 = getCodeSource();
    String str2 = this.service.getName();
    Side side = getSide();
    logger.info("SpongePowered MIXIN Subsystem Version={} Source={} Service={} Env={}", new Object[] { paramObject, str1, str2, side });
    boolean bool = getOption(Option.DEBUG_VERBOSE);
    if (bool || getOption(Option.DEBUG_EXPORT) || getOption(Option.DEBUG_PROFILER)) {
      PrettyPrinter prettyPrinter = new PrettyPrinter(32);
      prettyPrinter.add("SpongePowered MIXIN%s", new Object[] { bool ? " (Verbose debugging enabled)" : "" }).centre().hr();
      prettyPrinter.kv("Code source", str1);
      prettyPrinter.kv("Internal Version", paramObject);
      prettyPrinter.kv("Java 8 Supported", Boolean.valueOf(CompatibilityLevel.JAVA_8.isSupported())).hr();
      prettyPrinter.kv("Service Name", str2);
      prettyPrinter.kv("Service Class", this.service.getClass().getName()).hr();
      for (Option option : Option.values()) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b = 0; b < option.depth; b++)
          stringBuilder.append("- "); 
        prettyPrinter.kv(option.property, "%s<%s>", new Object[] { stringBuilder, option });
      } 
      prettyPrinter.hr().kv("Detected Side", side);
      prettyPrinter.print(System.err);
    } 
  }
  
  private String getCodeSource() {
    try {
      return getClass().getProtectionDomain().getCodeSource().getLocation().toString();
    } catch (Throwable throwable) {
      return "Unknown";
    } 
  }
  
  public Phase getPhase() {
    return this.phase;
  }
  
  @Deprecated
  public List<String> getMixinConfigs() {
    List<String> list = (List)GlobalProperties.get(this.configsKey);
    if (list == null) {
      list = new ArrayList();
      GlobalProperties.put(this.configsKey, list);
    } 
    return list;
  }
  
  @Deprecated
  public MixinEnvironment addConfiguration(String paramString) {
    logger.warn("MixinEnvironment::addConfiguration is deprecated and will be removed. Use Mixins::addConfiguration instead!");
    Mixins.addConfiguration(paramString, this);
    return this;
  }
  
  void registerConfig(String paramString) {
    List<String> list = getMixinConfigs();
    if (!list.contains(paramString))
      list.add(paramString); 
  }
  
  @Deprecated
  public MixinEnvironment registerErrorHandlerClass(String paramString) {
    Mixins.registerErrorHandlerClass(paramString);
    return this;
  }
  
  public MixinEnvironment registerTokenProviderClass(String paramString) {
    if (!this.tokenProviderClasses.contains(paramString))
      try {
        Class<IEnvironmentTokenProvider> clazz = this.service.getClassProvider().findClass(paramString, true);
        IEnvironmentTokenProvider iEnvironmentTokenProvider = clazz.newInstance();
        registerTokenProvider(iEnvironmentTokenProvider);
      } catch (Throwable throwable) {
        logger.error("Error instantiating " + paramString, throwable);
      }  
    return this;
  }
  
  public MixinEnvironment registerTokenProvider(IEnvironmentTokenProvider paramIEnvironmentTokenProvider) {
    if (paramIEnvironmentTokenProvider != null && !this.tokenProviderClasses.contains(paramIEnvironmentTokenProvider.getClass().getName())) {
      String str = paramIEnvironmentTokenProvider.getClass().getName();
      TokenProviderWrapper tokenProviderWrapper = new TokenProviderWrapper(paramIEnvironmentTokenProvider, this);
      logger.info("Adding new token provider {} to {}", new Object[] { str, this });
      this.tokenProviders.add(tokenProviderWrapper);
      this.tokenProviderClasses.add(str);
      Collections.sort(this.tokenProviders);
    } 
    return this;
  }
  
  public Integer getToken(String paramString) {
    paramString = paramString.toUpperCase();
    for (TokenProviderWrapper tokenProviderWrapper : this.tokenProviders) {
      Integer integer = tokenProviderWrapper.getToken(paramString);
      if (integer != null)
        return integer; 
    } 
    return this.internalTokens.get(paramString);
  }
  
  @Deprecated
  public Set<String> getErrorHandlerClasses() {
    return Mixins.getErrorHandlerClasses();
  }
  
  public Object getActiveTransformer() {
    return GlobalProperties.get("mixin.transformer");
  }
  
  public void setActiveTransformer(ITransformer paramITransformer) {
    if (paramITransformer != null)
      GlobalProperties.put("mixin.transformer", paramITransformer); 
  }
  
  public MixinEnvironment setSide(Side paramSide) {
    if (paramSide != null && getSide() == Side.UNKNOWN && paramSide != Side.UNKNOWN)
      this.side = paramSide; 
    return this;
  }
  
  public Side getSide() {
    if (this.side == null)
      for (Side side : Side.values()) {
        if (side.detect()) {
          this.side = side;
          break;
        } 
      }  
    return (this.side != null) ? this.side : Side.UNKNOWN;
  }
  
  public String getVersion() {
    return (String)GlobalProperties.get("mixin.initialised");
  }
  
  public boolean getOption(Option paramOption) {
    return this.options[paramOption.ordinal()];
  }
  
  public void setOption(Option paramOption, boolean paramBoolean) {
    this.options[paramOption.ordinal()] = paramBoolean;
  }
  
  public String getOptionValue(Option paramOption) {
    return paramOption.getStringValue();
  }
  
  public <E extends Enum<E>> E getOption(Option paramOption, E paramE) {
    return paramOption.getEnumValue(paramE);
  }
  
  public void setObfuscationContext(String paramString) {
    this.obfuscationContext = paramString;
  }
  
  public String getObfuscationContext() {
    return this.obfuscationContext;
  }
  
  public String getRefmapObfuscationContext() {
    String str = Option.OBFUSCATION_TYPE.getStringValue();
    if (str != null)
      return str; 
    return this.obfuscationContext;
  }
  
  public RemapperChain getRemappers() {
    return this.remappers;
  }
  
  public void audit() {
    Object object = getActiveTransformer();
    if (object instanceof MixinTransformer) {
      MixinTransformer mixinTransformer = (MixinTransformer)object;
      mixinTransformer.audit(this);
    } 
  }
  
  public List<ILegacyClassTransformer> getTransformers() {
    if (this.transformers == null)
      buildTransformerDelegationList(); 
    return Collections.unmodifiableList(this.transformers);
  }
  
  public void addTransformerExclusion(String paramString) {
    excludeTransformers.add(paramString);
    this.transformers = null;
  }
  
  private void buildTransformerDelegationList() {
    logger.debug("Rebuilding transformer delegation list:");
    this.transformers = new ArrayList<ILegacyClassTransformer>();
    for (ITransformer iTransformer : this.service.getTransformers()) {
      if (!(iTransformer instanceof ILegacyClassTransformer))
        continue; 
      ILegacyClassTransformer iLegacyClassTransformer = (ILegacyClassTransformer)iTransformer;
      String str = iLegacyClassTransformer.getName();
      boolean bool = true;
      for (String str1 : excludeTransformers) {
        if (str.contains(str1)) {
          bool = false;
          break;
        } 
      } 
      if (bool && !iLegacyClassTransformer.isDelegationExcluded()) {
        logger.debug("  Adding:    {}", new Object[] { str });
        this.transformers.add(iLegacyClassTransformer);
        continue;
      } 
      logger.debug("  Excluding: {}", new Object[] { str });
    } 
    logger.debug("Transformer delegation list created with {} entries", new Object[] { Integer.valueOf(this.transformers.size()) });
  }
  
  public String toString() {
    return String.format("%s[%s]", new Object[] { getClass().getSimpleName(), this.phase });
  }
  
  private static Phase getCurrentPhase() {
    if (currentPhase == Phase.NOT_INITIALISED)
      init(Phase.PREINIT); 
    return currentPhase;
  }
  
  public static void init(Phase paramPhase) {
    if (currentPhase == Phase.NOT_INITIALISED) {
      currentPhase = paramPhase;
      MixinEnvironment mixinEnvironment = getEnvironment(paramPhase);
      getProfiler().setActive(mixinEnvironment.getOption(Option.DEBUG_PROFILER));
      MixinLogger mixinLogger = new MixinLogger();
    } 
  }
  
  public static MixinEnvironment getEnvironment(Phase paramPhase) {
    if (paramPhase == null)
      return Phase.DEFAULT.getEnvironment(); 
    return paramPhase.getEnvironment();
  }
  
  public static MixinEnvironment getDefaultEnvironment() {
    return getEnvironment(Phase.DEFAULT);
  }
  
  public static MixinEnvironment getCurrentEnvironment() {
    if (currentEnvironment == null)
      currentEnvironment = getEnvironment(getCurrentPhase()); 
    return currentEnvironment;
  }
  
  public static CompatibilityLevel getCompatibilityLevel() {
    return compatibility;
  }
  
  @Deprecated
  public static void setCompatibilityLevel(CompatibilityLevel paramCompatibilityLevel) throws IllegalArgumentException {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    if (!"org.spongepowered.asm.mixin.transformer.MixinConfig".equals(arrayOfStackTraceElement[2].getClassName()))
      logger.warn("MixinEnvironment::setCompatibilityLevel is deprecated and will be removed. Set level via config instead!"); 
    if (paramCompatibilityLevel != compatibility && paramCompatibilityLevel.isAtLeast(compatibility)) {
      if (!paramCompatibilityLevel.isSupported())
        throw new IllegalArgumentException("The requested compatibility level " + paramCompatibilityLevel + " could not be set. Level is not supported"); 
      compatibility = paramCompatibilityLevel;
      logger.info("Compatibility level set to {}", new Object[] { paramCompatibilityLevel });
    } 
  }
  
  public static Profiler getProfiler() {
    return profiler;
  }
  
  static void gotoPhase(Phase paramPhase) {
    if (paramPhase == null || paramPhase.ordinal < 0)
      throw new IllegalArgumentException("Cannot go to the specified phase, phase is null or invalid"); 
    if (paramPhase.ordinal > (getCurrentPhase()).ordinal)
      MixinService.getService().beginPhase(); 
    if (paramPhase == Phase.DEFAULT) {
      Logger logger = (Logger)LogManager.getLogger("FML");
      logger.removeAppender((Appender)MixinLogger.appender);
    } 
    currentPhase = paramPhase;
    currentEnvironment = getEnvironment(getCurrentPhase());
  }
}
