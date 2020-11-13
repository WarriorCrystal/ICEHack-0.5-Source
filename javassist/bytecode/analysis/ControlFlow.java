package javassist.bytecode.analysis;

import java.util.ArrayList;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.stackmap.BasicBlock;

public class ControlFlow {
  private CtClass clazz;
  
  private MethodInfo methodInfo;
  
  private Block[] basicBlocks;
  
  private Frame[] frames;
  
  public ControlFlow(CtMethod paramCtMethod) throws BadBytecode {
    this(paramCtMethod.getDeclaringClass(), paramCtMethod.getMethodInfo2());
  }
  
  public ControlFlow(CtClass paramCtClass, MethodInfo paramMethodInfo) throws BadBytecode {
    this.clazz = paramCtClass;
    this.methodInfo = paramMethodInfo;
    this.frames = null;
    this
      
      .basicBlocks = (Block[])(new BasicBlock.Maker() {
        protected BasicBlock makeBlock(int param1Int) {
          return new ControlFlow.Block(param1Int, ControlFlow.this.methodInfo);
        }
        
        protected BasicBlock[] makeArray(int param1Int) {
          return (BasicBlock[])new ControlFlow.Block[param1Int];
        }
      }).make(paramMethodInfo);
    if (this.basicBlocks == null)
      this.basicBlocks = new Block[0]; 
    int i = this.basicBlocks.length;
    int[] arrayOfInt = new int[i];
    byte b;
    for (b = 0; b < i; b++) {
      Block block = this.basicBlocks[b];
      block.index = b;
      block.entrances = new Block[block.incomings()];
      arrayOfInt[b] = 0;
    } 
    for (b = 0; b < i; b++) {
      Block block = this.basicBlocks[b];
      for (byte b1 = 0; b1 < block.exits(); b1++) {
        Block block1 = block.exit(b1);
        arrayOfInt[block1.index] = arrayOfInt[block1.index] + 1;
        block1.entrances[arrayOfInt[block1.index]] = block;
      } 
      Catcher[] arrayOfCatcher = block.catchers();
      for (byte b2 = 0; b2 < arrayOfCatcher.length; b2++) {
        Block block1 = (arrayOfCatcher[b2]).node;
        arrayOfInt[block1.index] = arrayOfInt[block1.index] + 1;
        block1.entrances[arrayOfInt[block1.index]] = block;
      } 
    } 
  }
  
  public Block[] basicBlocks() {
    return this.basicBlocks;
  }
  
  public Frame frameAt(int paramInt) throws BadBytecode {
    if (this.frames == null)
      this.frames = (new Analyzer()).analyze(this.clazz, this.methodInfo); 
    return this.frames[paramInt];
  }
  
  public Node[] dominatorTree() {
    int i = this.basicBlocks.length;
    if (i == 0)
      return null; 
    Node[] arrayOfNode = new Node[i];
    boolean[] arrayOfBoolean = new boolean[i];
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++) {
      arrayOfNode[b] = new Node(this.basicBlocks[b]);
      arrayOfBoolean[b] = false;
    } 
    Access access = new Access(arrayOfNode) {
        BasicBlock[] exits(ControlFlow.Node param1Node) {
          return param1Node.block.getExit();
        }
        
        BasicBlock[] entrances(ControlFlow.Node param1Node) {
          return (BasicBlock[])param1Node.block.entrances;
        }
      };
    arrayOfNode[0].makeDepth1stTree(null, arrayOfBoolean, 0, arrayOfInt, access);
    while (true) {
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfBoolean[b1] = false; 
      if (!arrayOfNode[0].makeDominatorTree(arrayOfBoolean, arrayOfInt, access)) {
        Node.setChildren(arrayOfNode);
        return arrayOfNode;
      } 
    } 
  }
  
  public Node[] postDominatorTree() {
    int i = this.basicBlocks.length;
    if (i == 0)
      return null; 
    Node[] arrayOfNode = new Node[i];
    boolean[] arrayOfBoolean = new boolean[i];
    int[] arrayOfInt = new int[i];
    for (byte b1 = 0; b1 < i; b1++) {
      arrayOfNode[b1] = new Node(this.basicBlocks[b1]);
      arrayOfBoolean[b1] = false;
    } 
    Access access = new Access(arrayOfNode) {
        BasicBlock[] exits(ControlFlow.Node param1Node) {
          return (BasicBlock[])param1Node.block.entrances;
        }
        
        BasicBlock[] entrances(ControlFlow.Node param1Node) {
          return param1Node.block.getExit();
        }
      };
    int j = 0;
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      if ((arrayOfNode[b2]).block.exits() == 0)
        j = arrayOfNode[b2].makeDepth1stTree(null, arrayOfBoolean, j, arrayOfInt, access); 
    } 
    do {
      byte b;
      for (b = 0; b < i; b++)
        arrayOfBoolean[b] = false; 
      b2 = 0;
      for (b = 0; b < i; b++) {
        if ((arrayOfNode[b]).block.exits() == 0 && 
          arrayOfNode[b].makeDominatorTree(arrayOfBoolean, arrayOfInt, access))
          b2 = 1; 
      } 
    } while (b2 != 0);
    Node.setChildren(arrayOfNode);
    return arrayOfNode;
  }
  
  public static class Block extends BasicBlock {
    public Object clientData = null;
    
    int index;
    
    MethodInfo method;
    
    Block[] entrances;
    
    Block(int param1Int, MethodInfo param1MethodInfo) {
      super(param1Int);
      this.method = param1MethodInfo;
    }
    
    protected void toString2(StringBuffer param1StringBuffer) {
      super.toString2(param1StringBuffer);
      param1StringBuffer.append(", incoming{");
      for (byte b = 0; b < this.entrances.length; b++)
        param1StringBuffer.append((this.entrances[b]).position).append(", "); 
      param1StringBuffer.append("}");
    }
    
    BasicBlock[] getExit() {
      return this.exit;
    }
    
    public int index() {
      return this.index;
    }
    
    public int position() {
      return this.position;
    }
    
    public int length() {
      return this.length;
    }
    
    public int incomings() {
      return this.incoming;
    }
    
    public Block incoming(int param1Int) {
      return this.entrances[param1Int];
    }
    
    public int exits() {
      return (this.exit == null) ? 0 : this.exit.length;
    }
    
    public Block exit(int param1Int) {
      return (Block)this.exit[param1Int];
    }
    
    public ControlFlow.Catcher[] catchers() {
      ArrayList<ControlFlow.Catcher> arrayList = new ArrayList();
      BasicBlock.Catch catch_ = this.toCatch;
      while (catch_ != null) {
        arrayList.add(new ControlFlow.Catcher(catch_));
        catch_ = catch_.next;
      } 
      return arrayList.<ControlFlow.Catcher>toArray(new ControlFlow.Catcher[arrayList.size()]);
    }
  }
  
  static abstract class Access {
    ControlFlow.Node[] all;
    
    Access(ControlFlow.Node[] param1ArrayOfNode) {
      this.all = param1ArrayOfNode;
    }
    
    ControlFlow.Node node(BasicBlock param1BasicBlock) {
      return this.all[((ControlFlow.Block)param1BasicBlock).index];
    }
    
    abstract BasicBlock[] exits(ControlFlow.Node param1Node);
    
    abstract BasicBlock[] entrances(ControlFlow.Node param1Node);
  }
  
  public static class Node {
    private ControlFlow.Block block;
    
    private Node parent;
    
    private Node[] children;
    
    Node(ControlFlow.Block param1Block) {
      this.block = param1Block;
      this.parent = null;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Node[pos=").append(block().position());
      stringBuffer.append(", parent=");
      stringBuffer.append((this.parent == null) ? "*" : Integer.toString(this.parent.block().position()));
      stringBuffer.append(", children{");
      for (byte b = 0; b < this.children.length; b++)
        stringBuffer.append(this.children[b].block().position()).append(", "); 
      stringBuffer.append("}]");
      return stringBuffer.toString();
    }
    
    public ControlFlow.Block block() {
      return this.block;
    }
    
    public Node parent() {
      return this.parent;
    }
    
    public int children() {
      return this.children.length;
    }
    
    public Node child(int param1Int) {
      return this.children[param1Int];
    }
    
    int makeDepth1stTree(Node param1Node, boolean[] param1ArrayOfboolean, int param1Int, int[] param1ArrayOfint, ControlFlow.Access param1Access) {
      int i = this.block.index;
      if (param1ArrayOfboolean[i])
        return param1Int; 
      param1ArrayOfboolean[i] = true;
      this.parent = param1Node;
      BasicBlock[] arrayOfBasicBlock = param1Access.exits(this);
      if (arrayOfBasicBlock != null)
        for (byte b = 0; b < arrayOfBasicBlock.length; b++) {
          Node node = param1Access.node(arrayOfBasicBlock[b]);
          param1Int = node.makeDepth1stTree(this, param1ArrayOfboolean, param1Int, param1ArrayOfint, param1Access);
        }  
      param1ArrayOfint[i] = param1Int++;
      return param1Int;
    }
    
    boolean makeDominatorTree(boolean[] param1ArrayOfboolean, int[] param1ArrayOfint, ControlFlow.Access param1Access) {
      int i = this.block.index;
      if (param1ArrayOfboolean[i])
        return false; 
      param1ArrayOfboolean[i] = true;
      boolean bool = false;
      BasicBlock[] arrayOfBasicBlock1 = param1Access.exits(this);
      if (arrayOfBasicBlock1 != null)
        for (byte b = 0; b < arrayOfBasicBlock1.length; b++) {
          Node node = param1Access.node(arrayOfBasicBlock1[b]);
          if (node.makeDominatorTree(param1ArrayOfboolean, param1ArrayOfint, param1Access))
            bool = true; 
        }  
      BasicBlock[] arrayOfBasicBlock2 = param1Access.entrances(this);
      if (arrayOfBasicBlock2 != null)
        for (byte b = 0; b < arrayOfBasicBlock2.length; b++) {
          if (this.parent != null) {
            Node node = getAncestor(this.parent, param1Access.node(arrayOfBasicBlock2[b]), param1ArrayOfint);
            if (node != this.parent) {
              this.parent = node;
              bool = true;
            } 
          } 
        }  
      return bool;
    }
    
    private static Node getAncestor(Node param1Node1, Node param1Node2, int[] param1ArrayOfint) {
      while (param1Node1 != param1Node2) {
        if (param1ArrayOfint[param1Node1.block.index] < param1ArrayOfint[param1Node2.block.index]) {
          param1Node1 = param1Node1.parent;
        } else {
          param1Node2 = param1Node2.parent;
        } 
        if (param1Node1 == null || param1Node2 == null)
          return null; 
      } 
      return param1Node1;
    }
    
    private static void setChildren(Node[] param1ArrayOfNode) {
      int i = param1ArrayOfNode.length;
      int[] arrayOfInt = new int[i];
      byte b;
      for (b = 0; b < i; b++)
        arrayOfInt[b] = 0; 
      for (b = 0; b < i; b++) {
        Node node = (param1ArrayOfNode[b]).parent;
        if (node != null)
          arrayOfInt[node.block.index] = arrayOfInt[node.block.index] + 1; 
      } 
      for (b = 0; b < i; b++)
        (param1ArrayOfNode[b]).children = new Node[arrayOfInt[b]]; 
      for (b = 0; b < i; b++)
        arrayOfInt[b] = 0; 
      for (b = 0; b < i; b++) {
        Node node1 = param1ArrayOfNode[b];
        Node node2 = node1.parent;
        if (node2 != null) {
          arrayOfInt[node2.block.index] = arrayOfInt[node2.block.index] + 1;
          node2.children[arrayOfInt[node2.block.index]] = node1;
        } 
      } 
    }
  }
  
  public static class Catcher {
    private ControlFlow.Block node;
    
    private int typeIndex;
    
    Catcher(BasicBlock.Catch param1Catch) {
      this.node = (ControlFlow.Block)param1Catch.body;
      this.typeIndex = param1Catch.typeIndex;
    }
    
    public ControlFlow.Block block() {
      return this.node;
    }
    
    public String type() {
      if (this.typeIndex == 0)
        return "java.lang.Throwable"; 
      return this.node.method.getConstPool().getClassInfo(this.typeIndex);
    }
  }
}
