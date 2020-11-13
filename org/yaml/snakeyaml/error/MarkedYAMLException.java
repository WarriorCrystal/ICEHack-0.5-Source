package org.yaml.snakeyaml.error;

public class MarkedYAMLException extends YAMLException {
  private static final long serialVersionUID = -9119388488683035101L;
  
  private String context;
  
  private Mark contextMark;
  
  private String problem;
  
  private Mark problemMark;
  
  private String note;
  
  protected MarkedYAMLException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2, String paramString3) {
    this(paramString1, paramMark1, paramString2, paramMark2, paramString3, null);
  }
  
  protected MarkedYAMLException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2, String paramString3, Throwable paramThrowable) {
    super(paramString1 + "; " + paramString2 + "; " + paramMark2, paramThrowable);
    this.context = paramString1;
    this.contextMark = paramMark1;
    this.problem = paramString2;
    this.problemMark = paramMark2;
    this.note = paramString3;
  }
  
  protected MarkedYAMLException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2) {
    this(paramString1, paramMark1, paramString2, paramMark2, null, null);
  }
  
  protected MarkedYAMLException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2, Throwable paramThrowable) {
    this(paramString1, paramMark1, paramString2, paramMark2, null, paramThrowable);
  }
  
  public String getMessage() {
    return toString();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.context != null) {
      stringBuilder.append(this.context);
      stringBuilder.append("\n");
    } 
    if (this.contextMark != null && (this.problem == null || this.problemMark == null || this.contextMark
      
      .getName().equals(this.problemMark.getName()) || this.contextMark
      .getLine() != this.problemMark.getLine() || this.contextMark
      .getColumn() != this.problemMark.getColumn())) {
      stringBuilder.append(this.contextMark.toString());
      stringBuilder.append("\n");
    } 
    if (this.problem != null) {
      stringBuilder.append(this.problem);
      stringBuilder.append("\n");
    } 
    if (this.problemMark != null) {
      stringBuilder.append(this.problemMark.toString());
      stringBuilder.append("\n");
    } 
    if (this.note != null) {
      stringBuilder.append(this.note);
      stringBuilder.append("\n");
    } 
    return stringBuilder.toString();
  }
  
  public String getContext() {
    return this.context;
  }
  
  public Mark getContextMark() {
    return this.contextMark;
  }
  
  public String getProblem() {
    return this.problem;
  }
  
  public Mark getProblemMark() {
    return this.problemMark;
  }
}
