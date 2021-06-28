package com.company.entities;

import com.company.entities.behaviors.AltruisticBehavior;
import com.company.entities.behaviors.EgoisticBehavior;
import com.company.entities.behaviors.GroupBehavior;
import com.company.entities.behaviors.VoteBehavior;
import com.company.entities.Society;


public class Person {

    private double capital;

    private VoteBehavior voteBehavior;

    private String type;

    public String getType() {
        return type;
    }

    public Person(String type) {
        capital = 0;
        this.type = type;
        switch (type) {
            case ("egoist"):
                voteBehavior = new EgoisticBehavior();
                return;
            case ("altruist"):
                voteBehavior = new AltruisticBehavior();
                return;
            default:
                voteBehavior = new GroupBehavior();
        }
    }

    public double getCapital() {
        return capital;
    }

    public void changeCapital(double delta) {
        this.capital = capital + delta;
    }

    public boolean vote (Society society, OfferGenerator offerGenerator, int myIndexInSociety) {
        return voteBehavior.vote(society, offerGenerator, myIndexInSociety);
    }

    @Override
    public String toString() {
        return "{" + type + " : " + capital + "}";
    }
}
