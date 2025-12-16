package org.delcom.app.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tools.jackson.databind.ObjectMapper;

public class CashFlowStats {
    private int totalIncome = 0;
    private int totalOutcome = 0;

    private List<Integer> statIncomes = new ArrayList<Integer>();
    private List<Integer> statOutcomes = new ArrayList<Integer>();
    private List<String> statLabels = new ArrayList<String>();

    // Constructor
    public CashFlowStats() {

    }

    // Setter & Getters
    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void addTotalIncome(int totalIncome) {
        this.totalIncome += totalIncome;
    }

    public int getTotalOutcome() {
        return totalOutcome;
    }

    public void setTotalOutcome(int totalOutcome) {
        this.totalOutcome = totalOutcome;
    }

    public void addTotalOutcome(int totalOutcome) {
        this.totalOutcome += totalOutcome;
    }

    public List<Integer> getStatIncomes() {
        return statIncomes;
    }

    public void setStatIncomes(List<Integer> statIncomes) {
        this.statIncomes = statIncomes;
    }

    public List<Integer> getStatOutcomes() {
        return statOutcomes;
    }

    public void setStatOutcomes(List<Integer> statOutcomes) {
        this.statOutcomes = statOutcomes;
    }

    public List<String> getStatLabels() {
        return statLabels;
    }

    public void setStatLabels(List<String> statLabels) {
        this.statLabels = statLabels;
    }

    public void addStatLabels(String label) {
        this.statLabels.add(label);
        Collections.sort(this.statLabels);
    }

    // Method untuk mendapatkan labels sebagai JSON array
    public String getStatLabelsJson() {
        try {
            return new ObjectMapper().writeValueAsString(statLabels);
        } catch (Exception e) {
            return "[]";
        }
    }

    // Method untuk mendapatkan incomes sebagai JSON array
    public String getStatIncomesJson() {
        try {
            return new ObjectMapper().writeValueAsString(statIncomes);
        } catch (Exception e) {
            return "[]";
        }
    }

    // Method untuk mendapatkan outcomes sebagai JSON array
    public String getStatOutcomesJson() {
        try {
            return new ObjectMapper().writeValueAsString(statOutcomes);
        } catch (Exception e) {
            return "[]";
        }
    }

    // Method untuk balance
    public int getBalance() {
        return totalIncome - totalOutcome;
    }
}
