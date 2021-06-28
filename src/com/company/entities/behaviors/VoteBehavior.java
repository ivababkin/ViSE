package com.company.entities.behaviors;

import com.company.entities.OfferGenerator;
import com.company.entities.Society;

public interface VoteBehavior {
    boolean vote(Society society, OfferGenerator offerGenerator, int myIndexInSociety);
}
