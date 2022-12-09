package com.github.cao.awa.modmdo.simple.vec;

import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.util.math.*;
import org.json.*;

public class XYZ extends Storable implements Cloneable {
    public double x;
    public double y;
    public double z;

    public XYZ(double X, double Y, double Z) {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    public XYZ(XYZ xyz) {
        this.x = xyz.x;
        this.y = xyz.y;
        this.z = xyz.z;
    }

    public XYZ(Vec3d vec3d) {
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
    }

    public XYZ(Vec3i vec3i) {
        this.x = vec3i.getX();
        this.y = vec3i.getY();
        this.z = vec3i.getZ();
    }

    public XYZ(JSONObject json) {
        this.x = Double.parseDouble(json.get("x").toString());
        this.y = Double.parseDouble(json.get("y").toString());
        this.z = Double.parseDouble(json.get("z").toString());
    }

    public double getX() {
        return x;
    }

    public String getStringX() {
        return String.valueOf(x);
    }


    public double getY() {
        return y;
    }

    public String getStringY() {
        return String.valueOf(y);
    }


    public double getZ() {
        return z;
    }

    public String getStringZ() {
        return String.valueOf(z);
    }

    public String toString() {
        return "[X=" + x + ", Y=" + y + ", Z=" + z + "]";
    }

    public String toString(int keepPlaces) {
        String x = String.valueOf(this.x);
        String y = String.valueOf(this.y);
        String z = String.valueOf(this.z);
        try {
            x = x.substring(0, x.indexOf(".") + 1 + keepPlaces);
        } catch (Exception e) {

        }
        try {
            y = y.substring(0, y.indexOf(".") + 1 + keepPlaces);
        } catch (Exception e) {

        }
        try {
            z = z.substring(0, z.indexOf(".") + 1 + keepPlaces);
        } catch (Exception e) {

        }
        return "[X=" + x + ", Y=" + y + ", Z=" + z + "]";
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(x), String.valueOf(y), String.valueOf(z)};
    }

    public double[] toArray() {
        return new double[]{x, y, z};
    }

    public XYZ multiplyEach(double multiply) {
        x *= multiply;
        y *= multiply;
        z *= multiply;
        return this;
    }


    public XYZ multiplyXZ(double multiplyX, double multiplyZ) {
        x *= multiplyX;
        z *= multiplyZ;
        return this;
    }

    public XYZ multiplyY(double multiply) {
        y *= multiply;
        return this;
    }

    public XYZ divideEach(double divide) {
        x /= divide;
        y /= divide;
        z /= divide;
        return this;
    }


    public XYZ divideXZ(double divideX, double divideZ) {
        x /= divideX;
        z /= divideZ;
        return this;
    }

    public XYZ divideY(double divide) {
        y /= divide;
        return this;
    }

    public XYZ getIntegerXYZ() {
        int x = (int) getX();
        int y = (int) getY();
        int z = (int) getZ();
        return new XYZ(x, y, z);
    }

    public XYZ clone() {
        try {
            return (XYZ) super.clone();
        } catch (CloneNotSupportedException e) {
            return new XYZ(this);
        }
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("x", SharedVariables.FRACTION_DIGITS_2.format(x)).put("y", SharedVariables.FRACTION_DIGITS_2.format(y)).put("z", SharedVariables.FRACTION_DIGITS_2.format(z));
    }

    public boolean equal(XYZ xyz) {
        boolean b = xyz.x == x;
        b = b && xyz.y == y;
        b = b && xyz.z == z;
        return b;
    }
}

