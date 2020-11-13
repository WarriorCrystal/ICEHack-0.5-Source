package org.spongepowered.asm.mixin.injection.points;

import java.util.Collection;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.InjectionPoint.AtCode;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;

@AtCode("INVOKE")
public class BeforeInvoke extends InjectionPoint {
  protected final MemberInfo target;
  
  protected final MemberInfo permissiveTarget;
  
  protected final int ordinal;
  
  protected final String className;
  
  private boolean log = false;
  
  private final Logger logger = LogManager.getLogger("mixin");
  
  public BeforeInvoke(InjectionPointData paramInjectionPointData) {
    super(paramInjectionPointData);
    this.target = paramInjectionPointData.getTarget();
    this.ordinal = paramInjectionPointData.getOrdinal();
    this.log = paramInjectionPointData.get("log", false);
    this.className = getClassName();
    this.permissiveTarget = paramInjectionPointData.getContext().getOption(MixinEnvironment.Option.REFMAP_REMAP) ? this.target.transform(null) : null;
  }
  
  private String getClassName() {
    InjectionPoint.AtCode atCode = getClass().<InjectionPoint.AtCode>getAnnotation(InjectionPoint.AtCode.class);
    return String.format("@At(%s)", new Object[] { (atCode != null) ? atCode.value() : getClass().getSimpleName().toUpperCase() });
  }
  
  public BeforeInvoke setLogging(boolean paramBoolean) {
    this.log = paramBoolean;
    return this;
  }
  
  public boolean find(String paramString, InsnList paramInsnList, Collection<AbstractInsnNode> paramCollection) {
    log("{} is searching for an injection point in method with descriptor {}", new Object[] { this.className, paramString });
    if (!find(paramString, paramInsnList, paramCollection, this.target))
      return find(paramString, paramInsnList, paramCollection, this.permissiveTarget); 
    return true;
  }
  
  protected boolean find(String paramString, InsnList paramInsnList, Collection<AbstractInsnNode> paramCollection, MemberInfo paramMemberInfo) {
    if (paramMemberInfo == null)
      return false; 
    byte b = 0;
    boolean bool = false;
    ListIterator<AbstractInsnNode> listIterator = paramInsnList.iterator();
    while (listIterator.hasNext()) {
      AbstractInsnNode abstractInsnNode = listIterator.next();
      if (matchesInsn(abstractInsnNode)) {
        MemberInfo memberInfo = new MemberInfo(abstractInsnNode);
        log("{} is considering insn {}", new Object[] { this.className, memberInfo });
        if (paramMemberInfo.matches(memberInfo.owner, memberInfo.name, memberInfo.desc)) {
          log("{} > found a matching insn, checking preconditions...", new Object[] { this.className });
          if (matchesInsn(memberInfo, b)) {
            log("{} > > > found a matching insn at ordinal {}", new Object[] { this.className, Integer.valueOf(b) });
            bool |= addInsn(paramInsnList, paramCollection, abstractInsnNode);
            if (this.ordinal == b)
              break; 
          } 
          b++;
        } 
      } 
      inspectInsn(paramString, paramInsnList, abstractInsnNode);
    } 
    return bool;
  }
  
  protected boolean addInsn(InsnList paramInsnList, Collection<AbstractInsnNode> paramCollection, AbstractInsnNode paramAbstractInsnNode) {
    paramCollection.add(paramAbstractInsnNode);
    return true;
  }
  
  protected boolean matchesInsn(AbstractInsnNode paramAbstractInsnNode) {
    return paramAbstractInsnNode instanceof org.spongepowered.asm.lib.tree.MethodInsnNode;
  }
  
  protected void inspectInsn(String paramString, InsnList paramInsnList, AbstractInsnNode paramAbstractInsnNode) {}
  
  protected boolean matchesInsn(MemberInfo paramMemberInfo, int paramInt) {
    log("{} > > comparing target ordinal {} with current ordinal {}", new Object[] { this.className, Integer.valueOf(this.ordinal), Integer.valueOf(paramInt) });
    return (this.ordinal == -1 || this.ordinal == paramInt);
  }
  
  protected void log(String paramString, Object... paramVarArgs) {
    if (this.log)
      this.logger.info(paramString, paramVarArgs); 
  }
}
