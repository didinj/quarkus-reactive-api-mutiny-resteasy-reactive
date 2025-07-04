package com.djamware.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Product extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column
    public String description;

    @Column(nullable = false)
    public double price;

    // No need for getters/setters — Panache handles it
}
