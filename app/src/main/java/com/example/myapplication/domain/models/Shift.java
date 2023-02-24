package com.example.myapplication.domain.models;

import java.util.ArrayList;
import java.util.List;

public class Shift {
    private Employee[] employees = {null, null, null};
    private ShiftTime time;

    public Shift(){

    }

    public Shift(ShiftTime time){
        this.time = time;
    }

    public ShiftTime getTime() { return time; }

    public Employee[] getEmployees() { return employees; }

    public void addEmployee(int slot, Employee employee)
    {
        if (slot < 3)
            employees[slot] = employee;
    }
}
