package org.reflections.serializers;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.util.Utils;

public class JsonSerializer implements Serializer {
  private Gson gson;
  
  public Reflections read(InputStream paramInputStream) {
    return (Reflections)getGson().fromJson(new InputStreamReader(paramInputStream), Reflections.class);
  }
  
  public File save(Reflections paramReflections, String paramString) {
    try {
      File file = Utils.prepareFile(paramString);
      Files.write(toString(paramReflections), file, Charset.defaultCharset());
      return file;
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public String toString(Reflections paramReflections) {
    return getGson().toJson(paramReflections);
  }
  
  private Gson getGson() {
    if (this.gson == null)
      this
        
        .gson = (new GsonBuilder()).registerTypeAdapter(Multimap.class, new com.google.gson.JsonSerializer<Multimap>() {
            public JsonElement serialize(Multimap param1Multimap, Type param1Type, JsonSerializationContext param1JsonSerializationContext) {
              return param1JsonSerializationContext.serialize(param1Multimap.asMap());
            }
          }).registerTypeAdapter(Multimap.class, new JsonDeserializer<Multimap>() {
            public Multimap deserialize(JsonElement param1JsonElement, Type param1Type, JsonDeserializationContext param1JsonDeserializationContext) throws JsonParseException {
              SetMultimap setMultimap = Multimaps.newSetMultimap(new HashMap<>(), new Supplier<Set<String>>() {
                    public Set<String> get() {
                      return Sets.newHashSet();
                    }
                  });
              for (Map.Entry entry : ((JsonObject)param1JsonElement).entrySet()) {
                for (JsonElement jsonElement : entry.getValue())
                  setMultimap.get(entry.getKey()).add(jsonElement.getAsString()); 
              } 
              return (Multimap)setMultimap;
            }
          }).setPrettyPrinting().create(); 
    return this.gson;
  }
}
