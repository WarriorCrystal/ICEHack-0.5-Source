package me.fluffycq.icehack.events;

import net.minecraft.network.Packet;

public class PacketEvent extends ICEEvent {
  public PacketEvent(Packet paramPacket) {
    this.packet = paramPacket;
  }
  
  public Packet getPacket() {
    return this.packet;
  }
  
  public static class Send extends PacketEvent {
    static {
    
    }
    
    public Send(Packet param1Packet) {
      super(param1Packet);
    }
  }
  
  public static class Receive extends PacketEvent {
    public Receive(Packet param1Packet) {
      super(param1Packet);
    }
    
    static {
    
    }
  }
}
