package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.UI.Utilities.CalendarUtils;
import com.example.myapplication.data.repository.DayRepository;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Day;
import com.example.myapplication.domain.models.Employee;
import com.example.myapplication.domain.models.Shift;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button viewEmployeeButton;
    Button viewCalendarButton;
    TextView[] morningTextView = {null, null, null, null};
    TextView[] afternoonTextView = {null, null, null, null};
    DayRepository dayRepository;
    EmployeeRepository employeeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        morningTextView[0] = findViewById(R.id.morningTextView);
        morningTextView[1] = findViewById(R.id.dayEmployee1TextView);
        morningTextView[2] = findViewById(R.id.dayEmployee2TextView);
        morningTextView[3] = findViewById(R.id.dayEmployee3TextView);
        afternoonTextView[0] = findViewById(R.id.afternoonTextView);
        afternoonTextView[1] = findViewById(R.id.afternoonEmployee1TextView);
        afternoonTextView[2] = findViewById(R.id.afternoonEmployee2TextView);
        afternoonTextView[3] = findViewById(R.id.afternoonEmployee3TextView);

        viewEmployeeButton = findViewById(R.id.viewEmployeeButton);
        viewEmployeeButton.setOnClickListener(this::viewEmployees);


        viewCalendarButton = findViewById(R.id.viewCalendarButton);
        viewCalendarButton.setOnClickListener(this::viewCalendarActivity);

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        dayRepository = DayRepository.getInstance();
        dayRepository.setContext(this);
        //System.out.println("Here");
        CalendarUtils.selectedDate = LocalDate.now();

        setDayAdapter();
    }

    public void clearTextView(){
        for (int i = 0; i < 4; i++){
            morningTextView[i].setText("");
            afternoonTextView[i].setText("");
        }
    }

    private void setWeekdayAdapter(){
        Day day = dayRepository.getDayByDate(CalendarUtils.selectedDate);
        Shift[] shifts;

        List<String> morningEmployeeNames = new ArrayList<>();
        List<String> afternoonEmployeeNames = new ArrayList<>();

        morningEmployeeNames.add("Morning:");
        afternoonEmployeeNames.add("Afternoon:");
        if (day != null) {
            shifts = day.getShifts();
            int employeeCount = 2;

            if (day.isBusyDay())
                employeeCount = 3;

            if (shifts != null) {
                if (shifts[0] != null) {
                    for (int i = 0; i < employeeCount; i++) {
                        Employee employee = shifts[0].getEmployees()[i];
                        if (employee != null)
                            morningEmployeeNames.add(shifts[0].getEmployees()[i].getName());
                        //morningEmployeeNames.add(shifts[0].getEmployees()[i].getName());
                    }
                }

                if (shifts[1] != null) {
                    for (int i = 0; i < employeeCount; i++) {
                        Employee employee = shifts[1].getEmployees()[i];
                        if (employee != null)
                            afternoonEmployeeNames.add(shifts[1].getEmployees()[i].getName());
                    }
                }
            }
        } else {
            morningEmployeeNames.add("No one scheduled");
            afternoonEmployeeNames.add("No one scheduled");
        }

        clearTextView();

        for(int i = 0; i < morningEmployeeNames.size(); i++){
            morningTextView[i].setText(morningEmployeeNames.get(i));
        }

        for(int i = 0; i < afternoonEmployeeNames.size(); i++){
            afternoonTextView[i].setText(afternoonEmployeeNames.get(i));
        }
    }

    private void setWeekendAdapter(){
        Day day = dayRepository.getDayByDate(CalendarUtils.selectedDate);
        Shift[] shifts;// = day.getShifts();

        List<String> shiftEmployeeNames = new ArrayList<>();
        shiftEmployeeNames.add("All Day");

        if (day != null) {
            shifts = day.getShifts();
            int employeeCount = 2;
            if (day.isBusyDay())
                employeeCount = 3;
            if (shifts != null) {
                if (shifts[0] != null) {
                    for (int i = 0; i < employeeCount; i++) {
                        Employee employee = shifts[0].getEmployees()[i];
                        if (employee != null)
                            shiftEmployeeNames.add(employee.getName());
                    }
                }
            }
        } else {
            shiftEmployeeNames.add("No one scheduled");
        }

        clearTextView();

        for(int i = 0; i < shiftEmployeeNames.size(); i++){
            morningTextView[i].setText(shiftEmployeeNames.get(i));
        }
    }

    private void setDayAdapter() {
        if (CalendarUtils.isWeekend()){
            setWeekendAdapter();
        } else {
            setWeekdayAdapter();
        }

    }

    public void viewCalendarActivity(View view) {
        Intent intent = new Intent(this, ViewCalendarActivity.class);
        startActivity(intent);
    }

    public void viewEmployees(View view){
        Intent intent = new Intent(this, ViewEmployeesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }
}