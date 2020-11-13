package club.minnced.discord.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordRichPresence extends Structure {
  public DiscordRichPresence(String paramString) {
    setStringEncoding(paramString);
  }
  
  public DiscordRichPresence() {
    this("UTF-8");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DiscordRichPresence))
      return false; 
    DiscordRichPresence discordRichPresence = (DiscordRichPresence)paramObject;
    return (this.startTimestamp == discordRichPresence.startTimestamp && this.endTimestamp == discordRichPresence.endTimestamp && this.partySize == discordRichPresence.partySize && this.partyMax == discordRichPresence.partyMax && this.instance == discordRichPresence.instance && Objects.equals(this.state, discordRichPresence.state) && Objects.equals(this.details, discordRichPresence.details) && Objects.equals(this.largeImageKey, discordRichPresence.largeImageKey) && Objects.equals(this.largeImageText, discordRichPresence.largeImageText) && Objects.equals(this.smallImageKey, discordRichPresence.smallImageKey) && Objects.equals(this.smallImageText, discordRichPresence.smallImageText) && Objects.equals(this.partyId, discordRichPresence.partyId) && Objects.equals(this.matchSecret, discordRichPresence.matchSecret) && Objects.equals(this.joinSecret, discordRichPresence.joinSecret) && Objects.equals(this.spectateSecret, discordRichPresence.spectateSecret));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { 
          this.state, this.details, Long.valueOf(this.startTimestamp), Long.valueOf(this.endTimestamp), this.largeImageKey, this.largeImageText, this.smallImageKey, this.smallImageText, this.partyId, Integer.valueOf(this.partySize), 
          Integer.valueOf(this.partyMax), this.matchSecret, this.joinSecret, this.spectateSecret, Byte.valueOf(this.instance) });
  }
  
  protected List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
  
  private static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList(new String[] { 
          "state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", 
          "partyMax", "matchSecret", "joinSecret", "spectateSecret", "instance" }));
  
  public String state;
  
  public String details;
  
  public long startTimestamp;
  
  public long endTimestamp;
  
  public String largeImageKey;
  
  public String largeImageText;
  
  public String smallImageKey;
  
  public String smallImageText;
  
  public String partyId;
  
  public int partySize;
  
  public int partyMax;
  
  public String matchSecret;
  
  public String joinSecret;
  
  public String spectateSecret;
  
  public byte instance;
}
