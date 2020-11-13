package me.fluffycq.icehack.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

public class ProjUtil {
  public Vector3D[] getFrustum(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8) {
    double d1 = 2.0D * Math.tan(Math.toRadians(paramDouble6 / 2.0D)) * paramDouble7;
    double d2 = d1 * paramDouble8;
    Vector3D vector3D1 = getRotationVector(paramDouble4, paramDouble5).snormalize();
    Vector3D vector3D2 = getRotationVector(paramDouble4, paramDouble5 - 90.0D).snormalize();
    Vector3D vector3D3 = getRotationVector(paramDouble4 + 90.0D, 0.0D).snormalize();
    Vector3D vector3D4 = new Vector3D(paramDouble1, paramDouble2, paramDouble3);
    Vector3D vector3D5 = vector3D1.add(vector3D4);
    Vector3D vector3D6 = new Vector3D(vector3D5.x * paramDouble7, vector3D5.y * paramDouble7, vector3D5.z * paramDouble7);
    Vector3D vector3D7 = new Vector3D(vector3D6.x + vector3D2.x * d1 / 2.0D - vector3D3.x * d2 / 2.0D, vector3D6.y + vector3D2.y * d1 / 2.0D - vector3D3.y * d2 / 2.0D, vector3D6.z + vector3D2.z * d1 / 2.0D - vector3D3.z * d2 / 2.0D);
    Vector3D vector3D8 = new Vector3D(vector3D6.x - vector3D2.x * d1 / 2.0D - vector3D3.x * d2 / 2.0D, vector3D6.y - vector3D2.y * d1 / 2.0D - vector3D3.y * d2 / 2.0D, vector3D6.z - vector3D2.z * d1 / 2.0D - vector3D3.z * d2 / 2.0D);
    Vector3D vector3D9 = new Vector3D(vector3D6.x + vector3D2.x * d1 / 2.0D + vector3D3.x * d2 / 2.0D, vector3D6.y + vector3D2.y * d1 / 2.0D + vector3D3.y * d2 / 2.0D, vector3D6.z + vector3D2.z * d1 / 2.0D + vector3D3.z * d2 / 2.0D);
    Vector3D vector3D10 = new Vector3D(vector3D6.x - vector3D2.x * d1 / 2.0D + vector3D3.x * d2 / 2.0D, vector3D6.y - vector3D2.y * d1 / 2.0D + vector3D3.y * d2 / 2.0D, vector3D6.z - vector3D2.z * d1 / 2.0D + vector3D3.z * d2 / 2.0D);
    return new Vector3D[] { vector3D7, vector3D8, vector3D10, vector3D9 };
  }
  
  public Vector3D getLookVector() {
    return this.lookVec;
  }
  
  public Vector3D getRotationVector(double paramDouble1, double paramDouble2) {
    double d1 = Math.cos(-paramDouble1 * 0.01745329238474369D - Math.PI);
    double d2 = Math.sin(-paramDouble1 * 0.01745329238474369D - Math.PI);
    double d3 = -Math.cos(-paramDouble2 * 0.01745329238474369D);
    double d4 = Math.sin(-paramDouble2 * 0.01745329238474369D);
    return new Vector3D(d2 * d3, d4, d1 * d3);
  }
  
  public boolean[] doFrustumCheck(Vector3D[] paramArrayOfVector3D, Vector3D paramVector3D, double paramDouble1, double paramDouble2, double paramDouble3) {
    Vector3D vector3D = new Vector3D(paramDouble1, paramDouble2, paramDouble3);
    boolean bool1 = crossPlane(new Vector3D[] { paramVector3D, paramArrayOfVector3D[3], paramArrayOfVector3D[0] }, vector3D);
    boolean bool2 = crossPlane(new Vector3D[] { paramVector3D, paramArrayOfVector3D[0], paramArrayOfVector3D[1] }, vector3D);
    boolean bool3 = crossPlane(new Vector3D[] { paramVector3D, paramArrayOfVector3D[1], paramArrayOfVector3D[2] }, vector3D);
    boolean bool4 = crossPlane(new Vector3D[] { paramVector3D, paramArrayOfVector3D[2], paramArrayOfVector3D[3] }, vector3D);
    return new boolean[] { bool1, bool2, bool3, bool4 };
  }
  
  public void updateMatrices(IntBuffer paramIntBuffer, FloatBuffer paramFloatBuffer1, FloatBuffer paramFloatBuffer2, double paramDouble1, double paramDouble2) {
    this.viewport = paramIntBuffer;
    this.modelview = paramFloatBuffer1;
    this.projection = paramFloatBuffer2;
    this.widthScale = paramDouble1;
    this.heightScale = paramDouble2;
    float f = (float)Math.toDegrees(Math.atan(1.0D / this.projection.get(5)) * 2.0D);
    this.fovY = f;
    this.displayWidth = this.viewport.get(2);
    this.displayHeight = this.viewport.get(3);
    this.fovX = (float)Math.toDegrees(2.0D * Math.atan(this.displayWidth / this.displayHeight * Math.tan(Math.toRadians(this.fovY) / 2.0D)));
    Vector3D vector3D1 = new Vector3D(this.modelview.get(0), this.modelview.get(1), this.modelview.get(2));
    Vector3D vector3D2 = new Vector3D(this.modelview.get(4), this.modelview.get(5), this.modelview.get(6));
    Vector3D vector3D3 = new Vector3D(this.modelview.get(8), this.modelview.get(9), this.modelview.get(10));
    Vector3D vector3D4 = new Vector3D(0.0D, 1.0D, 0.0D);
    Vector3D vector3D5 = new Vector3D(1.0D, 0.0D, 0.0D);
    double d1 = Math.toDegrees(Math.atan2(vector3D5.cross(vector3D1).length(), vector3D5.dot(vector3D1))) + 180.0D;
    if (vector3D3.x < 0.0D)
      d1 = 360.0D - d1; 
    double d2 = 0.0D;
    if ((-vector3D3.y > 0.0D && d1 >= 90.0D && d1 < 270.0D) || (vector3D3.y > 0.0D && (d1 < 90.0D || d1 >= 270.0D))) {
      d2 = Math.toDegrees(Math.atan2(vector3D4.cross(vector3D2).length(), vector3D4.dot(vector3D2)));
    } else {
      d2 = -Math.toDegrees(Math.atan2(vector3D4.cross(vector3D2).length(), vector3D4.dot(vector3D2)));
    } 
    this.lookVec = getRotationVector(d1, d2);
    Matrix4f matrix4f = new Matrix4f();
    matrix4f.load(this.modelview.asReadOnlyBuffer());
    matrix4f.invert();
    this.frustumPos = new Vector3D(matrix4f.m30, matrix4f.m31, matrix4f.m32);
    this.frustum = getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, d1, d2, f, 1.0D, this.displayWidth / this.displayHeight);
    this.invFrustum = getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, d1 - 180.0D, -d2, f, 1.0D, this.displayWidth / this.displayHeight);
    this.viewVec = getRotationVector(d1, d2).normalized();
    this.bra = Math.toDegrees(Math.acos(this.displayHeight * paramDouble2 / Math.sqrt(this.displayWidth * paramDouble1 * this.displayWidth * paramDouble1 + this.displayHeight * paramDouble2 * this.displayHeight * paramDouble2)));
    this.bla = 360.0D - this.bra;
    this.tra = this.bla - 180.0D;
    this.tla = this.bra + 180.0D;
    this.rb = new Line(this.displayWidth * this.widthScale, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
    this.tb = new Line(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
    this.lb = new Line(0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
    this.bb = new Line(0.0D, this.displayHeight * this.heightScale, 0.0D, 1.0D, 0.0D, 0.0D);
  }
  
  public float getFovX() {
    return this.fovX;
  }
  
  public boolean crossPlane(Vector3D[] paramArrayOfVector3D, Vector3D paramVector3D) {
    Vector3D vector3D1 = new Vector3D(0.0D, 0.0D, 0.0D);
    Vector3D vector3D2 = paramArrayOfVector3D[1].sub(paramArrayOfVector3D[0]);
    Vector3D vector3D3 = paramArrayOfVector3D[2].sub(paramArrayOfVector3D[0]);
    Vector3D vector3D4 = vector3D2.cross(vector3D3).snormalize();
    double d1 = vector3D1.sub(vector3D4).dot(paramArrayOfVector3D[2]);
    double d2 = vector3D4.dot(paramVector3D) + d1;
    return (d2 >= 0.0D);
  }
  
  public static ProjUtil getInstance() {
    if (instance == null)
      instance = new ProjUtil(); 
    return instance;
  }
  
  public Projection project(double paramDouble1, double paramDouble2, double paramDouble3, ClampMode paramClampMode, boolean paramBoolean) {
    if (this.viewport != null && this.modelview != null && this.projection != null) {
      Vector3D vector3D = new Vector3D(paramDouble1, paramDouble2, paramDouble3);
      boolean[] arrayOfBoolean = doFrustumCheck(this.frustum, this.frustumPos, paramDouble1, paramDouble2, paramDouble3);
      boolean bool = (arrayOfBoolean[0] || arrayOfBoolean[1] || arrayOfBoolean[2] || arrayOfBoolean[3]) ? true : false;
      if (bool) {
        boolean bool1 = (vector3D.sub(this.frustumPos).dot(this.viewVec) <= 0.0D) ? true : false;
        boolean[] arrayOfBoolean1 = doFrustumCheck(this.invFrustum, this.frustumPos, paramDouble1, paramDouble2, paramDouble3);
        boolean bool2 = (arrayOfBoolean1[0] || arrayOfBoolean1[1] || arrayOfBoolean1[2] || arrayOfBoolean1[3]) ? true : false;
        if ((paramBoolean && !bool2) || (bool2 && paramClampMode != ClampMode.NONE)) {
          if ((paramBoolean && !bool2) || (paramClampMode == ClampMode.DIRECT && bool2)) {
            double d1 = 0.0D;
            double d2 = 0.0D;
            if (GLU.gluProject((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, this.modelview, this.projection, this.viewport, this.coords)) {
              if (bool1) {
                d1 = this.displayWidth * this.widthScale - this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0D;
                d2 = this.displayHeight * this.heightScale - (this.displayHeight - this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0D;
              } else {
                d1 = this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0D;
                d2 = (this.displayHeight - this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0D;
              } 
            } else {
              return new Projection(0.0D, 0.0D, Projection.Type.FAIL);
            } 
            Vector3D vector3D1 = (new Vector3D(d1, d2, 0.0D)).snormalize();
            d1 = vector3D1.x;
            d2 = vector3D1.y;
            Line line = new Line(this.displayWidth * this.widthScale / 2.0D, this.displayHeight * this.heightScale / 2.0D, 0.0D, d1, d2, 0.0D);
            double d3 = Math.toDegrees(Math.acos(vector3D1.y / Math.sqrt(vector3D1.x * vector3D1.x + vector3D1.y * vector3D1.y)));
            if (d1 < 0.0D)
              d3 = 360.0D - d3; 
            Vector3D vector3D2 = new Vector3D(0.0D, 0.0D, 0.0D);
            if (d3 >= this.bra && d3 < this.tra) {
              vector3D2 = this.rb.intersect(line);
            } else if (d3 >= this.tra && d3 < this.tla) {
              vector3D2 = this.tb.intersect(line);
            } else if (d3 >= this.tla && d3 < this.bla) {
              vector3D2 = this.lb.intersect(line);
            } else {
              vector3D2 = this.bb.intersect(line);
            } 
            return new Projection(vector3D2.x, vector3D2.y, bool2 ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
          } 
          if (paramClampMode == ClampMode.ORTHOGONAL && bool2) {
            if (GLU.gluProject((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, this.modelview, this.projection, this.viewport, this.coords)) {
              double d1 = this.coords.get(0) * this.widthScale;
              double d2 = (this.displayHeight - this.coords.get(1)) * this.heightScale;
              if (bool1) {
                d1 = this.displayWidth * this.widthScale - d1;
                d2 = this.displayHeight * this.heightScale - d2;
              } 
              if (d1 < 0.0D) {
                d1 = 0.0D;
              } else if (d1 > this.displayWidth * this.widthScale) {
                d1 = this.displayWidth * this.widthScale;
              } 
              if (d2 < 0.0D) {
                d2 = 0.0D;
              } else if (d2 > this.displayHeight * this.heightScale) {
                d2 = this.displayHeight * this.heightScale;
              } 
              return new Projection(d1, d2, bool2 ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
            } 
            return new Projection(0.0D, 0.0D, Projection.Type.FAIL);
          } 
        } else {
          if (GLU.gluProject((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, this.modelview, this.projection, this.viewport, this.coords)) {
            double d1 = this.coords.get(0) * this.widthScale;
            double d2 = (this.displayHeight - this.coords.get(1)) * this.heightScale;
            if (bool1) {
              d1 = this.displayWidth * this.widthScale - d1;
              d2 = this.displayHeight * this.heightScale - d2;
            } 
            return new Projection(d1, d2, bool2 ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
          } 
          return new Projection(0.0D, 0.0D, Projection.Type.FAIL);
        } 
      } else {
        if (GLU.gluProject((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, this.modelview, this.projection, this.viewport, this.coords)) {
          double d1 = this.coords.get(0) * this.widthScale;
          double d2 = (this.displayHeight - this.coords.get(1)) * this.heightScale;
          return new Projection(d1, d2, Projection.Type.INSIDE);
        } 
        return new Projection(0.0D, 0.0D, Projection.Type.FAIL);
      } 
    } 
    return new Projection(0.0D, 0.0D, Projection.Type.FAIL);
  }
  
  public Vector3D[] getFrustum() {
    return this.frustum;
  }
  
  public float getFovY() {
    return this.fovY;
  }
  
  public static class Vector3D {
    public Vector3D ssub(double param1Double1, double param1Double2, double param1Double3) {
      this.x -= param1Double1;
      this.y -= param1Double2;
      this.z -= param1Double3;
      return this;
    }
    
    public Vector3D sub(Vector3D param1Vector3D) {
      return new Vector3D(this.x - param1Vector3D.x, this.y - param1Vector3D.y, this.z - param1Vector3D.z);
    }
    
    public Vector3D snormalize() {
      double d = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.x /= d;
      this.y /= d;
      this.z /= d;
      return this;
    }
    
    public Vector3D smul(double param1Double) {
      this.x *= param1Double;
      this.y *= param1Double;
      this.z *= param1Double;
      return this;
    }
    
    public Vector3D sadd(Vector3D param1Vector3D) {
      this.x += param1Vector3D.x;
      this.y += param1Vector3D.y;
      this.z += param1Vector3D.z;
      return this;
    }
    
    public Vector3D add(double param1Double1, double param1Double2, double param1Double3) {
      return new Vector3D(this.x + param1Double1, this.y + param1Double2, this.z + param1Double3);
    }
    
    public Vector3D sdiv(double param1Double) {
      this.x /= param1Double;
      this.y /= param1Double;
      this.z /= param1Double;
      return this;
    }
    
    public Vector3D sub(double param1Double1, double param1Double2, double param1Double3) {
      return new Vector3D(this.x - param1Double1, this.y - param1Double2, this.z - param1Double3);
    }
    
    public Vector3D mul(double param1Double) {
      return new Vector3D(this.x * param1Double, this.y * param1Double, this.z * param1Double);
    }
    
    public Vector3D sadd(double param1Double1, double param1Double2, double param1Double3) {
      this.x += param1Double1;
      this.y += param1Double2;
      this.z += param1Double3;
      return this;
    }
    
    public Vector3D div(double param1Double) {
      return new Vector3D(this.x / param1Double, this.y / param1Double, this.z / param1Double);
    }
    
    public double dot(Vector3D param1Vector3D) {
      return this.x * param1Vector3D.x + this.y * param1Vector3D.y + this.z * param1Vector3D.z;
    }
    
    public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vector3D add(Vector3D param1Vector3D) {
      return new Vector3D(this.x + param1Vector3D.x, this.y + param1Vector3D.y, this.z + param1Vector3D.z);
    }
    
    public String toString() {
      return String.valueOf((new StringBuilder()).append("(X: ").append(this.x).append(" Y: ").append(this.y).append(" Z: ").append(this.z).append(")"));
    }
    
    public Vector3D(double param1Double1, double param1Double2, double param1Double3) {
      this.x = param1Double1;
      this.y = param1Double2;
      this.z = param1Double3;
    }
    
    public Vector3D scross(Vector3D param1Vector3D) {
      this.x = this.y * param1Vector3D.z - this.z * param1Vector3D.y;
      this.y = this.z * param1Vector3D.x - this.x * param1Vector3D.z;
      this.z = this.x * param1Vector3D.y - this.y * param1Vector3D.x;
      return this;
    }
    
    public Vector3D ssub(Vector3D param1Vector3D) {
      this.x -= param1Vector3D.x;
      this.y -= param1Vector3D.y;
      this.z -= param1Vector3D.z;
      return this;
    }
    
    public Vector3D cross(Vector3D param1Vector3D) {
      return new Vector3D(this.y * param1Vector3D.z - this.z * param1Vector3D.y, this.z * param1Vector3D.x - this.x * param1Vector3D.z, this.x * param1Vector3D.y - this.y * param1Vector3D.x);
    }
    
    public Vector3D normalized() {
      double d = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return new Vector3D(this.x / d, this.y / d, this.z / d);
    }
  }
  
  public static class Line {
    public Line(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      this.sourcePoint.x = param1Double1;
      this.sourcePoint.y = param1Double2;
      this.sourcePoint.z = param1Double3;
      this.direction.x = param1Double4;
      this.direction.y = param1Double5;
      this.direction.z = param1Double6;
    }
    
    private ProjUtil.Vector3D intersectYZ(Line param1Line) {
      double d1 = this.sourcePoint.y;
      double d2 = this.direction.y;
      double d3 = param1Line.sourcePoint.y;
      double d4 = param1Line.direction.y;
      double d5 = this.sourcePoint.z;
      double d6 = this.direction.z;
      double d7 = param1Line.sourcePoint.z;
      double d8 = param1Line.direction.z;
      double d9 = -(d1 * d8 - d3 * d8 - d4 * (d5 - d7));
      double d10 = d2 * d8 - d4 * d6;
      if (d10 == 0.0D)
        return null; 
      double d11 = d9 / d10;
      ProjUtil.Vector3D vector3D = new ProjUtil.Vector3D(0.0D, 0.0D, 0.0D);
      this.sourcePoint.x += this.direction.x * d11;
      this.sourcePoint.y += this.direction.y * d11;
      this.sourcePoint.z += this.direction.z * d11;
      return vector3D;
    }
    
    public ProjUtil.Vector3D intersect(Line param1Line) {
      double d1 = this.sourcePoint.x;
      double d2 = this.direction.x;
      double d3 = param1Line.sourcePoint.x;
      double d4 = param1Line.direction.x;
      double d5 = this.sourcePoint.y;
      double d6 = this.direction.y;
      double d7 = param1Line.sourcePoint.y;
      double d8 = param1Line.direction.y;
      double d9 = -(d1 * d8 - d3 * d8 - d4 * (d5 - d7));
      double d10 = d2 * d8 - d4 * d6;
      if (d10 == 0.0D)
        return intersectXZ(param1Line); 
      double d11 = d9 / d10;
      ProjUtil.Vector3D vector3D = new ProjUtil.Vector3D(0.0D, 0.0D, 0.0D);
      this.sourcePoint.x += this.direction.x * d11;
      this.sourcePoint.y += this.direction.y * d11;
      this.sourcePoint.z += this.direction.z * d11;
      return vector3D;
    }
    
    public ProjUtil.Vector3D intersectPlane(ProjUtil.Vector3D param1Vector3D1, ProjUtil.Vector3D param1Vector3D2) {
      ProjUtil.Vector3D vector3D = new ProjUtil.Vector3D(this.sourcePoint.x, this.sourcePoint.y, this.sourcePoint.z);
      double d = param1Vector3D1.sub(this.sourcePoint).dot(param1Vector3D2) / this.direction.dot(param1Vector3D2);
      vector3D.sadd(this.direction.mul(d));
      return (this.direction.dot(param1Vector3D2) == 0.0D) ? null : vector3D;
    }
    
    private ProjUtil.Vector3D intersectXZ(Line param1Line) {
      double d1 = this.sourcePoint.x;
      double d2 = this.direction.x;
      double d3 = param1Line.sourcePoint.x;
      double d4 = param1Line.direction.x;
      double d5 = this.sourcePoint.z;
      double d6 = this.direction.z;
      double d7 = param1Line.sourcePoint.z;
      double d8 = param1Line.direction.z;
      double d9 = -(d1 * d8 - d3 * d8 - d4 * (d5 - d7));
      double d10 = d2 * d8 - d4 * d6;
      if (d10 == 0.0D)
        return intersectYZ(param1Line); 
      double d11 = d9 / d10;
      ProjUtil.Vector3D vector3D = new ProjUtil.Vector3D(0.0D, 0.0D, 0.0D);
      this.sourcePoint.x += this.direction.x * d11;
      this.sourcePoint.y += this.direction.y * d11;
      this.sourcePoint.z += this.direction.z * d11;
      return vector3D;
    }
  }
  
  public enum Type {
    FAIL, OUTSIDE, INSIDE, INVERTED;
    
    static {
      FAIL = new Type("FAIL", 3);
      $VALUES = new Type[] { INSIDE, OUTSIDE, INVERTED, FAIL };
    }
  }
  
  public enum ClampMode {
    NONE, ORTHOGONAL, DIRECT;
    
    static {
    
    }
  }
  
  public static class Projection {
    public double getY() {
      return this.y;
    }
    
    public Type getType() {
      return this.t;
    }
    
    public double getX() {
      return this.x;
    }
    
    public boolean isType(Type param1Type) {
      return (this.t == param1Type);
    }
    
    public Projection(double param1Double1, double param1Double2, Type param1Type) {
      this.x = param1Double1;
      this.y = param1Double2;
      this.t = param1Type;
    }
    
    public enum Type {
      INSIDE, INVERTED, FAIL, OUTSIDE;
      
      static {
      
      }
    }
  }
}
