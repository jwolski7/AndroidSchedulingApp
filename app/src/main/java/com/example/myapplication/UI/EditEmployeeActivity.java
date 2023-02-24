package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Employee;

public class EditEmployeeActivity extends AppCompatActivity {

    EmployeeRepository employeeRepository;
    int empPos;
    Employee employee;

    EditText firstName;
    EditText lastName;
    EditText phone;
    EditText email;
    SwitchCompat open;
    SwitchCompat close;



    Spinner[] spinList = {null, null, null, null, null, null, null};

    char[] availability;

    String[] weekdayAvailabilities = {"Unavailable", "Morning", "Afternoon", "Morning/Afternoon"};
    String[] weekendAvailabilities = {"Unavailable", "Available"};

    ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        Intent intent = getIntent();

        ArrayAdapter weekdayArrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, weekdayAvailabilities);
        ArrayAdapter weekendArrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, weekendAvailabilities);

        spinList[0] = findViewById(R.id.spinnerMon);
        spinList[1] = findViewById(R.id.spinnerTue);
        spinList[2] = findViewById(R.id.spinnerWed);
        spinList[3] = findViewById(R.id.spinnerThu);
        spinList[4] = findViewById(R.id.spinnerFri);
        spinList[5] = findViewById(R.id.spinnerSat);
        spinList[6] = findViewById(R.id.spinnerSun);

        for (int i = 0; i < 5; i++){
            spinList[i].setAdapter(weekdayArrayAdapter);
            spinList[i].setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                    String adapterViewString = adapterView.toString();
                    setAvailability(adapterViewString.substring(adapterViewString.length() - 4, adapterViewString.length()-1).toLowerCase(), l);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        for (int i = 5; i < 7; i++){
            spinList[i].setAdapter(weekendArrayAdapter);
            spinList[i].setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                    String adapterViewString = adapterView.toString();
                    setAvailability(adapterViewString.substring(adapterViewString.length() - 4, adapterViewString.length()-1).toLowerCase(), l);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        final String value = intent.getStringExtra("Place");
        empPos = Integer.parseInt(value);

        employee = employeeRepository.getEmployeeList().get(empPos);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        phone = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.editTextEmail);

        open = findViewById(R.id.switchOpen);
        close = findViewById(R.id.switchCLose);

        firstName.setText(employee.getFirstName());
        lastName.setText(employee.getLastName());
        phone.setText(employee.getPhoneNumber());
        email.setText(employee.getEmail());

        open.setChecked(employee.getOpen());
        close.setChecked(employee.getClose());

        availability = employee.getAvailability();

        for (int i = 0; i < 7; i++){
            int position = 0;

            switch (availability[i]){
                case 'n':
                    position = 0;
                    break;
                case 'm':
                    position = 1;
                    break;
                case 'a':
                    position = 2;
                    break;
                case 'b':
                    position = 3;
            }
            spinList[i].setSelection(position);
        }

        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(this::save);
    }

    void save(View view){

        employee.setFirstName(firstName.getText().toString());
        employee.setLastName(lastName.getText().toString());
        employee.setEmail(email.getText().toString());
        employee.setPhoneNumber(phone.getText().toString());

        if (open.isChecked())
            employee.setOpen(true);

        if (close.isChecked())
            employee.setClose(true);

        employee.setAvailability(availability);

        employeeRepository.updateEmployee(employee);

        Intent intent = new Intent(this, EmployeeInfoActivity.class);
        intent.putExtra("Place", String.valueOf(empPos));
        startActivity(intent);
    }

    int dayStringToInt(String day){
        int nDay = 0;

        switch (day){
            case "mon":
                nDay = 0;
                break;
            case "tue":
                nDay = 1;
                break;
            case "wed":
                nDay = 2;
                break;
            case "thu":
                nDay = 3;
                break;
            case "fri":
                nDay = 4;
                break;
            case "sat":
                nDay = 5;
                break;
            case "sun":
                nDay = 6;
        }

        return nDay;
    }

    void setAvailability(String day, long l){
        char avail;
        int nDay;

        nDay = dayStringToInt(day);

        if (l == 0)
            avail = 'n';
        else if (l == 1)
            avail = 'm';
        else if (l == 2)
            avail = 'a';
        else
            avail = 'b';

        availability[nDay] = avail;
    }
}