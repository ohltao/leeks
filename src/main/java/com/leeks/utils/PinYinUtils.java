package com.leeks.utils;

import com.github.promeg.pinyinhelper.Pinyin;

public class PinYinUtils {
    public static String toPinYin(String input) {
        return Pinyin.toPinyin(input, "_").toLowerCase();
    }
}
