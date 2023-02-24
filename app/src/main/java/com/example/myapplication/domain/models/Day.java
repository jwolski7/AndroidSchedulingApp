package com.example.myapplication.domain.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Day {
    private LocalDate date;
    private Shift[] shifts = {null, null};
    boolean isBusy = false;

    public Day(){

    }

    public Day(LocalDate date){
        this.date = date;
    }

    public void setBusy (){
        isBusy = !isBusy;

        if(!isBusy){
            if (shifts[0] != null)
                shifts[0].addEmployee(2, null);

            if (shifts[1] != null)
                shifts[1].addEmployee(2, null);
        }
    }

    public boolean isBusyDay() { return isBusy; }

    public LocalDate getDate() { return date; }

    public Shift[] getShifts() { return shifts; }

    public Shift getShiftByTime(ShiftTime time){
        if (time == ShiftTime.AFTERNOON)
            return shifts[1];

        return shifts[0];
    }

    public void setShift(Shift shift){
        if (shift.getTime() == ShiftTime.AFTERNOON)
            shifts[1] = shift;
        else
            shifts[0] = shift;
    }

}
