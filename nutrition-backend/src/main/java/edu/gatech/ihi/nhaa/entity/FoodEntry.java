package edu.gatech.ihi.nhaa.entity;

import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "FoodEntry")
public class FoodEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "foodEntryId", nullable = false, updatable = false)
    private long foodEntryId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "consumptionDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date consumptionDate;

    @Column(name = "servings", nullable = false)
    private double servings;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "foodEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Nutrient> nutrients = new HashSet<>();

    public long getFoodEntryId() {
        return foodEntryId;
    }

    public void setFoodEntryId(long foodEntryId) {
        this.foodEntryId = foodEntryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(Date consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public double getServings() {
        return servings;
    }

    public void setServings(double servings) {
        this.servings = servings;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(Set<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public void addNutrient(Nutrient nutrient) {
        if(nutrients == null) {
            nutrients = new HashSet<>();
        }
        nutrients.add(nutrient);
        nutrient.setFoodEntry(this);
    }

    public void removeNutrient(Nutrient nutrient) {
        nutrients.remove(nutrient);
        nutrient.setFoodEntry(null);
    }
}