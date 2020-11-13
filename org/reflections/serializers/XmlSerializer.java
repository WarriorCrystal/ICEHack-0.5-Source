package org.reflections.serializers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.Store;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.Utils;

public class XmlSerializer implements Serializer {
  public Reflections read(InputStream paramInputStream) {
    Reflections reflections;
    try {
      Constructor<Reflections> constructor = Reflections.class.getDeclaredConstructor(new Class[0]);
      constructor.setAccessible(true);
      reflections = constructor.newInstance(new Object[0]);
    } catch (Exception exception) {
      reflections = new Reflections((Configuration)new ConfigurationBuilder());
    } 
    try {
      Document document = (new SAXReader()).read(paramInputStream);
      for (Element element1 : document.getRootElement().elements()) {
        Element element2 = element1;
        for (Element element3 : element2.elements()) {
          Element element4 = element3;
          Element element5 = element4.element("key");
          Element element6 = element4.element("values");
          for (Element element7 : element6.elements()) {
            Element element8 = element7;
            reflections.getStore().getOrCreate(element2.getName()).put(element5.getText(), element8.getText());
          } 
        } 
      } 
    } catch (DocumentException documentException) {
      throw new ReflectionsException("could not read.", documentException);
    } catch (Throwable throwable) {
      throw new RuntimeException("Could not read. Make sure relevant dependencies exist on classpath.", throwable);
    } 
    return reflections;
  }
  
  public File save(Reflections paramReflections, String paramString) {
    File file = Utils.prepareFile(paramString);
    try {
      Document document = createDocument(paramReflections);
      XMLWriter xMLWriter = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
      xMLWriter.write(document);
      xMLWriter.close();
    } catch (IOException iOException) {
      throw new ReflectionsException("could not save to file " + paramString, iOException);
    } catch (Throwable throwable) {
      throw new RuntimeException("Could not save to file " + paramString + ". Make sure relevant dependencies exist on classpath.", throwable);
    } 
    return file;
  }
  
  public String toString(Reflections paramReflections) {
    Document document = createDocument(paramReflections);
    try {
      StringWriter stringWriter = new StringWriter();
      XMLWriter xMLWriter = new XMLWriter(stringWriter, OutputFormat.createPrettyPrint());
      xMLWriter.write(document);
      xMLWriter.close();
      return stringWriter.toString();
    } catch (IOException iOException) {
      throw new RuntimeException();
    } 
  }
  
  private Document createDocument(Reflections paramReflections) {
    Store store = paramReflections.getStore();
    Document document = DocumentFactory.getInstance().createDocument();
    Element element = document.addElement("Reflections");
    for (String str : store.keySet()) {
      Element element1 = element.addElement(str);
      for (String str1 : store.get(str).keySet()) {
        Element element2 = element1.addElement("entry");
        element2.addElement("key").setText(str1);
        Element element3 = element2.addElement("values");
        for (String str2 : store.get(str).get(str1))
          element3.addElement("value").setText(str2); 
      } 
    } 
    return document;
  }
}
