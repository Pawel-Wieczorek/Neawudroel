package com.neawudroel.sakila;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@SecondaryTable(name = "film_category", pkJoinColumns = @PrimaryKeyJoinColumn(name = "category_id", referencedColumnName = "category_id"))
public class Category implements Serializable {
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "film_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private Set<Film> film = new HashSet<>();


    @Id
    @Column(name = "concert_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;

    @Column(name = "name")
    private String name;

    @Column(name = "last_update", nullable = false)
    private Timestamp last_update;


    public int getCategory_id() { return category_id; }

    public void setCategory_id(int category_id) { this.category_id = category_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }


    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }
}
