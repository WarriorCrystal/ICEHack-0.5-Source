package org.reflections.serializers;

import java.io.File;
import java.io.InputStream;
import org.reflections.Reflections;

public interface Serializer {
  Reflections read(InputStream paramInputStream);
  
  File save(Reflections paramReflections, String paramString);
  
  String toString(Reflections paramReflections);
}
