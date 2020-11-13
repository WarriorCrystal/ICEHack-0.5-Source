package club.minnced.discord.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordUser extends Structure {
  public DiscordUser(String paramString) {
    setStringEncoding(paramString);
  }
  
  public DiscordUser() {
    this("UTF-8");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DiscordUser))
      return false; 
    DiscordUser discordUser = (DiscordUser)paramObject;
    return (Objects.equals(this.userId, discordUser.userId) && Objects.equals(this.username, discordUser.username) && Objects.equals(this.discriminator, discordUser.discriminator) && Objects.equals(this.avatar, discordUser.avatar));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.userId, this.username, this.discriminator, this.avatar });
  }
  
  protected List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
  
  private static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList(new String[] { "userId", "username", "discriminator", "avatar" }));
  
  public String userId;
  
  public String username;
  
  public String discriminator;
  
  public String avatar;
}
