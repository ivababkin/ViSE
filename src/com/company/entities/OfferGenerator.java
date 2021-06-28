package com.company.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OfferGenerator {

    private Random random;

    private List<Double> offer;

    private double avgDelta;

    public OfferGenerator() {
        random = new Random();
    }

    public void generateNewOffer(int sizeOfSociety, double average, double deviation) {
        offer = new ArrayList<>();
        avgDelta = 0;
        for (int i = 0; i < sizeOfSociety; i++) {
            double delta = random.nextGaussian() * deviation + average;
            offer.add(delta);
            avgDelta += delta;
        }
        avgDelta /= sizeOfSociety;
    }

    public List<Double> getOffer() {
        return offer;
    }

    public double getAvgDelta() {
        return avgDelta;
    }

}
