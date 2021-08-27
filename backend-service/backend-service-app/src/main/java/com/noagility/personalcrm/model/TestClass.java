package com.noagility.personalcrm.model;


/**
 * For backend team:
 * @Data basically generates all the getters and setters for the attributes at runtime.
 * @AllArgsConstructor generates constructor with all attributes at runtime.
 * @NoArgsConstructor self explanatory.
 */
public class TestClass {

    public TestClass() {
    }

    public TestClass(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
    /*
    Up to here
     */
    private String id;
    private String name;
    private String address;
}
