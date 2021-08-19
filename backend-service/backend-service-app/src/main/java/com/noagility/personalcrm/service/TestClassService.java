package com.noagility.personalcrm.service;


import com.noagility.personalcrm.model.TestClass;

import java.util.Arrays;
import java.util.List;

public class TestClassService {

    public List<TestClass> get() {
        return Arrays.asList(new TestClass[] {new TestClass("1", "Apple", "10 Appleton St"),
        new TestClass("2", "Banana", "20 Bananaboat Rd"),
                new TestClass("3", "Orange", "Orangepeel St")});
    }
}