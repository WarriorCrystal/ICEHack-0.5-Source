package org.yaml.snakeyaml.tokens;

public final class TagTuple {
  private final String handle;
  
  private final String suffix;
  
  public TagTuple(String paramString1, String paramString2) {
    if (paramString2 == null)
      throw new NullPointerException("Suffix must be provided."); 
    this.handle = paramString1;
    this.suffix = paramString2;
  }
  
  public String getHandle() {
    return this.handle;
  }
  
  public String getSuffix() {
    return this.suffix;
  }
}
