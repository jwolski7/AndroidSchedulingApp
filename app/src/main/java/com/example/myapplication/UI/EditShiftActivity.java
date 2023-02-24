package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.UI.Utilities.CalendarUtils;
import com.example.myapplication.data.repository.DayRepository;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Day;
import com.example.myapplication.domain.models.Employee;
import com.example.myapplication.domain.models.Shift;
import com.example.myapplication.domain.models.ShiftTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditShiftActivity extends AppCompatActivity {

    EmployeeRepository employeeRepository;
    DayRepository dayRepository;
    Day day;
    Shift shift1;
    ShiftTime time;

    List<Employee> empList;

    Button button;

    CheckBox forceBox;
    boolean force = false;
    boolean modified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shift);

        dayRepository =DayRepository.getInstance();
        dayRepository.setContext(this);

        time = (ShiftTime) getIntent().getSerializableExtra("time");

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        TextView employee3TextView = findViewById(R.id.textView4);

        forceBox = findViewById(R.id.forceShiftCheckBox);

        Spinner spinner1 = findViewById(R.id.emp1Spinner);
        Spinner spinner2 = findViewById(R.id.emp2Spinner);
        Spinner spinner3 = findViewById(R.id.emp3Spinner);

        empList = employeeRepository.getEmployeeList();

        ArrayList<String> names = new ArrayList<>();

        day = dayRepository.getDayByDate(CalendarUtils.selectedDate);

        //Start a new day if the day schedule doesn't yet exist
        if (day == null) {
            day = new Day(CalendarUtils.selectedDate);
            shift1 = new Shift(time);
        }
        else
            modified = true;
            if (day.getShiftByTime(time) == null)
                shift1 = new Shift(time);
            else
                shift1 = day.getShiftByTime(time);

        //Set the spinners default option to select name
        names.add(0, "Select name");

        boolean ava;
        //Store the names of each employee in a list. -1 due to the addition of "Select name"
        for (int nm = 0; nm < empList.size(); nm++) {
            ava = available(empList.get(nm));
            String nameHolder = trainingCheck(empList.get(nm));
            System.out.println(empList.get(nm).getName());
            System.out.println(ava);
            if (ava == true) {
                names.add(nameHolder);
            }
        }

        button = findViewById(R.id.addButton);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);

        spinner1.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);

        // Listener for spinner 1. Adds selected employee to a shift.
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select name")) {
                    shift1.addEmployee(0, null);
                }
                else {
                    shift1.addEmployee(0, empCheck(adapterView.getItemAtPosition(i).toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (shift1.getEmployees()[0] != null)
            spinner1.setSelection(names.indexOf(trainingCheck(shift1.getEmployees()[0])));
        else
            spinner1.setSelection(0);

        // Listener for spinner 2. Adds selected employee to a shift.
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                if (adapterView.getItemAtPosition(j).equals("Select name")) {
                    shift1.addEmployee(1, null);
                }
                else {
                    shift1.addEmployee(1, empCheck(adapterView.getItemAtPosition(j).toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (shift1.getEmployees()[1] != null)
            spinner2.setSelection(names.indexOf(trainingCheck(shift1.getEmployees()[1])));
        else
            spinner2.setSelection(0);

        if (day.isBusyDay()){
            spinner3.setAdapter(dataAdapter);
            employee3TextView.setText("Employee 3");
            spinner3.setVisibility(View.VISIBLE);

            spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int k, long l) {
                    if (adapterView.getItemAtPosition(k).equals("Select name")) {
                        shift1.addEmployee(2, null);
                    }
                    else {
                        shift1.addEmployee(2, empCheck(adapterView.getItemAtPosition(k).toString()));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (shift1.getEmployees()[2] != null)
                spinner3.setSelection(names.indexOf(trainingCheck(shift1.getEmployees()[2])));
            else
                spinner3.setSelection(0);

        } else {
            spinner3.setVisibility(View.GONE);
        }

        forceBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forceBox.isChecked()) {
                    force = true;
                }
                else {
                    force = false;
                }
            }
        });

        button.setOnClickListener(this::addButton);
        System.out.println("End");
    }


    public void addButton(View view) {
        Intent intent = new Intent(this, ViewDayActivity.class);
        intent.putExtra("previous", getIntent().getStringExtra("previous"));
        TextView error = (TextView) findViewById(R.id.trainingErrorTextView);

        // Force the shift to be set
        if (force == true) {
            day.setShift(shift1);
            if (modified)
                dayRepository.updateDay(day);
            else
                dayRepository.addDay(day);

            startActivity(intent);
        }

        // Only add a shift to a day if all tests are passed. Otherwise display an error message.
        else {
            if (day.isBusyDay() == true) {
                if (shift1.getEmployees()[0] == null || shift1.getEmployees()[1] == null || shift1.getEmployees()[2] == null) {
                    error.setText("Not all shift slots are filled");
                }
                else {
                    if (shiftCheck() == true) {
                        if (recurring() == true) {
                            error.setText("Recurring employee.");

                        }
                        else {
                            day.setShift(shift1);
                            if (modified)
                                dayRepository.updateDay(day);
                            else
                                dayRepository.addDay(day);
                            startActivity(intent);
                        }
                    }
                    else {
                        error.setText("Employees don't have the necessary training for this shift.");
                    }
                }
            }

            else {
                if (shift1.getEmployees()[0] == null || shift1.getEmployees()[1] == null) {
                    error.setText("Not all shift slots are filled");
                }
                else {
                    if (shiftCheck() == true) {
                        if (recurring() == true) {
                            error.setText("Recurring employee.");
                        }

                        else {
                            day.setShift(shift1);
                            if (modified)
                                dayRepository.updateDay(day);
                            else
                                dayRepository.addDay(day);
                            startActivity(intent);
                        }
                    }
                    else {
                        error.setText("Employees don't have the necessary training for this shift.");
                    }
                }
            }
        }
    }

    /*
     * Checks what days an employee is available for.
     */
    public boolean available(Employee employee) {
        int pos = 0;

        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().toString();

        if (dayOfWeek.equals("MONDAY")) {
            pos = 0;
        }
        else if (dayOfWeek.equals("TUESDAY")) {
            pos = 1;
        }
        else if (dayOfWeek.equals("WEDNESDAY")) {
            pos = 2;
        }
        else if (dayOfWeek.equals("THURSDAY")) {
            pos = 3;
        }
        else if (dayOfWeek.equals("FRIDAY")) {
            pos = 4;
        }
        else if (dayOfWeek.equals("SATURDAY")) {
            pos = 5;
        }
        else if (dayOfWeek.equals("SUNDAY")) {
            pos = 6;
        }

        char c = employee.getAvailability()[pos];

        int compA = Character.compare(c, 'a');
        int compM = Character.compare(c, 'm');
        int compN = Character.compare(c, 'n');
        int compB = Character.compare(c, 'b');

        if (compN == 0) {
            return false;
        }
        else if (compM == 0 && String.valueOf(time).equals("MORNING")) {
            return true;
        }
        else if (compA == 0 && String.valueOf(time).equals("AFTERNOON")) {
            return true;
        }
        else if (compB == 0) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
     * Matches the selected employee with the correct one in the database.
     */
    public Employee empCheck(String name) {
        StringBuffer str1;
        str1 = new StringBuffer(name);
        str1.deleteCharAt(str1.length() - 1);
        StringBuffer str2;
        str2 = new StringBuffer(str1);
        str2.deleteCharAt(str2.length() - 1);

        for (int i = 0; i < empList.size(); i++) {
            if (empList.get(i).getName().equals(name)) {
                return empList.get(i);
            }

            else if (empList.get(i).getName().equals(String.valueOf(str1))) {
                return empList.get(i);
            }

            else if (empList.get(i).getName().equals(String.valueOf(str2))) {
                return empList.get(i);
            }
        }
        return null;
    }



    /*
     * Check to see if an employee has training for their shift. If yes add a symbol to the end of their name.
     */
    public String trainingCheck(Employee employee) {
        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().toString();
        System.out.println("We get here 9");
        String newEmp;
        if (dayOfWeek.equals("SATURDAY") || dayOfWeek.equals("SUNDAY")) {
            if (employee.getOpen() == true) {
                newEmp = employee.getName() + "*";
            }
            else {
                newEmp = employee.getName();
            }

            if (employee.getClose() == true) {
                newEmp = newEmp + "^";
            }

            return newEmp;
        }
        else {
            if (String.valueOf(time).equals("MORNING")) {
                if (employee.getOpen() == true) {
                    return employee.getName() + "*";
                }
            } else if (String.valueOf(time).equals("AFTERNOON")) {
                if (employee.getClose() == true) {
                    return employee.getName() + "^";
                }
            }

            return employee.getName();
        }
    }

    /*
     * Makes sure a employee has the necessary training for their shift
     */
    public boolean shiftCheck() {
        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().toString();
        int weekendCounter = 0;
        if (day.isBusyDay() == false) {
            if (!(dayOfWeek.equals("SATURDAY")) && !(dayOfWeek.equals("SUNDAY"))) {
                if (String.valueOf(time).equals("AFTERNOON")) {
                    if (shift1.getEmployees()[0].getClose() == true || shift1.getEmployees()[1].getClose() == true) {
                        return true;
                    }
                }
                else if (String.valueOf(time).equals("MORNING")) {
                    if (shift1.getEmployees()[0].getOpen() == true || shift1.getEmployees()[1].getOpen() == true) {
                        return true;
                    }
                }
            }
            else if (dayOfWeek.equals("SATURDAY") || dayOfWeek.equals("SUNDAY")) {
                if (shift1.getEmployees()[0].getOpen() == true || shift1.getEmployees()[1].getOpen() == true) {
                    weekendCounter++;
                }
                if (shift1.getEmployees()[0].getClose() == true || shift1.getEmployees()[1].getClose() == true) {
                    weekendCounter++;
                }
            }
        }

        else {
            if (!(dayOfWeek.equals("SATURDAY")) && !(dayOfWeek.equals("SUNDAY"))) {
                if (String.valueOf(time).equals("AFTERNOON")) {
                    if (shift1.getEmployees()[0].getClose() == true || shift1.getEmployees()[1].getClose() == true || shift1.getEmployees()[2].getClose() == true) {
                        return true;
                    }
                }
                else if (String.valueOf(time).equals("MORNING")) {
                    if (shift1.getEmployees()[0].getOpen() == true || shift1.getEmployees()[1].getOpen() == true || shift1.getEmployees()[2].getOpen() == true) {
                        return true;
                    }
                }
            }
            else if (dayOfWeek.equals("SATURDAY") || dayOfWeek.equals("SUNDAY")) {
                if (shift1.getEmployees()[0].getOpen() == true || shift1.getEmployees()[1].getOpen() == true || shift1.getEmployees()[2].getOpen() == true) {
                    weekendCounter++;
                }
                if (shift1.getEmployees()[0].getClose() == true || shift1.getEmployees()[1].getClose() == true || shift1.getEmployees()[2].getClose() == true) {
                    weekendCounter++;
                }

            }
        }

        if (weekendCounter == 2) {
            return true;
        }

        return false;
    }

    /*
     * Checks to see if an employee was added to a shift twice.
     */
    public boolean recurring() {
        if (day.isBusyDay() == true) {
            if (shift1.getEmployees()[0].getName().equals(shift1.getEmployees()[1].getName())
                    || shift1.getEmployees()[0].getName().equals(shift1.getEmployees()[2].getName())
                            || shift1.getEmployees()[1].getName().equals(shift1.getEmployees()[2].getName())) {

                return true;

            }
        }

        else {
            if (shift1.getEmployees()[0].getName().equals(shift1.getEmployees()[1].getName())) {
                return true;
            }
        }
        return false;
    }

    public void onBackPressed(){
        Intent intent = new Intent(this, ViewDayActivity.class);
        intent.putExtra("previous", getIntent().getStringExtra("previous"));
        startActivity(intent);
    }

}