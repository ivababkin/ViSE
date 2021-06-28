package com.company.entities.behaviors;

import com.company.entities.OfferGenerator;
import com.company.entities.Society;

public class EgoisticBehavior implements VoteBehavior {
    @Override
    public boolean vote(Society society, OfferGenerator offerGenerator, int myIndexInSociety) {
        return offerGenerator.getOffer().get(myIndexInSociety) > 0;
    }
}
