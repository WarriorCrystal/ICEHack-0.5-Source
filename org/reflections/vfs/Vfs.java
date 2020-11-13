package org.reflections.vfs;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import javax.annotation.Nullable;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.Utils;

public abstract class Vfs {
  private static List<UrlType> defaultUrlTypes = Lists.newArrayList((Object[])DefaultUrlTypes.values());
  
  public static List<UrlType> getDefaultUrlTypes() {
    return defaultUrlTypes;
  }
  
  public static void setDefaultURLTypes(List<UrlType> paramList) {
    defaultUrlTypes = paramList;
  }
  
  public static void addDefaultURLTypes(UrlType paramUrlType) {
    defaultUrlTypes.add(0, paramUrlType);
  }
  
  public static Dir fromURL(URL paramURL) {
    return fromURL(paramURL, defaultUrlTypes);
  }
  
  public static Dir fromURL(URL paramURL, List<UrlType> paramList) {
    for (UrlType urlType : paramList) {
      try {
        if (urlType.matches(paramURL)) {
          Dir dir = urlType.createDir(paramURL);
          if (dir != null)
            return dir; 
        } 
      } catch (Throwable throwable) {
        if (Reflections.log != null)
          Reflections.log.warn("could not create Dir using " + urlType + " from url " + paramURL.toExternalForm() + ". skipping.", throwable); 
      } 
    } 
    throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + paramURL.toExternalForm() + "]\neither use fromURL(final URL url, final List<UrlType> urlTypes) or use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) with your specialized UrlType.");
  }
  
  public static Dir fromURL(URL paramURL, UrlType... paramVarArgs) {
    return fromURL(paramURL, Lists.newArrayList((Object[])paramVarArgs));
  }
  
  public static Iterable<File> findFiles(Collection<URL> paramCollection, final String packagePrefix, final Predicate<String> nameFilter) {
    Predicate<File> predicate = new Predicate<File>() {
        public boolean apply(Vfs.File param1File) {
          String str = param1File.getRelativePath();
          if (str.startsWith(packagePrefix)) {
            String str1 = str.substring(str.indexOf(packagePrefix) + packagePrefix.length());
            return (!Utils.isEmpty(str1) && nameFilter.apply(str1.substring(1)));
          } 
          return false;
        }
      };
    return findFiles(paramCollection, predicate);
  }
  
  public static Iterable<File> findFiles(Collection<URL> paramCollection, Predicate<File> paramPredicate) {
    Iterable<File> iterable = new ArrayList();
    for (URL uRL : paramCollection) {
      try {
        iterable = Iterables.concat(iterable, 
            Iterables.filter(new Iterable<File>() {
                public Iterator<Vfs.File> iterator() {
                  return Vfs.fromURL(url).getFiles().iterator();
                }
              },  paramPredicate));
      } catch (Throwable throwable) {
        if (Reflections.log != null)
          Reflections.log.error("could not findFiles for url. continuing. [" + uRL + "]", throwable); 
      } 
    } 
    return iterable;
  }
  
  @Nullable
  public static java.io.File getFile(URL paramURL) {
    try {
      String str = paramURL.toURI().getSchemeSpecificPart();
      java.io.File file;
      if ((file = new java.io.File(str)).exists())
        return file; 
    } catch (URISyntaxException uRISyntaxException) {}
    try {
      String str = URLDecoder.decode(paramURL.getPath(), "UTF-8");
      if (str.contains(".jar!"))
        str = str.substring(0, str.lastIndexOf(".jar!") + ".jar".length()); 
      java.io.File file;
      if ((file = new java.io.File(str)).exists())
        return file; 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    try {
      String str = paramURL.toExternalForm();
      if (str.startsWith("jar:"))
        str = str.substring("jar:".length()); 
      if (str.startsWith("wsjar:"))
        str = str.substring("wsjar:".length()); 
      if (str.startsWith("file:"))
        str = str.substring("file:".length()); 
      if (str.contains(".jar!"))
        str = str.substring(0, str.indexOf(".jar!") + ".jar".length()); 
      java.io.File file;
      if ((file = new java.io.File(str)).exists())
        return file; 
      str = str.replace("%20", " ");
      if ((file = new java.io.File(str)).exists())
        return file; 
    } catch (Exception exception) {}
    return null;
  }
  
  private static boolean hasJarFileInPath(URL paramURL) {
    return paramURL.toExternalForm().matches(".*\\.jar(\\!.*|$)");
  }
  
  public static interface Dir {
    String getPath();
    
    Iterable<Vfs.File> getFiles();
    
    void close();
  }
  
  public static interface File {
    String getName();
    
    String getRelativePath();
    
    InputStream openInputStream() throws IOException;
  }
  
  public static interface UrlType {
    boolean matches(URL param1URL) throws Exception;
    
    Vfs.Dir createDir(URL param1URL) throws Exception;
  }
  
  public enum DefaultUrlTypes implements UrlType {
    jarFile {
      public boolean matches(URL param2URL) {
        return (param2URL.getProtocol().equals("file") && Vfs.hasJarFileInPath(param2URL));
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        return new ZipDir(new JarFile(Vfs.getFile(param2URL)));
      }
    },
    jarUrl {
      public boolean matches(URL param2URL) {
        return ("jar".equals(param2URL.getProtocol()) || "zip".equals(param2URL.getProtocol()) || "wsjar".equals(param2URL.getProtocol()));
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        try {
          URLConnection uRLConnection = param2URL.openConnection();
          if (uRLConnection instanceof JarURLConnection)
            return new ZipDir(((JarURLConnection)uRLConnection).getJarFile()); 
        } catch (Throwable throwable) {}
        java.io.File file = Vfs.getFile(param2URL);
        if (file != null)
          return new ZipDir(new JarFile(file)); 
        return null;
      }
    },
    directory {
      public boolean matches(URL param2URL) {
        if (param2URL.getProtocol().equals("file") && !Vfs.hasJarFileInPath(param2URL)) {
          java.io.File file = Vfs.getFile(param2URL);
          return (file != null && file.isDirectory());
        } 
        return false;
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        return new SystemDir(Vfs.getFile(param2URL));
      }
    },
    jboss_vfs {
      public boolean matches(URL param2URL) {
        return param2URL.getProtocol().equals("vfs");
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        Object object = param2URL.openConnection().getContent();
        Class<?> clazz = ClasspathHelper.contextClassLoader().loadClass("org.jboss.vfs.VirtualFile");
        java.io.File file1 = (java.io.File)clazz.getMethod("getPhysicalFile", new Class[0]).invoke(object, new Object[0]);
        String str = (String)clazz.getMethod("getName", new Class[0]).invoke(object, new Object[0]);
        java.io.File file2 = new java.io.File(file1.getParentFile(), str);
        if (!file2.exists() || !file2.canRead())
          file2 = file1; 
        return file2.isDirectory() ? new SystemDir(file2) : new ZipDir(new JarFile(file2));
      }
    },
    jboss_vfsfile {
      public boolean matches(URL param2URL) throws Exception {
        return ("vfszip".equals(param2URL.getProtocol()) || "vfsfile".equals(param2URL.getProtocol()));
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        return (new UrlTypeVFS()).createDir(param2URL);
      }
    },
    bundle {
      public boolean matches(URL param2URL) throws Exception {
        return param2URL.getProtocol().startsWith("bundle");
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        return Vfs.fromURL((URL)ClasspathHelper.contextClassLoader()
            .loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve", new Class[] { URL.class }).invoke(null, new Object[] { param2URL }));
      }
    },
    jarInputStream {
      public boolean matches(URL param2URL) throws Exception {
        return param2URL.toExternalForm().contains(".jar");
      }
      
      public Vfs.Dir createDir(URL param2URL) throws Exception {
        return new JarInputDir(param2URL);
      }
    };
  }
}
