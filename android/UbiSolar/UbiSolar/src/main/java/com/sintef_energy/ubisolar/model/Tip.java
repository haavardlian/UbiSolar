package com.sintef_energy.ubisolar.model;


/**
 * Created by Håvard on 05.03.14.
 */
public class Tip {
    private int id;
    private String name;
    private String description;
    private int averageRating;
    private int numberOfRatings;

    public Tip(int id, String name, String description, int averageRating, int numberOfRatings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.averageRating = averageRating;
        this.numberOfRatings = numberOfRatings;
    }

    public Tip() {
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return this.name + ": " + this.description + " | " + this.averageRating + ":" + this.numberOfRatings;
    }
}
