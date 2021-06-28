package com.company.entities.behaviors;

import com.company.entities.OfferGenerator;
import com.company.entities.Society;

public class GroupBehavior implements VoteBehavior {
    @Override
    public boolean vote(Society society, OfferGenerator offerGenerator, int myIndexInSociety) {
        return society.getGroupSolutions().get(society.getPeople().get(myIndexInSociety).getType());
    }
}
