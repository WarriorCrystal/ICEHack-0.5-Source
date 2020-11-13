package club.minnced.discord.rpc;

import com.sun.jna.Callback;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordEventHandlers extends Structure {
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DiscordEventHandlers))
      return false; 
    DiscordEventHandlers discordEventHandlers = (DiscordEventHandlers)paramObject;
    return (Objects.equals(this.ready, discordEventHandlers.ready) && Objects.equals(this.disconnected, discordEventHandlers.disconnected) && Objects.equals(this.errored, discordEventHandlers.errored) && Objects.equals(this.joinGame, discordEventHandlers.joinGame) && Objects.equals(this.spectateGame, discordEventHandlers.spectateGame) && Objects.equals(this.joinRequest, discordEventHandlers.joinRequest));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.ready, this.disconnected, this.errored, this.joinGame, this.spectateGame, this.joinRequest });
  }
  
  protected List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
  
  private static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList(new String[] { "ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest" }));
  
  public OnReady ready;
  
  public OnStatus disconnected;
  
  public OnStatus errored;
  
  public OnGameUpdate joinGame;
  
  public OnGameUpdate spectateGame;
  
  public OnJoinRequest joinRequest;
  
  public static interface OnJoinRequest extends Callback {
    void accept(DiscordUser param1DiscordUser);
  }
  
  public static interface OnGameUpdate extends Callback {
    void accept(String param1String);
  }
  
  public static interface OnStatus extends Callback {
    void accept(int param1Int, String param1String);
  }
  
  public static interface OnReady extends Callback {
    void accept(DiscordUser param1DiscordUser);
  }
}
