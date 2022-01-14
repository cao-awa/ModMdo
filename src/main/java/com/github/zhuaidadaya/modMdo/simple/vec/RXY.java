package com.github.zhuaidadaya.modMdo.simple.vec;

import net.minecraft.util.math.Vec2f;
import org.json.JSONObject;

public class RXY {
    public double x;
    public double y;

    public RXY(double X, double Y) {
        this.x = X;
        this.y = Y;
    }

    public RXY(RXY rxy) {
        this.x = rxy.getX();
        this.y = rxy.getY();
    }

    public RXY(Vec2f vec2f) {
        this.x = vec2f.x;
        this.y = vec2f.y;
    }

    public RXY(JSONObject json) {
        this.x = Double.parseDouble(json.get("x").toString());
        this.y = Double.parseDouble(json.get("y").toString());
    }

    public String toString(int keepPlaces) {
        String x = String.valueOf(this.x);
        String y = String.valueOf(this.y);
        try {
            x = x.substring(0, x.indexOf(".") + 1 + keepPlaces);
        } catch (Exception e) {

        }
        try {
            y = y.substring(0, y.indexOf(".") + 1 + keepPlaces);
        } catch (Exception e) {

        }
        return "[X=" + x + ", Y=" + y + "]";
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

    public String toString() {
        return "[X=" + x + ", Y=" + y + "]";
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("x", x).put("y", y);
    }

    public boolean equal(RXY xyz) {
        boolean b = xyz.x == x;
        b = b && xyz.y == y;
        return b;
    }
}