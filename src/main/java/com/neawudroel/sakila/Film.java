package com.neawudroel.sakila;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "film")
public class Film {
    @Id
    private int film_id;

    private String title;
    private String description;

    private Date release_year;

    // private int language_id // MANY_TO_ONE

    private int rental_duration;
    private int rental_rate; // mysql-type decimal (4,2)
    private int length;

    private int replacement_cost;

    public enum Rating {
        G("G"),
        PG("PG"),
        PG_13("PG-13"),
        R("R"),
        NC_17("NC-17");

        private Rating(final String string) {
            this.string = string;
        }

        // internal state
        private String string;

        public String toString() {
            return string;
        }
    }

    // Saved as enum but accessed as string.
    private Rating rating;

    public enum SpecialFeatures {
        TRAILERS("Trailers"),
        COMMENTARIES("Commentaries"),
        DELETED_SCENES("Deleted Scenes"),
        BEHIND_THE_SCENES("Behind the Scenes");

        private SpecialFeatures(final String string) {
            this.string = string;
        }

        // internal state
        private String string;

        public String toString() {
            return string;
        }
    }

    // Saved as set of enum but accessed as set of string.
    private Set<SpecialFeatures> special_features;

    private Timestamp last_update;

    public int getFilm_id() {
        return film_id;
    }

    public void setFilm_id(int film_id) {
        this.film_id = film_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Date release_year) {
        this.release_year = release_year;
    }

    public int getRental_duration() {
        return rental_duration;
    }

    public void setRental_duration(int rental_duration) {
        this.rental_duration = rental_duration;
    }

    public int getRental_rate() {
        return rental_rate;
    }

    public void setRental_rate(int rental_rate) {
        this.rental_rate = rental_rate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getReplacement_cost() {
        return replacement_cost;
    }

    public void setReplacement_cost(int replacement_cost) {
        this.replacement_cost = replacement_cost;
    }


    /**
     * Translating from enum to string occur.
     * Return value is not owned by this object. Use setter to set rating.
     */
    public String getRating() {
        return rating.toString();
    }

    /**
     * Translating from string to enum occur
     * Warning! String must be mappable by Film.Rating!
     */
    public void setRating(String rating) {
        this.rating = Rating.valueOf(rating);
    }

    /**
     * Translating from enum to string occur.
     * Return value is not owned by this object.
     * Use setter to set special_features.
     */
    public Set<String> getSpecial_features() {
        return special_features.stream()
                .map(SpecialFeatures::toString)
                .collect(Collectors.toSet());
    }


    /**
     * Translating from string to enum occur
     * Warning! String must be mappable by Film.SpecialFeatures!
     */
    public void setSpecial_features(@NotNull Set<String> special_features) {
        this.special_features = special_features.stream()
                .map(SpecialFeatures::valueOf)
                .collect(Collectors.toSet());
    }

    public Timestamp getLast_update() {
        return last_update;
    }

    // Automatically updated (Most likely)
    /*
    public void setLast_update(Timestamp last_update) {
        this.last_update = last_update;
    }*/
}
