package com.github.zhuaidadaya.modMdo.simple.vec;

public class Speed {
    public static double parseSpeed(XYZ before, XYZ after, int keepPlaces) {
        double oldX = before.x;
        double oldY = before.y;
        double oldZ = before.z;
        double newX = after.x;
        double newY = after.y;
        double newZ = after.z;

        double speed = 0;
        String s;

        speed += oldX > newX ? oldX - newX : newX - oldX;
        speed += oldY > newY ? oldY - newY : newY - oldY;
        speed += oldZ > newZ ? oldZ - newZ : newZ - oldZ;

        s = String.valueOf(speed);

        try {
            if(keepPlaces != - 1)
                speed = Double.parseDouble(s.substring(0, s.indexOf(".") + 1 + keepPlaces));
        } catch (Exception e) {

        }

        return speed;
    }

    public static double parseSpeed(XYZ before, XYZ after) {
        return parseSpeed(before, after, - 1);
    }
}
