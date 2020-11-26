package com.zhongyou.meettvapplicaion.utils;

import com.orhanobut.logger.Logger;

/**
 * Created by whatisjava on 16-12-7.
 */

public class UIDUtil {

    public static String generatorUID(String uid) {
        String temp;
        if (uid.length() > 8) {
            temp = uid.substring(0, 8);
        } else {
            temp = uid;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < temp.length(); i++) {
            if (Character.isDigit(temp.charAt(i))) {
                sb.append(temp.charAt(i));
            } else if (Character.isLetter(temp.charAt(i))) {
                sb.append(temp.charAt(i) % 10);
            }
        }
        Integer account = Integer.parseInt(sb.toString());
        return "3" + String.valueOf(account);
    }

}
