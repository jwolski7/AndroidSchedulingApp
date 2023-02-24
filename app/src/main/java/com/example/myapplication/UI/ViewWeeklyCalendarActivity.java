package com.example.myapplication.UI;

import static com.example.myapplication.UI.Utilities.CalendarUtils.daysInWeekArray;
import static com.example.myapplication.UI.Utilities.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UI.Utilities.CalendarAdapter;
import com.example.myapplication.UI.Utilities.CalendarUtils;
import com.example.myapplication.data.repository.DayRepository;
import com.example.myapplication.domain.models.Day;
import com.example.myapplication.domain.models.Employee;
import com.example.myapplication.domain.models.Shift;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ViewWeeklyCalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private TextView[] morningTextView = {null, null, null, null};
    private TextView[] afternoonTextView = {null, null, null, null};
    private Button editScheduleButton;
    private DayRepository dayRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_weekly_calendar);

        dayRepository = DayRepository.getInstance();
        dayRepository.setContext(this);

        initWidgets();
        setWeekView();

    }

    private void setWeekView() {

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setDayAdapter();

        editScheduleButton.setOnClickListener(this::viewDayAction);
    }


    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        morningTextView[0] = findViewById(R.id.morningTextView);
        morningTextView[1] = findViewById(R.id.dayEmployee1TextView);
        morningTextView[2] = findViewById(R.id.dayEmployee2TextView);
        morningTextView[3] = findViewById(R.id.dayEmployee3TextView);
        afternoonTextView[0] = findViewById(R.id.afternoonTextView);
        afternoonTextView[1] = findViewById(R.id.afternoonEmployee1TextView);
        afternoonTextView[2] = findViewById(R.id.afternoonEmployee2TextView);
        afternoonTextView[3] = findViewById(R.id.afternoonEmployee3TextView);
        editScheduleButton = findViewById(R.id.editScheduleButton);
    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();

    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void viewDayAction(View view){
        Intent intent = new Intent(this, ViewDayActivity.class);
        intent.putExtra("previous", "week");
        startActivity(intent);
    }

    private void setDayAdapter() {
        if (CalendarUtils.isWeekend()){
            setWeekendAdapter();
        } else {
            setWeekdayAdapter();
        }

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ViewCalendarActivity.class);
        startActivity(intent);
    }
}