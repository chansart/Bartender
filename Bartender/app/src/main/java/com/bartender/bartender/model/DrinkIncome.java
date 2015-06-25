package com.bartender.bartender.model;

import java.util.Date;

/**
 * Created by Zélie on 02-05-15.
 */
public class DrinkIncome {

    private String DrinkName;
    private float Income; // revenu qu'à rapporté
    private Date dateOrder;
    public DrinkIncome(String DrinkName, float Income, Date date){
        this.DrinkName= DrinkName;
        this.Income=Income;
        this.dateOrder=date;

    }
    public String getDrinkName() {
        return DrinkName;
    }

    public void setDrinkName(String drinkName) {
        DrinkName = drinkName;
    }

    public float getIncome() {
        return Income;
    }

    public void setIncome(float income) {
        Income = income;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }
}
