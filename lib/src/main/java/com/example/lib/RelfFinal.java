package com.example.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RelfFinal {

    private static final int KEY_EXIT = 10;  // 基本类型时 编译的时候直接替换了代码中的值，修改没有意义

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("invok3->"+RelfFinal.KEY_EXIT);
        Field field = RelfFinal.class.getDeclaredField("KEY_EXIT");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, 256);
        System.out.println("invok3-<"+RelfFinal.KEY_EXIT);
    }
}
