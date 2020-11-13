//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class MathUtil {
  public static Vec3d direction(float paramFloat) {
    return new Vec3d(Math.cos(degToRad((paramFloat + 90.0F))), 0.0D, Math.sin(degToRad((paramFloat + 90.0F))));
  }
  
  public static double parabolic(double paramDouble1, double paramDouble2, double paramDouble3) {
    return paramDouble1 + (paramDouble2 - paramDouble1) / paramDouble3;
  }
  
  public static double[] calcIntersection(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    double d1 = paramArrayOfdouble1[3] - paramArrayOfdouble1[1];
    double d2 = paramArrayOfdouble1[0] - paramArrayOfdouble1[2];
    double d3 = d1 * paramArrayOfdouble1[0] + d2 * paramArrayOfdouble1[1];
    double d4 = paramArrayOfdouble2[3] - paramArrayOfdouble2[1];
    double d5 = paramArrayOfdouble2[0] - paramArrayOfdouble2[2];
    double d6 = d4 * paramArrayOfdouble2[0] + d5 * paramArrayOfdouble2[1];
    double d7 = d1 * d5 - d4 * d2;
    return new double[] { (d5 * d3 - d2 * d6) / d7, (d1 * d6 - d4 * d3) / d7 };
  }
  
  public static double degToRad(double paramDouble) {
    return paramDouble * 0.01745329238474369D;
  }
  
  public static Vec3d div(Vec3d paramVec3d1, Vec3d paramVec3d2) {
    return new Vec3d(paramVec3d1.x / paramVec3d2.x, paramVec3d1.y / paramVec3d2.y, paramVec3d1.z / paramVec3d2.z);
  }
  
  public static double radToDeg(double paramDouble) {
    return paramDouble * 57.295780181884766D;
  }
  
  public static float wrap(float paramFloat) {
    paramFloat %= 360.0F;
    if (paramFloat >= 180.0F)
      paramFloat -= 360.0F; 
    if (paramFloat < -180.0F)
      paramFloat += 360.0F; 
    return paramFloat;
  }
  
  public static double map(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5) {
    paramDouble1 = (paramDouble1 - paramDouble2) / (paramDouble3 - paramDouble2);
    return paramDouble4 + paramDouble1 * (paramDouble5 - paramDouble4);
  }
  
  public static Vec3d mult(Vec3d paramVec3d1, Vec3d paramVec3d2) {
    return new Vec3d(paramVec3d1.x * paramVec3d2.x, paramVec3d1.y * paramVec3d2.y, paramVec3d1.z * paramVec3d2.z);
  }
  
  public static Vec3d div(Vec3d paramVec3d, float paramFloat) {
    return new Vec3d(paramVec3d.x / paramFloat, paramVec3d.y / paramFloat, paramVec3d.z / paramFloat);
  }
  
  static {
  
  }
  
  public static double[] directionSpeedNoForward(double paramDouble) {
    Minecraft minecraft = Minecraft.getMinecraft();
    float f1 = 1.0F;
    if (minecraft.gameSettings.keyBindLeft.isPressed() || minecraft.gameSettings.keyBindRight.isPressed() || minecraft.gameSettings.keyBindBack.isPressed() || minecraft.gameSettings.keyBindForward.isPressed())
      f1 = minecraft.player.movementInput.moveForward; 
    float f2 = minecraft.player.movementInput.moveStrafe;
    float f3 = minecraft.player.prevRotationYaw + (minecraft.player.rotationYaw - minecraft.player.prevRotationYaw) * minecraft.getRenderPartialTicks();
    if (f1 != 0.0F) {
      if (f2 > 0.0F) {
        f3 += ((f1 > 0.0F) ? -45 : 45);
      } else if (f2 < 0.0F) {
        f3 += ((f1 > 0.0F) ? 45 : -45);
      } 
      f2 = 0.0F;
      if (f1 > 0.0F) {
        f1 = 1.0F;
      } else if (f1 < 0.0F) {
        f1 = -1.0F;
      } 
    } 
    double d1 = Math.sin(Math.toRadians((f3 + 90.0F)));
    double d2 = Math.cos(Math.toRadians((f3 + 90.0F)));
    double d3 = f1 * paramDouble * d2 + f2 * paramDouble * d1;
    double d4 = f1 * paramDouble * d1 - f2 * paramDouble * d2;
    return new double[] { d3, d4 };
  }
  
  public static Vec3d interpolateEntity(Entity paramEntity, float paramFloat) {
    return new Vec3d(paramEntity.lastTickPosX + (paramEntity.posX - paramEntity.lastTickPosX) * paramFloat, paramEntity.lastTickPosY + (paramEntity.posY - paramEntity.lastTickPosY) * paramFloat, paramEntity.lastTickPosZ + (paramEntity.posZ - paramEntity.lastTickPosZ) * paramFloat);
  }
  
  public static float clamp(float paramFloat1, float paramFloat2, float paramFloat3) {
    if (paramFloat1 <= paramFloat2)
      paramFloat1 = paramFloat2; 
    if (paramFloat1 >= paramFloat3)
      paramFloat1 = paramFloat3; 
    return paramFloat1;
  }
  
  public static double[] directionSpeed(double paramDouble) {
    Minecraft minecraft = Minecraft.getMinecraft();
    float f1 = minecraft.player.movementInput.moveForward;
    float f2 = minecraft.player.movementInput.moveStrafe;
    float f3 = minecraft.player.prevRotationYaw + (minecraft.player.rotationYaw - minecraft.player.prevRotationYaw) * minecraft.getRenderPartialTicks();
    if (f1 != 0.0F) {
      if (f2 > 0.0F) {
        f3 += ((f1 > 0.0F) ? -45 : 45);
      } else if (f2 < 0.0F) {
        f3 += ((f1 > 0.0F) ? 45 : -45);
      } 
      f2 = 0.0F;
      if (f1 > 0.0F) {
        f1 = 1.0F;
      } else if (f1 < 0.0F) {
        f1 = -1.0F;
      } 
    } 
    double d1 = Math.sin(Math.toRadians((f3 + 90.0F)));
    double d2 = Math.cos(Math.toRadians((f3 + 90.0F)));
    double d3 = f1 * paramDouble * d2 + f2 * paramDouble * d1;
    double d4 = f1 * paramDouble * d1 - f2 * paramDouble * d2;
    return new double[] { d3, d4 };
  }
  
  public static double calculateAngle(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    double d = Math.toDegrees(Math.atan2(paramDouble3 - paramDouble1, paramDouble4 - paramDouble2));
    d += Math.ceil(-d / 360.0D) * 360.0D;
    return d;
  }
  
  public static float[] calcAngle(Vec3d paramVec3d1, Vec3d paramVec3d2) {
    double d1 = paramVec3d2.x - paramVec3d1.x;
    double d2 = (paramVec3d2.y - paramVec3d1.y) * -1.0D;
    double d3 = paramVec3d2.z - paramVec3d1.z;
    double d4 = MathHelper.sqrt(d1 * d1 + d3 * d3);
    return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(d3, d1)) - 90.0D), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(d2, d4))) };
  }
  
  public static double getDistance(Vec3d paramVec3d, double paramDouble1, double paramDouble2, double paramDouble3) {
    double d1 = paramVec3d.x - paramDouble1;
    double d2 = paramVec3d.y - paramDouble2;
    double d3 = paramVec3d.z - paramDouble3;
    return MathHelper.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
  }
  
  public static Vec3d mult(Vec3d paramVec3d, float paramFloat) {
    return new Vec3d(paramVec3d.x * paramFloat, paramVec3d.y * paramFloat, paramVec3d.z * paramFloat);
  }
  
  public static double round(double paramDouble, int paramInt) {
    return (paramInt < 0) ? paramDouble : (new BigDecimal(paramDouble)).setScale(paramInt, RoundingMode.HALF_UP).doubleValue();
  }
  
  public static double linear(double paramDouble1, double paramDouble2, double paramDouble3) {
    return (paramDouble1 < paramDouble2 - paramDouble3) ? (paramDouble1 + paramDouble3) : ((paramDouble1 > paramDouble2 + paramDouble3) ? (paramDouble1 - paramDouble3) : paramDouble2);
  }
}
