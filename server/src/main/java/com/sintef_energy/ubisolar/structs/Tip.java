package com.sintef_energy.ubisolar.structs;

import com.yammer.dropwizard.json.JsonSnakeCase;

/**
 * Created by Håvard on 05.03.14.
 */
@JsonSnakeCase
public class Tip {
    private int id;
    private String name;
    private String description;

    public Tip(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Tip() {
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
}
