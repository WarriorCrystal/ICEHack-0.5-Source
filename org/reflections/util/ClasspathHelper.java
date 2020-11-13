package org.reflections.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;
import org.reflections.Reflections;

public abstract class ClasspathHelper {
  public static ClassLoader contextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public static ClassLoader staticClassLoader() {
    return Reflections.class.getClassLoader();
  }
  
  public static ClassLoader[] classLoaders(ClassLoader... paramVarArgs) {
    if (paramVarArgs != null && paramVarArgs.length != 0)
      return paramVarArgs; 
    ClassLoader classLoader1 = contextClassLoader(), classLoader2 = staticClassLoader();
    (new ClassLoader[2])[0] = classLoader1;
    (new ClassLoader[2])[1] = classLoader2;
    (new ClassLoader[1])[0] = classLoader1;
    return (classLoader1 != null) ? ((classLoader2 != null && classLoader1 != classLoader2) ? new ClassLoader[2] : new ClassLoader[1]) : new ClassLoader[0];
  }
  
  public static Collection<URL> forPackage(String paramString, ClassLoader... paramVarArgs) {
    return forResource(resourceName(paramString), paramVarArgs);
  }
  
  public static Collection<URL> forResource(String paramString, ClassLoader... paramVarArgs) {
    ArrayList<URL> arrayList = new ArrayList();
    ClassLoader[] arrayOfClassLoader = classLoaders(paramVarArgs);
    for (ClassLoader classLoader : arrayOfClassLoader) {
      try {
        Enumeration<URL> enumeration = classLoader.getResources(paramString);
        while (enumeration.hasMoreElements()) {
          URL uRL = enumeration.nextElement();
          int i = uRL.toExternalForm().lastIndexOf(paramString);
          if (i != -1) {
            arrayList.add(new URL(uRL, uRL.toExternalForm().substring(0, i)));
            continue;
          } 
          arrayList.add(uRL);
        } 
      } catch (IOException iOException) {
        if (Reflections.log != null)
          Reflections.log.error("error getting resources for " + paramString, iOException); 
      } 
    } 
    return distinctUrls(arrayList);
  }
  
  public static URL forClass(Class<?> paramClass, ClassLoader... paramVarArgs) {
    ClassLoader[] arrayOfClassLoader = classLoaders(paramVarArgs);
    String str = paramClass.getName().replace(".", "/") + ".class";
    for (ClassLoader classLoader : arrayOfClassLoader) {
      try {
        URL uRL = classLoader.getResource(str);
        if (uRL != null) {
          String str1 = uRL.toExternalForm().substring(0, uRL.toExternalForm().lastIndexOf(paramClass.getPackage().getName().replace(".", "/")));
          return new URL(str1);
        } 
      } catch (MalformedURLException malformedURLException) {
        if (Reflections.log != null)
          Reflections.log.warn("Could not get URL", malformedURLException); 
      } 
    } 
    return null;
  }
  
  public static Collection<URL> forClassLoader() {
    return forClassLoader(classLoaders(new ClassLoader[0]));
  }
  
  public static Collection<URL> forClassLoader(ClassLoader... paramVarArgs) {
    ArrayList<URL> arrayList = new ArrayList();
    ClassLoader[] arrayOfClassLoader = classLoaders(paramVarArgs);
    for (ClassLoader classLoader : arrayOfClassLoader) {
      while (classLoader != null) {
        if (classLoader instanceof URLClassLoader) {
          URL[] arrayOfURL = ((URLClassLoader)classLoader).getURLs();
          if (arrayOfURL != null)
            arrayList.addAll(Arrays.asList(arrayOfURL)); 
        } 
        classLoader = classLoader.getParent();
      } 
    } 
    return distinctUrls(arrayList);
  }
  
  public static Collection<URL> forJavaClassPath() {
    ArrayList<URL> arrayList = new ArrayList();
    String str = System.getProperty("java.class.path");
    if (str != null)
      for (String str1 : str.split(File.pathSeparator)) {
        try {
          arrayList.add((new File(str1)).toURI().toURL());
        } catch (Exception exception) {
          if (Reflections.log != null)
            Reflections.log.warn("Could not get URL", exception); 
        } 
      }  
    return distinctUrls(arrayList);
  }
  
  public static Collection<URL> forWebInfLib(ServletContext paramServletContext) {
    ArrayList<URL> arrayList = new ArrayList();
    Set set = paramServletContext.getResourcePaths("/WEB-INF/lib");
    if (set == null)
      return arrayList; 
    for (String str : set) {
      try {
        arrayList.add(paramServletContext.getResource(str));
      } catch (MalformedURLException malformedURLException) {}
    } 
    return distinctUrls(arrayList);
  }
  
  public static URL forWebInfClasses(ServletContext paramServletContext) {
    try {
      String str = paramServletContext.getRealPath("/WEB-INF/classes");
      if (str != null) {
        File file = new File(str);
        if (file.exists())
          return file.toURL(); 
      } else {
        return paramServletContext.getResource("/WEB-INF/classes");
      } 
    } catch (MalformedURLException malformedURLException) {}
    return null;
  }
  
  public static Collection<URL> forManifest() {
    return forManifest(forClassLoader());
  }
  
  public static Collection<URL> forManifest(URL paramURL) {
    ArrayList<URL> arrayList = new ArrayList();
    arrayList.add(paramURL);
    try {
      String str = cleanPath(paramURL);
      File file = new File(str);
      JarFile jarFile = new JarFile(str);
      URL uRL = tryToGetValidUrl(file.getPath(), (new File(str)).getParent(), str);
      if (uRL != null)
        arrayList.add(uRL); 
      Manifest manifest = jarFile.getManifest();
      if (manifest != null) {
        String str1 = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
        if (str1 != null)
          for (String str2 : str1.split(" ")) {
            uRL = tryToGetValidUrl(file.getPath(), (new File(str)).getParent(), str2);
            if (uRL != null)
              arrayList.add(uRL); 
          }  
      } 
    } catch (IOException iOException) {}
    return distinctUrls(arrayList);
  }
  
  public static Collection<URL> forManifest(Iterable<URL> paramIterable) {
    ArrayList<URL> arrayList = new ArrayList();
    for (URL uRL : paramIterable)
      arrayList.addAll(forManifest(uRL)); 
    return distinctUrls(arrayList);
  }
  
  static URL tryToGetValidUrl(String paramString1, String paramString2, String paramString3) {
    try {
      if ((new File(paramString3)).exists())
        return (new File(paramString3)).toURI().toURL(); 
      if ((new File(paramString2 + File.separator + paramString3)).exists())
        return (new File(paramString2 + File.separator + paramString3)).toURI().toURL(); 
      if ((new File(paramString1 + File.separator + paramString3)).exists())
        return (new File(paramString1 + File.separator + paramString3)).toURI().toURL(); 
      if ((new File((new URL(paramString3)).getFile())).exists())
        return (new File((new URL(paramString3)).getFile())).toURI().toURL(); 
    } catch (MalformedURLException malformedURLException) {}
    return null;
  }
  
  public static String cleanPath(URL paramURL) {
    String str = paramURL.getPath();
    try {
      str = URLDecoder.decode(str, "UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    if (str.startsWith("jar:"))
      str = str.substring("jar:".length()); 
    if (str.startsWith("file:"))
      str = str.substring("file:".length()); 
    if (str.endsWith("!/"))
      str = str.substring(0, str.lastIndexOf("!/")) + "/"; 
    return str;
  }
  
  private static String resourceName(String paramString) {
    if (paramString != null) {
      String str = paramString.replace(".", "/");
      str = str.replace("\\", "/");
      if (str.startsWith("/"))
        str = str.substring(1); 
      return str;
    } 
    return null;
  }
  
  private static Collection<URL> distinctUrls(Collection<URL> paramCollection) {
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>(paramCollection.size());
    for (URL uRL : paramCollection)
      linkedHashMap.put(uRL.toExternalForm(), uRL); 
    return linkedHashMap.values();
  }
}
