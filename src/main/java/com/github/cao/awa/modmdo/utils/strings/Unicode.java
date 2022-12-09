package com.github.cao.awa.modmdo.utils.strings;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.action.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unicode {
    private static final StringBuilder sb = new StringBuilder();
    private static final Pattern p = Pattern.compile("\\\\u[\\da-f]{4}");

    public static String stringToUnicode(String s){
        sb.setLength(0);
        StringBuilder builder = new StringBuilder();
        Do.letForUp(s.length(), integer -> {
            sb.append("\\u");
            builder.setLength(0);
            builder.append(Integer.toHexString(s.charAt(integer)).toLowerCase());
            while(builder.length() < 4){
                builder.insert(0, 0);
            }
            sb.append(builder);
        });
        return sb.toString();
    }

    public static String unicodeToString(String s){
        s = s.toLowerCase();
        sb.setLength(0);
        Matcher m = p.matcher(s);
        while(m.find()){
            sb.append((char)Integer.parseInt(m.group().substring(2), 16));
        }
        return sb.toString();
    }
}
