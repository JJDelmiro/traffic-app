package com.example.trafficsigns;

public class Signs {
    private String name;
    private int image;

    public Signs(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
