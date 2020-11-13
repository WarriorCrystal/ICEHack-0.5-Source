package javassist.util;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HotSwapper {
  private static final String TRIGGER_NAME = HotSwapper1.class.getName();
  
  public HotSwapper(int paramInt) throws IOException, IllegalConnectorArgumentsException {
    this(Integer.toString(paramInt));
  }
  
  private VirtualMachine jvm = null;
  
  private MethodEntryRequest request = null;
  
  private Map newClassFiles = null;
  
  private HotSwapper1 trigger = new HotSwapper1();
  
  private static final String HOST_NAME = "localhost";
  
  public HotSwapper(String paramString) throws IOException, IllegalConnectorArgumentsException {
    AttachingConnector attachingConnector = (AttachingConnector)findConnector("com.sun.jdi.SocketAttach");
    Map map = attachingConnector.defaultArguments();
    ((Connector.Argument)map.get("hostname")).setValue("localhost");
    ((Connector.Argument)map.get("port")).setValue(paramString);
    this.jvm = attachingConnector.attach(map);
    EventRequestManager eventRequestManager = this.jvm.eventRequestManager();
    this.request = methodEntryRequests(eventRequestManager, TRIGGER_NAME);
  }
  
  private Connector findConnector(String paramString) throws IOException {
    List list = Bootstrap.virtualMachineManager().allConnectors();
    Iterator<Connector> iterator = list.iterator();
    while (iterator.hasNext()) {
      Connector connector = iterator.next();
      if (connector.name().equals(paramString))
        return connector; 
    } 
    throw new IOException("Not found: " + paramString);
  }
  
  private static MethodEntryRequest methodEntryRequests(EventRequestManager paramEventRequestManager, String paramString) {
    MethodEntryRequest methodEntryRequest = paramEventRequestManager.createMethodEntryRequest();
    methodEntryRequest.addClassFilter(paramString);
    methodEntryRequest.setSuspendPolicy(1);
    return methodEntryRequest;
  }
  
  private void deleteEventRequest(EventRequestManager paramEventRequestManager, MethodEntryRequest paramMethodEntryRequest) {
    paramEventRequestManager.deleteEventRequest((EventRequest)paramMethodEntryRequest);
  }
  
  public void reload(String paramString, byte[] paramArrayOfbyte) {
    ReferenceType referenceType = toRefType(paramString);
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(referenceType, paramArrayOfbyte);
    reload2(hashMap, paramString);
  }
  
  public void reload(Map paramMap) {
    Set set = paramMap.entrySet();
    Iterator<Map.Entry> iterator = set.iterator();
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    String str = null;
    while (iterator.hasNext()) {
      Map.Entry entry = iterator.next();
      str = (String)entry.getKey();
      hashMap.put(toRefType(str), entry.getValue());
    } 
    if (str != null)
      reload2(hashMap, str + " etc."); 
  }
  
  private ReferenceType toRefType(String paramString) {
    List<ReferenceType> list = this.jvm.classesByName(paramString);
    if (list == null || list.isEmpty())
      throw new RuntimeException("no such class: " + paramString); 
    return list.get(0);
  }
  
  private void reload2(Map paramMap, String paramString) {
    synchronized (this.trigger) {
      startDaemon();
      this.newClassFiles = paramMap;
      this.request.enable();
      this.trigger.doSwap();
      this.request.disable();
      Map map = this.newClassFiles;
      if (map != null) {
        this.newClassFiles = null;
        throw new RuntimeException("failed to reload: " + paramString);
      } 
    } 
  }
  
  private void startDaemon() {
    (new Thread() {
        private void errorMsg(Throwable param1Throwable) {
          System.err.print("Exception in thread \"HotSwap\" ");
          param1Throwable.printStackTrace(System.err);
        }
        
        public void run() {
          EventSet eventSet = null;
          try {
            eventSet = HotSwapper.this.waitEvent();
            EventIterator eventIterator = eventSet.eventIterator();
            while (eventIterator.hasNext()) {
              Event event = eventIterator.nextEvent();
              if (event instanceof com.sun.jdi.event.MethodEntryEvent) {
                HotSwapper.this.hotswap();
                break;
              } 
            } 
          } catch (Throwable throwable) {
            errorMsg(throwable);
          } 
          try {
            if (eventSet != null)
              eventSet.resume(); 
          } catch (Throwable throwable) {
            errorMsg(throwable);
          } 
        }
      }).start();
  }
  
  EventSet waitEvent() throws InterruptedException {
    EventQueue eventQueue = this.jvm.eventQueue();
    return eventQueue.remove();
  }
  
  void hotswap() {
    Map map = this.newClassFiles;
    this.jvm.redefineClasses(map);
    this.newClassFiles = null;
  }
}
