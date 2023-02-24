package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.UI.Utilities.CalendarUtils;
import com.example.myapplication.data.repository.DayRepository;
import com.example.myapplication.domain.models.Day;
import com.example.myapplication.domain.models.Employee;
import com.example.myapplication.domain.models.Shift;
import com.example.myapplication.domain.models.ShiftTime;

import java.util.ArrayList;
import java.util.List;

public class ViewDayActivity extends AppCompatActivity {

    TextView dateText;
    TextView[] morningTextView = {null, null, null, null};
    TextView[] afternoonTextView = {null, null, null, null};
    Button editMorningButton;
    Button editAfternoonButton;
    Button editShiftButton;
    CheckBox isBusy;
    Day day;
    String previous;
    DayRepository dayRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day);

        dayRepository = DayRepository.getInstance();
        dayRepository.setContext(this);

        previous = getIntent().getStringExtra("previous");

        dateText = findViewById(R.id.dateTextView);
        morningTextView[0] = findViewById(R.id.morningTextView);
        morningTextView[1] = findViewById(R.id.dayEmployee1TextView);
        morningTextView[2] = findViewById(R.id.dayEmployee2TextView);
        morningTextView[3] = findViewById(R.id.dayEmployee3TextView);
        afternoonTextView[0] = findViewById(R.id.afternoonTextView);
        afternoonTextView[1] = findViewById(R.id.afternoonEmployee1TextView);
        afternoonTextView[2] = findViewById(R.id.afternoonEmployee2TextView);
        afternoonTextView[3] = findViewById(R.id.afternoonEmployee3TextView);
        editMorningButton = findViewById(R.id.editMorningButton);
        editAfternoonButton = findViewById(R.id.editAfternoonButton);
        editShiftButton = findViewById(R.id.editShiftButton);
        isBusy = findViewById(R.id.checkBox);

        dateText.setText(CalendarUtils.selectedDate.toString());
        day = dayRepository.getDayByDate(CalendarUtils.selectedDate);

        if(day != null){
            if (day.isBusyDay())
                isBusy.setChecked(true);
            else
                isBusy.setChecked(false);
        }

        setDayAdapter();

        addBoxOnClick();
    }

    public void clearTextView(){
        for (int i = 0; i < 4; i++){
            morningTextView[i].setText("");
            afternoonTextView[i].setText("");
        }
    }

    private void addBoxOnClick(){
        isBusy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day != null) {
                    day.setBusy();
                    dayRepository.updateDay(day);
                }
                else {
                    day = new Day(CalendarUtils.selectedDate);
                    day.setBusy();
                    dayRepository.addDay(day);
                }
                clearTextView();
                setDayAdapter();
            }
        });
    }

    private void setWeekdayAdapter(){
        Shift[] shifts;

        editShiftButton.setVisibility(View.GONE);
        editShiftButton.setEnabled(false);

        editMorningButton.setEnabled(true);
        editMorningButton.setVisibility(View.VISIBLE);
        editMorningButton.setOnClickListener(this::editMorning);
        editMorningButton.setText("Edit Morning");

        editAfternoonButton.setEnabled(true);
        editAfternoonButton.setVisibility(View.VISIBLE);
        editAfternoonButton.setOnClickListener(this::editAfternoon);
        editAfternoonButton.setText("Edit Afternoon");

        List<String> morningEmployeeNames = new ArrayList<>();
        List<String> afternoonEmployeeNames = new ArrayList<>();

        morningEmployeeNames.add("Morning:");
        afternoonEmployeeNames.add("Afternoon:");

        if (day != null) {
            shifts = day.getShifts();
            System.out.println(shifts[1]);
            int employeeCount = 2;

            if (shifts[0] != null) {

                if (day.isBusyDay())
                    employeeCount = 3;

                for (int i = 0; i < employeeCount; i++) {
                    Employee employee = shifts[0].getEmployees()[i];
                    if (employee != null)
                        morningEmployeeNames.add(employee.getName());
                }
            }
            else
                morningEmployeeNames.add("No one scheduled");

            if (shifts[1] != null) {

                if (day.isBusyDay())
                    employeeCount = 3;

                for (int i = 0; i < employeeCount; i++) {
                    Employee employee = shifts[1].getEmployees()[i];
                    if (employee != null)
                        afternoonEmployeeNames.add(employee.getName());
                }
            }
            else
                afternoonEmployeeNames.add("No one scheduled");

        } else {
            morningEmployeeNames.add("No one scheduled");
            afternoonEmployeeNames.add("No one scheduled");
        }

        for(int i = 0; i < morningEmployeeNames.size(); i++){
            morningTextView[i].setText(morningEmployeeNames.get(i));
        }

        for(int i = 0; i < afternoonEmployeeNames.size(); i++){
            afternoonTextView[i].setText(afternoonEmployeeNames.get(i));
        }

    }

    private void setWeekendAdapter(){
        Shift[] shifts;

        editShiftButton.setEnabled(true);
        editShiftButton.setVisibility(View.VISIBLE);
        editShiftButton.setOnClickListener(this::editMorning);
        editShiftButton.setText("Edit Shift");

        editAfternoonButton.setVisibility(View.GONE);
        editAfternoonButton.setEnabled(false);

        editMorningButton.setVisibility(View.GONE);
        editMorningButton.setEnabled(false);

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
            } else
                shiftEmployeeNames.add("No one scheduled");
        } else
            shiftEmployeeNames.add("No one scheduled");

        for(int i = 0; i < shiftEmployeeNames.size(); i++){
            morningTextView[i].setText(shiftEmployeeNames.get(i));
        }
    }

    private void setDayAdapter(){
        if (CalendarUtils.isWeekend())
            setWeekendAdapter();
        else
            setWeekdayAdapter();
    }

    private void editMorning(View view){
        Intent intent = new Intent(this, EditShiftActivity.class);
        intent.putExtra("time", ShiftTime.MORNING);
        intent.putExtra("previous", getIntent().getStringExtra("previous"));
        startActivity(intent);
    }

    private void editAfternoon(View view){
        Intent intent = new Intent(this, EditShiftActivity.class);
        intent.putExtra("time", ShiftTime.AFTERNOON);
        intent.putExtra("previous", getIntent().getStringExtra("previous"));
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent;
        if (getIntent().getStringExtra("previous").equals("week"))
            intent = new Intent(this, ViewWeeklyCalendarActivity.class);
        else
            intent = new Intent(this, ViewCalendarActivity.class);

        startActivity(intent);
    }
}