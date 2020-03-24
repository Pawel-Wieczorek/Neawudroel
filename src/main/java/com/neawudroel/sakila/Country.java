package com.neawudroel.sakila;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "country")
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "country_id", unique = true)
    private int country_id;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "last_update", nullable = false)
    private Timestamp last_update;

    private Set<City> cities;

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Timestamp getLast_update() {
        return last_update;
    }

    public void setLast_update(Timestamp last_update) {
        this.last_update = last_update;
    }

    @OneToMany(targetEntity = City.class, mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
}
