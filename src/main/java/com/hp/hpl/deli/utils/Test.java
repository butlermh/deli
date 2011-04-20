package com.hp.hpl.deli.utils;

import java.lang.reflect.Constructor;

class Test
{
    public static void main(String[] args) throws Exception
    {
        Class<Outer.Inner> clazz = Outer.Inner.class;

        Constructor<Outer.Inner> ctor = clazz.getConstructor(Outer.class);

        Outer outer = new Outer();
        Outer.Inner instance = ctor.newInstance(outer);
    }
}

class Outer
{
    class Inner
    {
        // getConstructor only returns a public constructor. If you need
        // non-public ones, use getDeclaredConstructors
        public Inner() {}
    }
}
