package com.company.entities.behaviors;

import com.company.entities.OfferGenerator;
import com.company.entities.Society;
import com.company.entities.behaviors.VoteBehavior;

public class AltruisticBehavior implements VoteBehavior {
    @Override
    public boolean vote(Society society, OfferGenerator offerGenerator, int myIndexInSociety) {
        return offerGenerator.getAvgDelta() > 0;
    }
}
