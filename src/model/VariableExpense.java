package model;

import java.time.LocalDate;

public class VariableExpense {
    private int id;
    private String name;
    private double amount;
    private int idPromoter;
    private LocalDate date;
    private boolean status;

    public VariableExpense() {}

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPromoter() {
        return idPromoter;
    }

    public void setIdPromoter(int idPromoter) {
        this.idPromoter = idPromoter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
