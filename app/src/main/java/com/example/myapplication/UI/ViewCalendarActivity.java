package com.example.myapplication.UI;

import static com.example.myapplication.UI.Utilities.CalendarUtils.daysInMonthArray;
import static com.example.myapplication.UI.Utilities.CalendarUtils.monthYearFromDate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.UI.Utilities.CalendarAdapter;
import com.example.myapplication.UI.Utilities.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;


public class ViewCalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button viewDayButton;
    public String dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);
        initWidgets();
        setMonthView();
        viewDayButton.setOnClickListener(this::viewDay);
    }


    private void setMonthView() {

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }


    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        viewDayButton = findViewById(R.id.viewDayButton);
    }


    public void previousMonthAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();

    }


    public void nextMonthAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }


    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null){
            CalendarUtils.selectedDate = date;
            System.out.println(date.getDayOfWeek().toString());
            dayOfWeek = date.getDayOfWeek().toString();
            setMonthView();
        }
    }

    public void weeklyAction(View view){
        Intent intent = new Intent(this, ViewWeeklyCalendarActivity.class);
        startActivity(intent);
    }

    public void viewDay(View view){
        Intent intent = new Intent(this, ViewDayActivity.class);
        intent.putExtra("previous", "month");
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
