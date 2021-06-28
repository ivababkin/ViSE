package com.company.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Society {

    public double alpha = 0.5;

    public List<Person> people;

    public Society() {
        people = new ArrayList<>();
    }

    public void addPerson(String personType) {
        people.add(new Person(personType));
    }

    public void addPeople(String personType, int amountOfPeople) {
        for (int i = 0; i < amountOfPeople; i++) {
            addPerson(personType);
        }
    }

    public List<Double> getCapitals() {
        List<Double> capitals = new ArrayList<>();
        for (Person person : people) {
            capitals.add(person.getCapital());
        }
        return capitals;
    }

    public List<Person> getPeople() {
        return people;
    }

    public int getPopulation() {
        return people.size();
    }

    private Map<String, Boolean> groupSolutions;

    public Map<String, Boolean> getGroupSolutions() {
        return groupSolutions;
    }

    private boolean getSolutionForOneGroup(OfferGenerator offerGenerator, int firstIndexOfGrMember, int sizeOfGroup, String strategy) { // . A-стратегия-когда считается, сколько есть людей с положительным приращением    B-стратегия-когда считается, является ли приращения капитала группы в цемло положительным
        if ("B".equals(strategy)) {
            double avgGrDelta = 0;
            for (int i = firstIndexOfGrMember; i < firstIndexOfGrMember + sizeOfGroup; i++) {
                avgGrDelta += offerGenerator.getOffer().get(i);
            }
            avgGrDelta /= sizeOfGroup;
            return avgGrDelta > 0;
        } else if ("A".equals(strategy)) {
            int numOfBeneficiaries = 0;
            for (int i = firstIndexOfGrMember; i < firstIndexOfGrMember + sizeOfGroup; i++) {
                if (offerGenerator.getOffer().get(i) > 0) {
                    numOfBeneficiaries++;
                }
            }
            return numOfBeneficiaries > sizeOfGroup / 2;
        }
        return false;
    }

    public void generateGroupSolutions(OfferGenerator offerGenerator, String strategy) {
        groupSolutions = new HashMap<>();
        for (int personNumber = 0; personNumber < getPopulation(); personNumber++) {
            String groupName = people.get(personNumber).getType();
            if (!groupName.equals("altruist") && !groupName.equals("egoist")) {
                int firstIndexOfGrMember = personNumber;
                int sizeOfGroup = 1;
                while ((personNumber + sizeOfGroup < getPopulation()) && people.get(personNumber + sizeOfGroup).getType().equals(groupName)) {
                    sizeOfGroup++;
                }
                groupSolutions.put(groupName, getSolutionForOneGroup(offerGenerator, firstIndexOfGrMember, sizeOfGroup, strategy));
                personNumber = personNumber + sizeOfGroup - 1;
            }
        }
    }

    private void acceptOffer(List<Double> offer) {
        for (int i = 0; i < getPopulation(); i++) {
            people.get(i).changeCapital(offer.get(i));
        }
    }

    public void step(OfferGenerator offerGenerator, String strategy) {
        int numberOfAcceptors = 0;
        generateGroupSolutions(offerGenerator, strategy);
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).vote(this, offerGenerator, i)) {
                numberOfAcceptors++;
            }
        }
        if (numberOfAcceptors > getPopulation() * alpha) {
            acceptOffer(offerGenerator.getOffer());
        }
    }

    public Map<String, Integer> getGroupPopulations() {
        Map<String, Integer> groupPopulations = new HashMap<>();
        for (int personNumber = 0; personNumber < getPopulation(); personNumber++) {
            String groupName = getPeople().get(personNumber).getType();
            int sizeOfGroup = 1;
            while ((personNumber + sizeOfGroup < getPopulation()) && getPeople().get(personNumber + sizeOfGroup).getType().equals(groupName)) {
                sizeOfGroup++;
            }
            groupPopulations.put(groupName, sizeOfGroup);
            personNumber = personNumber + sizeOfGroup - 1;
        }
        return groupPopulations;
    }

    public int getPopulationWithoutEgoists(Map<String, Integer> groupPopulations) {
        return getPopulation() - (groupPopulations.get("egoist") == null ? 0 : groupPopulations.get("egoist"));
    }

    @Override
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[ ");
//        for (Person person : people) {
//            sb.append(person.toString()).append(", ");
//        }
//        sb.append("]");
//        return sb.toString();
        Map<String, Integer> population = new HashMap<>();
        for (Person person : people) {
            population.merge(person.getType(), 1, Integer::sum);
        }
        return population.toString();
    }

    Society getSameSocietyWithoutEgoists() {
        Society newSociety = new Society();
        for (Person person : people) {
            newSociety.addPerson(person.getType());
        }
        return newSociety;
    }
}
