package com.company;

import com.company.entities.IndexCalculator;
import com.company.entities.OfferGenerator;
import com.company.entities.Person;
import com.company.entities.Society;

import java.util.HashMap;
import java.util.Map;

public class Main {


    public static int TOTAL_POPULATION = 3000;

    public static Map<String, Double> makeSteps(OfferGenerator offerGenerator, Society society, int numberOfSteps, double average, double deviation, String strategy) {
        for (int i = 0; i < numberOfSteps; i++) {
            offerGenerator.generateNewOffer(society.getPopulation(), average, deviation);
            society.step(offerGenerator, strategy);
        }
        return getGroupCapitals(society, numberOfSteps);
    }

    private static double getAvgCapitalOfOneGroup(Society society, int firstIndexOfGrMember, int sizeOfGroup) {
        double avgCapital = 0;
        for (int i = firstIndexOfGrMember; i < firstIndexOfGrMember + sizeOfGroup; i++) {
            avgCapital += society.getPeople().get(i).getCapital();
        }
        avgCapital /= sizeOfGroup;
        return avgCapital;
    }

    private static Map<String, Double> getGroupCapitals(Society society, int numberOfSteps) { //average capitals in groups
        Map<String, Double> groupCapitals = new HashMap<>();
        for (int personNumber = 0; personNumber < society.getPopulation(); personNumber++) {
            String groupName = society.getPeople().get(personNumber).getType();
            int firstIndexOfGrMember = personNumber;
            int sizeOfGroup = 1;
            while ((personNumber + sizeOfGroup < society.getPopulation()) && society.getPeople().get(personNumber + sizeOfGroup).getType().equals(groupName)) {
                sizeOfGroup++;
            }
            groupCapitals.put(groupName, getAvgCapitalOfOneGroup(society, firstIndexOfGrMember, sizeOfGroup) / (double) numberOfSteps);
            personNumber = personNumber + sizeOfGroup - 1;
        }
        return groupCapitals;
    }


    private double calculateAverageCapital(Society society) {
        double averageCapital = 0;
        for (Person person : society.getPeople()) {
            averageCapital += person.getCapital();
        }
        averageCapital = averageCapital / society.getPopulation();
        return averageCapital;
    }

    private String getEgoOneTwoThreeCapitals(Map<String, Double> groupCapitals, Society society) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ")
                .append(groupCapitals.get("egoist") == null ? "0" : groupCapitals.get("egoist")).append(" ")
                .append(groupCapitals.get("grOne") == null ? "0" : groupCapitals.get("grOne")).append(" ")
                .append(groupCapitals.get("grTwo") == null ? "0" : groupCapitals.get("grTwo")).append(" ")
                .append(groupCapitals.get("grThree") == null ? "0" : groupCapitals.get("grThree")).append(" ")
                .append(calculateAverageCapital(society));
        return sb.toString();
    }

    public static Society generateRandomGroups(int notNormalizedNumOfEgoist) { //from 3 to 8 groups
        int numOfGroups = (int) (Math.random() * 7 + 3);
        Map<String, Integer> populationsOfGroups = new HashMap<>();
        int totalPopulation = 0;
        for (int i = 0; i < numOfGroups; i++) {
            int newRandomNumber = (int) (Math.random() * 500 + 100);
            populationsOfGroups.put("Gr_" + i, newRandomNumber);
            totalPopulation += newRandomNumber;
        }

//        int numberOfEgoist = (int) (Math.random() * 300 + 50);
//        totalPopulation += notNormalizedNumOfEgoist;
//        populationsOfGroups.put("egoist", notNormalizedNumOfEgoist / totalPopulation);

        Society society = new Society();
        for (int i = 0; i < numOfGroups; i++) {
            society.addPeople("Gr_" + i, populationsOfGroups.get("Gr_" + i) * TOTAL_POPULATION / totalPopulation);
        }
        return society;
    }

    private Map<String, Double> sumMaps(Map<String, Double> firstMap, Map<String, Double> secondMap) {
        Map<String, Double> newMap = new HashMap<>();
        for (String key : firstMap.keySet()) {
            newMap.put(key, firstMap.get(key) + firstMap.get(key));
        }
        return newMap;
    }

    private void printIndexes() {
        double totalBenzafIndex = 0;
        double totalJohnstonIndex = 0;
        double totalDigenPakelIndex = 0;
        double totalShepliShubikIndex = 0;
        double totalHolerPakelIndex = 0;

        for (int i = 0; i < 100; i++) {
            Society society = generateRandomGroups(0);
            society.alpha = 0.5;
            Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 10000, -0.2, 10, "B");
            Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
            totalBenzafIndex += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
            totalJohnstonIndex += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
            totalDigenPakelIndex += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
            totalShepliShubikIndex += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
            totalHolerPakelIndex += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            System.out.println(i + "   " + totalBenzafIndex / ((double) i + 1) + "   " + totalJohnstonIndex / ((double) i + 1) +
                    "   " + totalDigenPakelIndex / ((double) i + 1) + "   " + totalShepliShubikIndex / ((double) i + 1) + "   " + totalHolerPakelIndex / ((double) i + 1));
        }
    }

    public static Society generateRandomGroupsWithEgoists(int notNormalizedNumOfEgoist) { //from 3 to 8 groups
        int numOfGroups = (int) (Math.random() * 7 + 12);
        Map<String, Integer> populationsOfGroups = new HashMap<>();
        int totalPopulation = 0;
        for (int i = 0; i < numOfGroups; i++) {
            int newRandomNumber = (int) (Math.random() * 500 + 100);
            populationsOfGroups.put("Gr_" + i, newRandomNumber);
            totalPopulation += newRandomNumber;
        }

        //totalPopulation += notNormalizedNumOfEgoist;
        populationsOfGroups.put("egoist", notNormalizedNumOfEgoist);

        Society society = new Society();
        for (int i = 0; i < numOfGroups; i++) {
            society.addPeople("Gr_" + i, populationsOfGroups.get("Gr_" + i) * (TOTAL_POPULATION - notNormalizedNumOfEgoist) / totalPopulation);
        }
        return society;
    }

    private static void printIndexesFromNumOfEgoists() {
        for (int notNormalizedNumOfEgoist = 1000; notNormalizedNumOfEgoist < 2500; notNormalizedNumOfEgoist += 50) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            int numOfExperiments = 10;
            for (int i = 0; i < numOfExperiments; i++) {
                Society society = generateRandomGroups(notNormalizedNumOfEgoist);
                society.alpha = 0.5;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, -0.2, 10, "A");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
//            System.out.println(notNormalizedNumOfEgoist + "   " + totalBenzafIndCor / (numOfExperiments) + "   " + totalJohnstonIndCor / (numOfExperiments) +
//                    "   " + totalDigenPakelIndCor / (numOfExperiments) + "   " + totalShepliShubikIndCor / (numOfExperiments) + "   " + totalHolerPakelIndCor / (numOfExperiments));
            System.out.printf("%d    %.3f    %.3f    %.3f    %.3f    %.3f\n", notNormalizedNumOfEgoist, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));

        }
    }

    private static void printIndexesFromAlpha() {
        for (double alpha = 0.1; alpha <= 0.9; alpha += 0.1) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;

            final int numOfExperiments = 10;

            for (int i = 0; i < numOfExperiments; i++) {
                Society society = generateRandomGroups(0);
                society.alpha = alpha;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 50000, -0.2, 10, "A");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
            System.out.println(alpha + "   " + totalBenzafIndCor / (numOfExperiments) + "   " + totalJohnstonIndCor / (numOfExperiments) +
                    "   " + totalDigenPakelIndCor / (numOfExperiments) + "   " + totalShepliShubikIndCor / (numOfExperiments) + "   " + totalHolerPakelIndCor / (numOfExperiments));
            System.out.printf("%.2f    %.3f    %.3f    %.3f    %.3f    %.3f\n", alpha, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));

        }
    }

    private static void printIndexesFromNumOFGroups() {
        for (int numOfGroups = 2; numOfGroups <= 20; numOfGroups++) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            int numOfExperiments = 10;
            for (int i = 0; i < numOfExperiments; i++) {
                Society society = generateNRandomGroups(numOfGroups);
                society.alpha = 0.5;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, -0.2, 10, "B");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
//            System.out.println(numOfGroups + "   " + totalBenzafIndCor / (30) + "   " + totalJohnstonIndCor / (30) +
//                    "   " + totalDigenPakelIndCor / (30) + "   " + totalShepliShubikIndCor / (30) + "   " + totalHolerPakelIndCor / (30));
            System.out.printf("%d    %.3f    %.3f    %.3f    %.3f    %.3f\n", numOfGroups, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));

        }
    }

    private static Society generateNRandomGroups(int numOfGroups) {
        Map<String, Integer> populationsOfGroups = new HashMap<>();
        int totalPopulation = 0;
        for (int i = 0; i < numOfGroups; i++) {
            int newRandomNumber = (int) (Math.random() * 500 + 100);
            populationsOfGroups.put("Gr_" + i, newRandomNumber);
            totalPopulation += newRandomNumber;
        }

        Society society = new Society();
        for (int i = 0; i < numOfGroups; i++) {
            society.addPeople("Gr_" + i, populationsOfGroups.get("Gr_" + i) * TOTAL_POPULATION / totalPopulation);
        }
        return society;

    }

    private void printIndexesFromMu() {
        for (double Mu = -1.5; Mu < -1; Mu += 0.05) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            for (int i = 0; i < 30; i++) {
                Society society = generateRandomGroups(0);
                society.alpha = 0.5;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, Mu, 10, "B");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
            System.out.println(Mu + "   " + totalBenzafIndCor / (30) + "   " + totalJohnstonIndCor / (30) +
                    "   " + totalDigenPakelIndCor / (30) + "   " + totalShepliShubikIndCor / (30) + "   " + totalHolerPakelIndCor / (30));
        }
    }

    private void printIndexesFromDeviation() {
        for (double deviation = 1; deviation < 15; deviation++) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            for (int i = 0; i < 30; i++) {
                Society society = generateRandomGroups(0);
                society.alpha = 0.5;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, -0.2, deviation, "B");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
            System.out.println(deviation + "   " + totalBenzafIndCor / (30) + "   " + totalJohnstonIndCor / (30) +
                    "   " + totalDigenPakelIndCor / (30) + "   " + totalShepliShubikIndCor / (30) + "   " + totalHolerPakelIndCor / (30));
        }
    }

    private static void printIndexesFromFavour() { // favour = average / deviation
        for (double favour = -0.02; favour <= -0.02; favour += 0.005) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            final int numOfExperiments = 10;
            for (int i = 0; i < numOfExperiments; i++) {
                Society society = generateRandomGroups(0);
                society.alpha = 0.5;
                double deviation = 10;
                double average = deviation * favour;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, average, deviation, "A");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
//            System.out.println(favour + "   " + totalBenzafIndCor / (numOfExperiments) + "   " + totalJohnstonIndCor / (numOfExperiments) +
//                    "   " + totalDigenPakelIndCor / (numOfExperiments) + "   " + totalShepliShubikIndCor / (numOfExperiments) + "   " + totalHolerPakelIndCor / (numOfExperiments));
            System.out.printf("%.3f    %.3f    %.3f    %.3f    %.3f    %.3f\n", favour, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));

        }
    }

    private static void justPrintIndexes() {
        double totalBenzafIndCor = 0;
        double totalJohnstonIndCor = 0;
        double totalDigenPakelIndCor = 0;
        double totalShepliShubikIndCor = 0;
        double totalHolerPakelIndCor = 0;
        final int numOfExperiments = 30;
        for (int i = 0; i < numOfExperiments; i++) {
            System.out.println("" + i + "/30");
            Society society = generateRandomGroups(0);
            society.alpha = 0.5;
            double deviation = 10;
            double average = -0.2;
            Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 50000, average, deviation, "B");
            Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
            totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
            totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
            totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
            totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
            totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
        }
//            System.out.println(favour + "   " + totalBenzafIndCor / (numOfExperiments) + "   " + totalJohnstonIndCor / (numOfExperiments) +
//                    "   " + totalDigenPakelIndCor / (numOfExperiments) + "   " + totalShepliShubikIndCor / (numOfExperiments) + "   " + totalHolerPakelIndCor / (numOfExperiments));
        System.out.printf("%.3f    %.3f    %.3f    %.3f    %.3f\n", totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));


    }

    private static Society specialGenerateRandomGroups(int minSizeOfGroup) {
        int numOfGroups = 15;//(int) (Math.random() * 7 + 12);
        Map<String, Integer> populationsOfGroups = new HashMap<>();
        int totalPopulation = 0;
        for (int i = 0; i < numOfGroups; i++) {
            int newRandomNumber = (int) (Math.random() * 500 + minSizeOfGroup);
            populationsOfGroups.put("Gr_" + i, newRandomNumber);
            totalPopulation += newRandomNumber;
        }

        Society society = new Society();
        for (int i = 0; i < numOfGroups; i++) {
            society.addPeople("Gr_" + i, populationsOfGroups.get("Gr_" + i) * TOTAL_POPULATION / totalPopulation);
        }
        return society;

    }

    private static void printIndexesFromGroupSizeDeviation() {
        for (int minSizeOfGroup = 50; minSizeOfGroup <= 1000; minSizeOfGroup += 50) {
            double totalBenzafIndCor = 0;
            double totalJohnstonIndCor = 0;
            double totalDigenPakelIndCor = 0;
            double totalShepliShubikIndCor = 0;
            double totalHolerPakelIndCor = 0;
            final int numOfExperiments = 10;
            for (int i = 0; i < numOfExperiments; i++) {
                Society society = specialGenerateRandomGroups(minSizeOfGroup);
                society.alpha = 0.5;
                double deviation = 10;
                double average = -0.1;
                Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, average, deviation, "A");
                Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
                totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
                totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
                totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
                totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
            }
            System.out.printf("%d    %.3f    %.3f    %.3f    %.3f    %.3f\n", minSizeOfGroup, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));

        }
    }

    private static void printThreeGroupsFromAlpha() {
        for (double alpha = 0.1; alpha <= 0.9; alpha += 0.05) {
            Society society = new Society();
            society.addPeople("Gr_1.1", 50);
            society.addPeople("Gr_1.2", 50);
            society.addPeople("Gr_1.3", 50);
            society.addPeople("Gr_1.4", 50);
            society.addPeople("Gr_1.5", 50);

            society.addPeople("Gr_2.1", 200);
            society.addPeople("Gr_2.2", 200);
            society.addPeople("Gr_2.3", 200);
            society.addPeople("Gr_2.4", 200);
            society.addPeople("Gr_2.5", 200);

            society.addPeople("Gr_3.1", 350);
            society.addPeople("Gr_3.2", 350);
            society.addPeople("Gr_3.3", 350);
            society.addPeople("Gr_3.4", 350);
            society.addPeople("Gr_3.5", 350);

            society.alpha = alpha;
            Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 50000, -0.2, 10, "A");
            Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, alpha);
            double avgCapitalDeltas1 = avgCapitalDeltas.get("Gr_1.1") + avgCapitalDeltas.get("Gr_1.2") + avgCapitalDeltas.get("Gr_1.3") + avgCapitalDeltas.get("Gr_1.4") + avgCapitalDeltas.get("Gr_1.5") / 5;
            double avgCapitalDeltas2 = avgCapitalDeltas.get("Gr_2.1") + avgCapitalDeltas.get("Gr_2.2") + avgCapitalDeltas.get("Gr_2.3") + avgCapitalDeltas.get("Gr_2.4") + avgCapitalDeltas.get("Gr_2.5") / 5;
            double avgCapitalDeltas3 = avgCapitalDeltas.get("Gr_3.1") + avgCapitalDeltas.get("Gr_3.2") + avgCapitalDeltas.get("Gr_3.3") + avgCapitalDeltas.get("Gr_3.4") + avgCapitalDeltas.get("Gr_3.5") / 5;

            double index1 = indexes.get("Benzaf").get("Gr_1.1") + indexes.get("Benzaf").get("Gr_1.2") + indexes.get("Benzaf").get("Gr_1.3") + indexes.get("Benzaf").get("Gr_1.4") + indexes.get("Benzaf").get("Gr_1.5") / 5;
            double index2 = indexes.get("Benzaf").get("Gr_2.1") + indexes.get("Benzaf").get("Gr_2.2") + indexes.get("Benzaf").get("Gr_2.3") + indexes.get("Benzaf").get("Gr_2.4") + indexes.get("Benzaf").get("Gr_2.5") / 5;
            double index3 = indexes.get("Benzaf").get("Gr_3.1") + indexes.get("Benzaf").get("Gr_3.2") + indexes.get("Benzaf").get("Gr_3.3") + indexes.get("Benzaf").get("Gr_3.4") + indexes.get("Benzaf").get("Gr_3.5") / 5;


            System.out.printf("%.2f    %.6f    %.6f    %.6f    %.6f    %.6f    %.6f\n", alpha,
                    avgCapitalDeltas1, avgCapitalDeltas2, avgCapitalDeltas3,
                    index1, index2, index3);
        }
    }

    private static void printThreeGroupsFromFavour() {
        for (double Mu = -1; Mu < 0.5; Mu += 0.05) {
            Society society = new Society();
            society.addPeople("Gr_1.1", 50);
            society.addPeople("Gr_1.2", 50);
            society.addPeople("Gr_1.3", 50);
            society.addPeople("Gr_1.4", 50);
            society.addPeople("Gr_1.5", 50);

            society.addPeople("Gr_2.1", 200);
            society.addPeople("Gr_2.2", 200);
            society.addPeople("Gr_2.3", 200);
            society.addPeople("Gr_2.4", 200);
            society.addPeople("Gr_2.5", 200);

            society.addPeople("Gr_3.1", 350);
            society.addPeople("Gr_3.2", 350);
            society.addPeople("Gr_3.3", 350);
            society.addPeople("Gr_3.4", 350);
            society.addPeople("Gr_3.5", 350);

            society.alpha = 0.5;
            Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 30000, Mu, 10, "A");
            Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
            double avgCapitalDeltas1 = avgCapitalDeltas.get("Gr_1.1") + avgCapitalDeltas.get("Gr_1.2") + avgCapitalDeltas.get("Gr_1.3") + avgCapitalDeltas.get("Gr_1.4") + avgCapitalDeltas.get("Gr_1.5") / 5;
            double avgCapitalDeltas2 = avgCapitalDeltas.get("Gr_2.1") + avgCapitalDeltas.get("Gr_2.2") + avgCapitalDeltas.get("Gr_2.3") + avgCapitalDeltas.get("Gr_2.4") + avgCapitalDeltas.get("Gr_2.5") / 5;
            double avgCapitalDeltas3 = avgCapitalDeltas.get("Gr_3.1") + avgCapitalDeltas.get("Gr_3.2") + avgCapitalDeltas.get("Gr_3.3") + avgCapitalDeltas.get("Gr_3.4") + avgCapitalDeltas.get("Gr_3.5") / 5;

            double index1 = indexes.get("Benzaf").get("Gr_1.1") + indexes.get("Benzaf").get("Gr_1.2") + indexes.get("Benzaf").get("Gr_1.3") + indexes.get("Benzaf").get("Gr_1.4") + indexes.get("Benzaf").get("Gr_1.5") / 5;
            double index2 = indexes.get("Benzaf").get("Gr_2.1") + indexes.get("Benzaf").get("Gr_2.2") + indexes.get("Benzaf").get("Gr_2.3") + indexes.get("Benzaf").get("Gr_2.4") + indexes.get("Benzaf").get("Gr_2.5") / 5;
            double index3 = indexes.get("Benzaf").get("Gr_3.1") + indexes.get("Benzaf").get("Gr_3.2") + indexes.get("Benzaf").get("Gr_3.3") + indexes.get("Benzaf").get("Gr_3.4") + indexes.get("Benzaf").get("Gr_3.5") / 5;


            System.out.printf("%.3f    %.6f    %.6f    %.6f    %.6f    %.6f    %.6f\n", Mu / 10,
                    avgCapitalDeltas1, avgCapitalDeltas2, avgCapitalDeltas3,
                    index1, index2, index3);
        }
    }

    private static void printBestCorrelations() {
        double totalBenzafIndCor = 0;
        double totalJohnstonIndCor = 0;
        double totalDigenPakelIndCor = 0;
        double totalShepliShubikIndCor = 0;
        double totalHolerPakelIndCor = 0;
        int numOfExperiments = 50;
        for (int i = 0; i < numOfExperiments; i++) {
            Society society = generateRandomGroups(0);
            society.alpha = 0.5;
            Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 50000, -0.1, 10, "B");
            Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
            totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
            totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
            totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
            totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
            totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
        }
        System.out.printf("%.5f    %.5f    %.5f    %.5f    %.5f\n", totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));


    }

    private static void printIndexesFromFavourAndAlpha() { // favour = average / deviation
        for (double favour = -0.1; favour <= 0.07; favour += 0.05) {
            for (double alpha = 0.1; alpha <= 0.9; alpha += 0.2) {
                double totalBenzafIndCor = 0;
                double totalJohnstonIndCor = 0;
                double totalDigenPakelIndCor = 0;
                double totalShepliShubikIndCor = 0;
                double totalHolerPakelIndCor = 0;
                final int numOfExperiments = 5;
                for (int i = 0; i < numOfExperiments; i++) {
                    Society society = generateRandomGroups(0);
                    society.alpha = alpha;
                    double deviation = 10;
                    double average = deviation * favour;
                    Map<String, Double> avgCapitalDeltas = makeSteps(new OfferGenerator(), society, 10000, average, deviation, "A");
                    Map<String, Map<String, Double>> indexes = IndexCalculator.generateIndexes(society, society.alpha);
                    totalBenzafIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Benzaf"));
//                    totalJohnstonIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("Johnston"));
//                    totalDigenPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("DigenPakel"));
//                    totalShepliShubikIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("ShepliShubik"));
//                    totalHolerPakelIndCor += IndexCalculator.getCorrelation(avgCapitalDeltas, indexes.get("HolerPakel"));
                }
//                System.out.printf("%.3f    %.2f    %.3f    %.3f    %.3f    %.3f    %.3f\n", favour, alpha, totalBenzafIndCor / (numOfExperiments), totalJohnstonIndCor / (numOfExperiments), totalDigenPakelIndCor / (numOfExperiments), totalShepliShubikIndCor / (numOfExperiments), totalHolerPakelIndCor / (numOfExperiments));
                System.out.printf("%.3f    %.2f    %.4f\n", favour, alpha, totalBenzafIndCor / (numOfExperiments));

            }
        }
    }

    public static void run() {

        //printIndexes();
        //printIndexesFromNumOfEgoists();
        //printIndexesFromNumOFGroups();
//        printIndexesFromAlpha();
        //printIndexesFromMu();
        //printIndexesFromDeviation();
//        printIndexesFromFavour();
    }

    public static void main(String[] args) {
        printIndexesFromFavourAndAlpha();

        //run();
//        Engine engine = new Engine();
//        engine.run();
    }
}
