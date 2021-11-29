package com.github.zhuaidadaya.modMdo.Commands;

public class XYZ {
    private double X;
    private double Y;
    private double Z;

    public XYZ(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public double getX() {
        return X;
    }

    public String getStringX() {
        return String.valueOf(X);
    }


    public double getY() {
        return Y;
    }

    public String getStringY() {
        return String.valueOf(Y);
    }


    public double getZ() {
        return Z;
    }

    public String getStringZ() {
        return String.valueOf(Z);
    }

    public String toString() {
        return "[X=" + X + ", Y=" + Y + ", Z=" + Z + "]";
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(X), String.valueOf(Y), String.valueOf(Z)};
    }

    public double[] toArray() {
        return new double[]{X, Y, Z};
    }

    public XYZ multiplyEach(double multiply) {
        X *= multiply;
        Y *= multiply;
        Z *= multiply;
        return this;
    }


    public XYZ multiplyXZ(double multiplyX, double multiplyZ) {
        X *= multiplyX;
        Z *= multiplyZ;
        return this;
    }

    public XYZ multiplyY(double multiply) {
        Y *= multiply;
        return this;
    }

    public XYZ divideEach(double divide) {
        X /= divide;
        Y /= divide;
        Z /= divide;
        return this;
    }


    public XYZ divideXZ(double divideX, double divideZ) {
        X /= divideX;
        Z /= divideZ;
        return this;
    }

    public XYZ divideY(double divide) {
        Y /= divide;
        return this;
    }

    public XYZ getIntegerXYZ() {
        int x = (int) getX();
        int y = (int) getY();
        int z = (int) getZ();
        return new XYZ(x, y, z);
    }

    public XYZ clone() {
        return new XYZ(getX(), getY(), getZ());
    }
}

