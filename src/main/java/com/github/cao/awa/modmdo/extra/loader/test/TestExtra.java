package com.github.cao.awa.modmdo.extra.loader.test;

import com.github.cao.awa.modmdo.annotations.extra.*;
import com.github.cao.awa.modmdo.extra.loader.*;

import java.util.*;

@ModMdoAutoExtra
public class TestExtra extends ModMdoExtra<TestExtra> {
    private static final UUID id = UUID.fromString("1a6dbe1a-fea8-499f-82d1-cececcf78bab");

    @Override
    public void prepare() {
        setId(id);
        setName("TestModMdo");
    }

    @Override
    public void init() {
        //System.out.println("My test extra");
    }

    @Override
    public void initCommand() {

    }

    @Override
    public void initStaticCommand() {

    }

    @Override
    public void initEvent() {

    }
}
