package com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.builder;

public final class StringBuilderConductor {
    private final StringBuilder builder;

    public StringBuilderConductor(String str) {
        this(new StringBuilder(str));
    }

    public StringBuilderConductor(StringBuilder builder) {
        this.builder = builder;
    }

    public StringBuilderConductor() {
        this(new StringBuilder());
    }

    public int length() {
        return builder.length();
    }

    public int capacity() {
        return builder.capacity();
    }

    public StringBuilderConductor deleteLast() {
        return deleteLast(1);
    }

    public StringBuilderConductor deleteLast(int size) {
        if (length() > size) {
            return delete(builder.length() - size, builder.length());
        }
        return this;
    }

    public StringBuilderConductor delete(int start, int end) {
        builder.delete(start, end);
        return this;
    }

    public StringBuilder builder() {
        return builder;
    }

    public int compareTo(StringBuilder another) {
        return builder.compareTo(another);
    }

    public StringBuilderConductor append(Object obj) {
        append(String.valueOf(obj));
        return this;
    }

    public StringBuilderConductor append(String str) {
        builder.append(str);
        return this;
    }

    public StringBuilderConductor append(StringBuffer sb) {
        builder.append(sb);
        return this;
    }

    public StringBuilderConductor append(CharSequence s) {
        builder.append(s);
        return this;
    }

    public StringBuilderConductor append(CharSequence s, int start, int end) {
        builder.append(s, start, end);
        return this;
    }

    public StringBuilderConductor append(char[] str) {
        builder.append(str);
        return this;
    }

    public StringBuilderConductor append(char[] str, int offset, int len) {
        builder.append(str, offset, len);
        return this;
    }

    public StringBuilderConductor append(boolean b) {
        builder.append(b);

        return this;
    }

    public StringBuilderConductor append(char c) {
        builder.append(c);
        return this;
    }

    public StringBuilderConductor append(int i) {
        builder.append(i);
        return this;
    }

    public StringBuilderConductor append(long lng) {
        builder.append(lng);
        return this;
    }

    public StringBuilderConductor append(float f) {
        builder.append(f);
        return this;
    }

    public StringBuilderConductor append(double d) {
        builder.append(d);
        return this;
    }

    public StringBuilderConductor appendCodePoint(int codePoint) {
        builder.appendCodePoint(codePoint);
        return this;
    }

    public StringBuilderConductor deleteCharAt(int index) {
        builder.deleteCharAt(index);
        return this;
    }

    public StringBuilderConductor replace(int start, int end, String str) {
        builder.replace(start, end, str);
        return this;
    }

    public StringBuilderConductor insert(int index, char[] str, int offset, int len) {
        builder.insert(index, str, offset, len);
        return this;
    }


    public StringBuilderConductor insert(int offset, Object obj) {
        builder.insert(offset, obj);
        return this;
    }

    public StringBuilderConductor insert(int offset, String str) {
        builder.insert(offset, str);
        return this;
    }


    public StringBuilderConductor insert(int offset, char[] str) {
        builder.insert(offset, str);
        return this;
    }


    public StringBuilderConductor insert(int dstOffset, CharSequence s) {
        builder.insert(dstOffset, s);
        return this;
    }

    public StringBuilderConductor insert(int dstOffset, CharSequence s, int start, int end) {
        builder.insert(dstOffset, s, start, end);
        return this;
    }


    public StringBuilderConductor insert(int offset, boolean b) {
        builder.insert(offset, b);
        return this;
    }

    public StringBuilderConductor insert(int offset, char c) {
        builder.insert(offset, c);
        return this;
    }


    public StringBuilderConductor insert(int offset, int i) {
        builder.insert(offset, i);
        return this;
    }


    public StringBuilderConductor insert(int offset, long l) {
        builder.insert(offset, l);
        return this;
    }


    public StringBuilderConductor insert(int offset, float f) {
        builder.insert(offset, f);
        return this;
    }

    public StringBuilderConductor insert(int offset, double d) {
        builder.insert(offset, d);
        return this;
    }


    public int indexOf(String str) {
        return builder.indexOf(str);
    }


    public int indexOf(String str, int fromIndex) {
        return builder.indexOf(str, fromIndex);
    }


    public int lastIndexOf(String str) {
        return builder.lastIndexOf(str);
    }


    public int lastIndexOf(String str, int fromIndex) {
        return builder.lastIndexOf(str, fromIndex);
    }

    public StringBuilderConductor reverse() {
        builder.reverse();
        return this;
    }

    public String toString() {
        return builder.toString();
    }
}
