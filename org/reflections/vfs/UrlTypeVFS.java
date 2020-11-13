package org.reflections.vfs;

import com.google.common.base.Predicate;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;

public class UrlTypeVFS implements Vfs.UrlType {
  public static final String[] REPLACE_EXTENSION = new String[] { ".ear/", ".jar/", ".war/", ".sar/", ".har/", ".par/" };
  
  final String VFSZIP = "vfszip";
  
  final String VFSFILE = "vfsfile";
  
  public boolean matches(URL paramURL) {
    return ("vfszip".equals(paramURL.getProtocol()) || "vfsfile".equals(paramURL.getProtocol()));
  }
  
  public Vfs.Dir createDir(URL paramURL) {
    try {
      URL uRL = adaptURL(paramURL);
      return new ZipDir(new JarFile(uRL.getFile()));
    } catch (Exception exception) {
      try {
        return new ZipDir(new JarFile(paramURL.getFile()));
      } catch (IOException iOException) {
        if (Reflections.log != null) {
          Reflections.log.warn("Could not get URL", exception);
          Reflections.log.warn("Could not get URL", iOException);
        } 
        return null;
      } 
    } 
  }
  
  public URL adaptURL(URL paramURL) throws MalformedURLException {
    if ("vfszip".equals(paramURL.getProtocol()))
      return replaceZipSeparators(paramURL.getPath(), this.realFile); 
    if ("vfsfile".equals(paramURL.getProtocol()))
      return new URL(paramURL.toString().replace("vfsfile", "file")); 
    return paramURL;
  }
  
  URL replaceZipSeparators(String paramString, Predicate<File> paramPredicate) throws MalformedURLException {
    int i = 0;
    while (i != -1) {
      i = findFirstMatchOfDeployableExtention(paramString, i);
      if (i > 0) {
        File file = new File(paramString.substring(0, i - 1));
        if (paramPredicate.apply(file))
          return replaceZipSeparatorStartingFrom(paramString, i); 
      } 
    } 
    throw new ReflectionsException("Unable to identify the real zip file in path '" + paramString + "'.");
  }
  
  int findFirstMatchOfDeployableExtention(String paramString, int paramInt) {
    Pattern pattern = Pattern.compile("\\.[ejprw]ar/");
    Matcher matcher = pattern.matcher(paramString);
    if (matcher.find(paramInt))
      return matcher.end(); 
    return -1;
  }
  
  Predicate<File> realFile = new Predicate<File>() {
      public boolean apply(File param1File) {
        return (param1File.exists() && param1File.isFile());
      }
    };
  
  URL replaceZipSeparatorStartingFrom(String paramString, int paramInt) throws MalformedURLException {
    String str1 = paramString.substring(0, paramInt - 1);
    String str2 = paramString.substring(paramInt);
    byte b1 = 1;
    for (String str : REPLACE_EXTENSION) {
      while (str2.contains(str)) {
        str2 = str2.replace(str, str.substring(0, 4) + "!");
        b1++;
      } 
    } 
    String str3 = "";
    for (byte b2 = 0; b2 < b1; b2++)
      str3 = str3 + "zip:"; 
    if (str2.trim().length() == 0)
      return new URL(str3 + "/" + str1); 
    return new URL(str3 + "/" + str1 + "!" + str2);
  }
}
