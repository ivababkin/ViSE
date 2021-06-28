package com.company.entities;

import java.util.*;

public class IndexCalculator {

    public static List<List<String>> getCoalitions(List<String> groupNames) {
        int N = groupNames.size();
        List<List<String>> coalitions = new ArrayList<>();
        for (int mask = 1; mask < (1 << N); mask++) {//перебор масок
            List<String> coalition = new ArrayList<>();
            for (int j = 0; j < N; j++) {//перебор индексов массива
                if ((mask & (1 << j)) != 0) {//поиск индекса в маске
                    coalition.add(groupNames.get(j));//вывод элемента
                }
            }
            coalitions.add(coalition);
        }
        return coalitions;
    }

    public static Map<String, Map<String, Double>> generateIndexes(Society society, double alpha) {
        Map<String, Integer> groupPopulations = society.getGroupPopulations();
        int populationWithoutEgoists = society.getPopulationWithoutEgoists(groupPopulations);
        groupPopulations.remove("egoist");
        List<String> groupNames = new ArrayList<>(groupPopulations.keySet());
        List<List<String>> coalitions = getCoalitions(groupNames);
        List<List<String>> winningCoalitions = getWinningCoalitions(groupPopulations, coalitions, populationWithoutEgoists, alpha);
        List<List<String>> minimalWinningCoalitions = getMinimalWinningCoalitions(winningCoalitions, groupPopulations, populationWithoutEgoists, alpha);
        Map<String, List<List<String>>> minimalWinningCoalitionsWithGroups = getMinimalWinningCoalitionsWithGroups(minimalWinningCoalitions, groupPopulations);
        Map<String, List<List<String>>> winningCoalitionsWhereGroupsAreDecisive = getWinningCoalitionsWhereGroupsAreDecisive(groupPopulations, winningCoalitions, populationWithoutEgoists, alpha);//количества решающих коалиций, где данная группа является ключевой
        Map<String, Map<String, Double>> indexes = getIndexes(minimalWinningCoalitionsWithGroups, groupPopulations, winningCoalitionsWhereGroupsAreDecisive,  populationWithoutEgoists, alpha);
        return indexes;
    }

    private static Map<String, List<List<String>>> getMinimalWinningCoalitionsWithGroups(List<List<String>> winningCoalitions, Map<String, Integer> groupPopulations) {
        Map<String, List<List<String>>> minimalWinningCoalitionsWithGroups = new HashMap<>();
        for (String groupName : groupPopulations.keySet()) {
            minimalWinningCoalitionsWithGroups.put(groupName, new ArrayList<>());
        }
        for (List<String> coalition : winningCoalitions) {
            for (String groupName : coalition) {
                minimalWinningCoalitionsWithGroups.get(groupName).add(coalition);
            }
        }
        return minimalWinningCoalitionsWithGroups;
    }

    private static int getPopulationOfCoalitionWithoutGroup(List<String> coalition, Map<String, Integer> groupPopulations, String groupName) {
        List<String> coalitionWithoutGr = new ArrayList<>();
        coalitionWithoutGr.addAll(coalition);
        coalitionWithoutGr.remove(groupName);
        return getPopulationOfCoalition(coalitionWithoutGr, groupPopulations);
    }


    private static int getPopulationOfCoalition(List<String> coalition, Map<String, Integer> groupPopulations) {
        int populationOfCoalition = 0;
        for (String group : coalition) {
            populationOfCoalition += groupPopulations.get(group);
        }
        return populationOfCoalition;
    }

    private static boolean isCoalitionWinning(Map<String, Integer> groupPopulations, List<String> coalition, int populationWithoutEgoists, double alpha) {
        if (getPopulationOfCoalition(coalition, groupPopulations) > populationWithoutEgoists * alpha) {
            return true;
        }
        return false;
    }

    private static List<List<String>> getWinningCoalitions(Map<String, Integer> groupPopulations, List<List<String>> coalitions, int populationWithoutEgoists, double alpha) {
        List<List<String>> winningCoalitions = new ArrayList<>();
        for (List<String> coalition : coalitions) {
            if (isCoalitionWinning(groupPopulations, coalition, populationWithoutEgoists, alpha)) {
                winningCoalitions.add(coalition);
            }
        }
        return winningCoalitions;
    }

    private static boolean isCoalitionMinimal(Map<String, Integer> groupPopulations, List<String> winningCoalition, int populationWithoutEgoists, double alpha) {
        if (!isCoalitionWinning(groupPopulations, winningCoalition, populationWithoutEgoists, alpha)) {
            return false;
        }
        for (String group : winningCoalition) {
            List<String> newCoalition = new ArrayList<>(winningCoalition);
            newCoalition.remove(group);
            if (isCoalitionWinning(groupPopulations, newCoalition, populationWithoutEgoists, alpha)) {
                return false;
            }
        }
        return true;
    }

    private static List<List<String>> getMinimalWinningCoalitions(List<List<String>> winningCoalitions, Map<String, Integer> groupPopulations, int populationWithoutEgoists, double alpha) {
        List<List<String>> minimalWinningCoalitions = new ArrayList<>();
        for (List<String> coalition : winningCoalitions) {
            if (isCoalitionMinimal(groupPopulations, coalition, populationWithoutEgoists, alpha)) {
                minimalWinningCoalitions.add(coalition);
            }
        }
        return minimalWinningCoalitions;
    }

    private static List<List<String>> getWinningCoalitionWhereGroupIsDecisive(String groupName, Map<String, Integer> groupPopulations, List<List<String>> winningCoalitions, int populationWithoutEgoists, double alpha) {
        //решающие коалиции, где данная группа является ключевой
        List<List<String>> coalitions = new ArrayList<>();
        for (List<String> coalition : winningCoalitions) {
            if (coalition.contains(groupName) &&
                    (getPopulationOfCoalition(coalition, groupPopulations) > populationWithoutEgoists * alpha) &&
                    (getPopulationOfCoalitionWithoutGroup(coalition, groupPopulations, groupName) <= populationWithoutEgoists * alpha)) {
                coalitions.add(coalition);
            }
        }
        return coalitions;
    }

    private static Map<String, List<List<String>>> getWinningCoalitionsWhereGroupsAreDecisive(Map<String, Integer> groupPopulations, List<List<String>> winningCoalitions, int populationWithoutEgoists, double alpha) { // number of winning coalitions, where current group is decisive
        Map<String, List<List<String>>> winningCoalitionsWhereGroupsAreDecisive = new HashMap<>();
        List<String> groupNames = new ArrayList<>(groupPopulations.keySet());
        for (String group : groupNames) {
            winningCoalitionsWhereGroupsAreDecisive.put(group, getWinningCoalitionWhereGroupIsDecisive(group, groupPopulations, winningCoalitions, populationWithoutEgoists, alpha));
        }
        return winningCoalitionsWhereGroupsAreDecisive;
    }

    private static double getAvgValue(Map<String, Double> map) {
        return map.values().stream().mapToDouble(Double::doubleValue).sum() / map.entrySet().size();
    }

    private static double getDispersion(Map<String, Double> map) {
        double dispersion = 0;
        double avgValue = getAvgValue(map);
        for (double value : map.values()) {
            dispersion += Math.pow((value - avgValue), 2);
        }
        return Math.sqrt(dispersion);
    }

    private static double getCovariation(Map<String, Double> indexes, Map<String, Double> capitals) {
        indexes.remove("egoist");
        double avgIndex = getAvgValue(indexes);
        double avgCapital = getAvgValue(capitals);
        double covariation = 0;
        for (String groupName : indexes.keySet()) {
            covariation += (indexes.get(groupName) - avgIndex) * (capitals.get(groupName) - avgCapital);
        }
        return covariation;
    }

    public static double getCorrelation( Map<String, Double> capitals, Map<String, Double> indexes) {
        double correlation = getCovariation(indexes, capitals) / getDispersion(indexes) / getDispersion(capitals);
        if (Double.isNaN(correlation)) {
            correlation = 0;
        }
        return correlation;
    }


    public static Map<String, Double> getBenzafIndexes(Map<String, List<List<String>>> winningCoalitionsWhereGroupsAreDecisive, Map<String, Integer> groupPopulations) {
        Map<String, Double> BenzafIndexes = new HashMap<>();
        int sumB_i_s = winningCoalitionsWhereGroupsAreDecisive.values().stream().mapToInt(List::size).sum();
        for (Map.Entry<String, List<List<String>>> entry : winningCoalitionsWhereGroupsAreDecisive.entrySet()) {
            BenzafIndexes.put(entry.getKey(),  (double) entry.getValue().size() / (double) sumB_i_s);
        }
        return normalizeIndexes(BenzafIndexes, groupPopulations);
    }

    public static Map<String, Double> getShepliShubikIndexes(Map<String, List<List<String>>> minimalWinningCoalitionsWhereGroupsAreDecisive, Map<String, Integer> groupPopulations) {
        Map<String, Double> ShepliShubikIndexes = new HashMap<>();
        int numOfGroups = groupPopulations.keySet().size();
        for (Map.Entry<String, List<List<String>>> coalitions : minimalWinningCoalitionsWhereGroupsAreDecisive.entrySet()) {
            double index = 0;
            for (List<String> coalition : coalitions.getValue()) {
                index += ((double)factorial(numOfGroups - coalition.size()) * (double)factorial(coalition.size() - 1) / (double) factorial(numOfGroups));
            }
            ShepliShubikIndexes.put(coalitions.getKey(), index);
        }
        return normalizeIndexes(ShepliShubikIndexes, groupPopulations);
    }


    public static Map<String, Double> normalizeIndexes(Map<String, Double> indexes, Map<String, Integer> groupPopulations) {
        for (Map.Entry<String, Double> index : indexes.entrySet()) {
//            indexes.put(index.getKey(), index.getValue());
//            indexes.put(index.getKey(), index.getValue() / groupPopulations.get(index.getKey()));
            indexes.put(index.getKey(), index.getValue() / Math.sqrt(groupPopulations.get(index.getKey())));
        }
        return indexes;

    }



    public static Map<String, Map<String, Double>> getIndexes(Map<String, List<List<String>>> minimalWinningCoalitionsWithGroups,
                                                 Map<String, Integer> groupPopulations, Map<String, List<List<String>>> winningCoalitionsWhereGroupsAreDecisive, int populationWithoutEgoists, double alpha) {
        Map<String, Map<String, Double>> indexes = new HashMap<>();
        indexes.put("Benzaf", getBenzafIndexes(winningCoalitionsWhereGroupsAreDecisive, groupPopulations));
        indexes.put("Johnston", getJohnstonIndexes(winningCoalitionsWhereGroupsAreDecisive, groupPopulations, populationWithoutEgoists, alpha));
        indexes.put("DigenPakel", getJohnstonIndexes(minimalWinningCoalitionsWithGroups, groupPopulations, populationWithoutEgoists, alpha));
        indexes.put("HolerPakel", getBenzafIndexes(minimalWinningCoalitionsWithGroups, groupPopulations));// как бенцаф, но по минимальным
        indexes.put("ShepliShubik", getShepliShubikIndexes(winningCoalitionsWhereGroupsAreDecisive, groupPopulations));
        return indexes;
    }

    public static long factorial(int n) {
        if (n == 0) {
            return 1;
        }
        if (n <= 2) {
            return n;
        }
        return n * factorial(n - 1);
    }



    public static int getNumberOfDecisiveGroupsInCoalition(List<String> coalition, Map<String, Integer> groupPopulations, int populationWithoutEgoists, double alpha) {
        //число ключевых партий в жанной коалиции
        if (!isCoalitionWinning(groupPopulations, coalition, populationWithoutEgoists, alpha)) {
            return 0;
        }
        int numberOfDecisiveGroupsInCoalition = 0;
        for (String group : coalition) {
            List<String> coalitionWithoutGroup = new ArrayList<>(coalition);
            coalitionWithoutGroup.remove(group);
            if (!isCoalitionWinning(groupPopulations, coalitionWithoutGroup, populationWithoutEgoists, alpha)) {
                numberOfDecisiveGroupsInCoalition++;
            }
        }
        return numberOfDecisiveGroupsInCoalition;
    }

    public static Map<String, Double> getJohnstonIndexes(Map<String, List<List<String>>> winningCoalitionsWhereGroupsAreDecisive, Map<String, Integer> groupPopulations, int populationWithoutEgoists, double alpha) {
        Map<String, Double> notNormalizedJohnstonIndexes = new HashMap<>();
        double totalIndex = 0;
        for (Map.Entry<String, List<List<String>>> coalitions : winningCoalitionsWhereGroupsAreDecisive.entrySet()) {
            double index = 0;
            for (List<String> coalition : coalitions.getValue()) {
                index += 1 / (double) getNumberOfDecisiveGroupsInCoalition(coalition, groupPopulations, populationWithoutEgoists, alpha);
            }
            totalIndex += index;
            notNormalizedJohnstonIndexes.put(coalitions.getKey(), index);
        }
        for (Map.Entry<String, Double> indexes : notNormalizedJohnstonIndexes.entrySet()) {
            notNormalizedJohnstonIndexes.put(indexes.getKey(), indexes.getValue() / totalIndex);
        }
        return normalizeIndexes(notNormalizedJohnstonIndexes, groupPopulations);
    }


}
