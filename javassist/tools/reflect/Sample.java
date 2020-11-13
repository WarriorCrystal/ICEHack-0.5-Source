package javassist.tools.reflect;

public class Sample {
  private Metaobject _metaobject;
  
  private static ClassMetaobject _classobject;
  
  public Object trap(Object[] paramArrayOfObject, int paramInt) throws Throwable {
    Metaobject metaobject = this._metaobject;
    if (metaobject == null)
      return ClassMetaobject.invoke(this, paramInt, paramArrayOfObject); 
    return metaobject.trapMethodcall(paramInt, paramArrayOfObject);
  }
  
  public static Object trapStatic(Object[] paramArrayOfObject, int paramInt) throws Throwable {
    return _classobject.trapMethodcall(paramInt, paramArrayOfObject);
  }
  
  public static Object trapRead(Object[] paramArrayOfObject, String paramString) {
    if (paramArrayOfObject[0] == null)
      return _classobject.trapFieldRead(paramString); 
    return ((Metalevel)paramArrayOfObject[0])._getMetaobject().trapFieldRead(paramString);
  }
  
  public static Object trapWrite(Object[] paramArrayOfObject, String paramString) {
    Metalevel metalevel = (Metalevel)paramArrayOfObject[0];
    if (metalevel == null) {
      _classobject.trapFieldWrite(paramString, paramArrayOfObject[1]);
    } else {
      metalevel._getMetaobject().trapFieldWrite(paramString, paramArrayOfObject[1]);
    } 
    return null;
  }
}
