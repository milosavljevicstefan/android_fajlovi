package com.example.kolokvijum1b;

public class Person {
    private String name;
    private int age;
    private boolean active;

    public Person(String name, int age, boolean active) {
        this.name = name;
        this.age = age;
        this.active = active;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public boolean isActive() { return active; }
}