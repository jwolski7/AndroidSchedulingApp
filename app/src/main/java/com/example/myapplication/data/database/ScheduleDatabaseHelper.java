package com.example.myapplication.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.UI.Utilities.CalendarUtils;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Day;
import com.example.myapplication.domain.models.Employee;
import com.example.myapplication.domain.models.Shift;
import com.example.myapplication.domain.models.ShiftTime;

import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleDatabaseHelper extends SQLiteOpenHelper{
    private static final String TABLE_NAME = "schedule_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "date";
    private static final String COL3 = "dayType";
    private static final String COL4 = "busyStatus";
    private static final String COL5 = "empIDs";


    public ScheduleDatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableInit = "CREATE TABLE " + TABLE_NAME + "("+COL1+ " INTEGER PRIMARY KEY, " +
                COL2+" TEXT, "+ COL3+" TEXT, "+ COL4+" TEXT, "+COL5+" TEXT)";
        sqLiteDatabase.execSQL(tableInit);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /***public boolean populateDB(List<Day> dayList){
        return true;
    }***/

    public boolean addDay(Day day){
        SQLiteDatabase db;
        ContentValues content;

        LocalDate date = day.getDate();
        String dateString = date.toString();
        String dayType = "1";
        Shift[] shifts = day.getShifts();
        String busy = "0";
        StringBuilder empList = new StringBuilder();

        if (day.isBusyDay())
            busy = "1";

        String empString = "";

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            dayType = "0";

        for (int i = 0; i < 2; i++){
            if (shifts[i] != null){
                if (i > 0)
                    empList.append(":");

                Shift shift = shifts[i];
                for (int j = 0; j < 3; j++) {
                    Employee employee = shift.getEmployees()[j];
                    if (employee != null){
                        if (j > 0)
                            empList.append(",");

                        empList.append(employee.getId());
                    }
                }
            }
        }

        empString = empList.toString();

        db = this.getWritableDatabase();
        content = new ContentValues();
        content.put(COL2, dateString);
        content.put(COL3, dayType);
        content.put(COL4, busy);
        content.put(COL5, empString);

        long record = db.insert(TABLE_NAME, null, content);
        return record != -1;
    }

    public boolean updateDay(Day day){
        SQLiteDatabase db;
        ContentValues content;

        LocalDate date = day.getDate();
        String dateString = date.toString();
        String dayType = "1";
        Shift[] shifts = day.getShifts();
        String busy = "0";
        StringBuilder empList = new StringBuilder();

        if (day.isBusyDay())
            busy = "1";

        String empString = "";

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            dayType = "0";

        for (int i = 0; i < 2; i++){
            if (shifts[i] != null){
                if (i > 0)
                    empList.append(":");

                Shift shift = shifts[i];
                for (int j = 0; j < 3; j++) {
                    Employee employee = shift.getEmployees()[j];
                    if (employee != null){
                        if (j > 0)
                            empList.append(",");

                        empList.append(employee.getId());
                    }
                }
            }
        }

        empString = empList.toString();

        db = this.getWritableDatabase();
        content = new ContentValues();
        content.put(COL2, dateString);
        content.put(COL3, dayType);
        content.put(COL4, busy);
        content.put(COL5, empString);

        db.update(TABLE_NAME, content, "DATE = ? ", new String[]{dateString} );
        return true;
    }

    public List<Day> getSchedule(){
        List<Day> dayList = new ArrayList<>();
        List<String> shiftStringList;
        List<String> employeeStringList;
        EmployeeRepository employeeRepository;

        ShiftTime[] shiftTimes = {ShiftTime.MORNING, ShiftTime.AFTERNOON};


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        result.moveToFirst();

        String dateString;
        LocalDate date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String busy;
        String empString;

        employeeRepository = EmployeeRepository.getInstance();

        while (!result.isAfterLast()) {
            dateString = result.getString(result.getColumnIndexOrThrow(COL2));
            busy = result.getString(result.getColumnIndexOrThrow(COL4));
            empString = result.getString(result.getColumnIndexOrThrow(COL5));
            if (empString == null)
                empString = "";
            date = LocalDate.parse(dateString, formatter);
            Day currDay = new Day(date);

            if(busy.equals("1"))
                currDay.setBusy();

            shiftStringList = Arrays.asList(empString.split(":"));

            for (int i = 0; i < shiftStringList.size(); i++){
                Employee employee;
                String shiftString = shiftStringList.get(i);
                Shift shift = new Shift(shiftTimes[i]);
                employeeStringList = Arrays.asList(shiftString.split(","));
                for (int j = 0; j < employeeStringList.size(); j++){
                    String str = employeeStringList.get(j);
                    if (!str.equals(""))
                        employee = employeeRepository.getEmployeeByID(Integer.parseInt(employeeStringList.get(j)));
                    else
                        employee = null;
                    shift.addEmployee(j, employee);
                }
                currDay.setShift(shift);
            }

            dayList.add(currDay);
            result.moveToNext();
        }
        result.close();

        return dayList;
    }




}
