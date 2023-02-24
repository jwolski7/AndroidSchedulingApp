package com.example.myapplication.data.repository;

import android.content.Context;

import com.example.myapplication.data.database.EmployeeDatabaseHelper;
import com.example.myapplication.data.database.ScheduleDatabaseHelper;
import com.example.myapplication.domain.models.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DayRepository {
    private  List<Day> dayList = new ArrayList<>();
    private  ScheduleDatabaseHelper schedDB;

    private static final DayRepository instance = new DayRepository();

    public static DayRepository getInstance() { return instance; }

    public void setContext(Context context){
        schedDB = new ScheduleDatabaseHelper(context);
    }
    public List<Day> getSchedule(){
        return schedDB.getSchedule();
    }

    public boolean addDay(Day day){
        schedDB.addDay(day);
        return true;
    }

    public  Day getDayByDate(LocalDate date){
        LocalDate dt;
        dayList = getSchedule();
        for (Day day: dayList) {
            dt = day.getDate();
            System.out.println(date.toString());
            System.out.println(dt.toString());
            if (dt.isEqual(date))
                return day;
        }
        return null;
    }

    public void updateDay(Day day){
        schedDB.updateDay(day);
    }
}
