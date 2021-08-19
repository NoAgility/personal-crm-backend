package com.noagility.personalcrm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * For backend team:
 * @Data basically generates all the getters and setters for the attributes at runtime.
 * @AllArgsConstructor generates constructor with all attributes at runtime.
 * @NoArgsConstructor self explanatory.
 */
@Data
//@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestClass {

    /*
    This won't be needed for the actual application, but I'm providing hardcoded objects in TestClassService
    as an example so I put it here
    */

    /*
    Remove these and uncomment the @AllArgsConstructor once you remove the hardcoded response @ testClassService
     */
    public TestClass(String id, String name, String address) {
        this.id = id;
        this.name = name;
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
