package com.github.cao.awa.modmdo.utils.strings;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.action.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unicode {
    private static final StringBuilder sb = new StringBuilder();
    private static final Pattern p = Pattern.compile("\\\\u[\\da-f]{4}");

    public static String stringToUnicode(String s){
        sb.setLength(0);
        StringBuilder tmp = new StringBuilder();
        Do.letForUp(s.length(), integer -> {
            sb.append("\\u");
            tmp.setLength(0);
            tmp.append(Integer.toHexString(s.charAt(integer)).toLowerCase());
            while(tmp.length() < 4){
                tmp.insert(0, 0);
            }
            sb.append(tmp);
        });
//        for(int i = 0; i < s.length(); i++){
//            sb.append("\\u");
//            tmp.setLength(0);
//            tmp.append(Integer.toHexString(s.charAt(i)).toLowerCase());
//            while(tmp.length() < 4){
//                tmp.insert(0, 0);
//            }
//            sb.append(tmp);
//        }
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
