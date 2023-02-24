package com.example.myapplication.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class EmployeeInfoActivity extends AppCompatActivity {

    EmployeeRepository employeeRepository;
    Employee employee;
    ImageButton editButton;
    ImageButton homeButton;
    ImageButton deleteButton;

    @Override
    /**
     * Display a single employees information and give a few navigation options to
     * the user.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        TextView nameText = (TextView) findViewById(R.id.nameEntry);
        TextView emailText = (TextView) findViewById(R.id.emailEntry);
        TextView phoneText = (TextView) findViewById(R.id.phoneEntry);
        TextView openText = (TextView) findViewById(R.id.openEntry);
        TextView closeText = (TextView) findViewById(R.id.closeEntry);


        TextView monText = (TextView) findViewById(R.id.monAva);
        TextView tueText = (TextView) findViewById(R.id.tueAva);
        TextView wedText = (TextView) findViewById(R.id.wedAva);
        TextView thurText = (TextView) findViewById(R.id.thurAva);
        TextView friText = (TextView) findViewById(R.id.friAva);
        TextView satText = (TextView) findViewById(R.id.satAva);
        TextView sunText = (TextView) findViewById(R.id.sunAva);



        editButton = findViewById(R.id.editButton);
        homeButton = findViewById(R.id.homeButton);
        deleteButton = findViewById(R.id.deleteButton);

        List<Employee> employeeList = employeeRepository.getEmployeeList();


        Intent intent = getIntent();

        final String value = intent.getStringExtra("Place");
        int empPos = Integer.parseInt(value);

        employee = employeeList.get(empPos);

        nameText.setText(employee.getName());
        emailText.setText(employee.getEmail());
        phoneText.setText(employee.getPhoneNumber());

        if(employee.getOpen())
            openText.setText("Open.");
        else
            openText.setText("");

        if(employee.getClose())
            closeText.setText("Close.");
        else
            closeText.setText("");

        monText.setText(setAvailability(employeeList.get(empPos).getAvailability()[0]));
        tueText.setText(setAvailability(employeeList.get(empPos).getAvailability()[1]));
        wedText.setText(setAvailability(employeeList.get(empPos).getAvailability()[2]));
        thurText.setText(setAvailability(employeeList.get(empPos).getAvailability()[3]));
        friText.setText(setAvailability(employeeList.get(empPos).getAvailability()[4]));
        satText.setText(setAvailability(employeeList.get(empPos).getAvailability()[5]));
        sunText.setText(setAvailability(employeeList.get(empPos).getAvailability()[6]));

        editButton.setOnClickListener(view -> editEmployee(empPos));

        homeButton.setOnClickListener(this::home);

        /**
         * When the delete button is pressed a popup text box appears to confirm the
         * users choice
         */
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeInfoActivity.this);

            builder.setCancelable(true);
            builder.setTitle("Deleting Employee");
            builder.setMessage("Are you sure you want to remove this employee?");

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

            builder.setPositiveButton("OK", (dialogInterface, i) -> deleteEmployee(employee));
            builder.show();
        });


    }

    /**
     * Removes selected employee from the repository and sends the user back to the
     *  home screen.
     */
    public void deleteEmployee(Employee employee) {
        employeeRepository.removeEmployee(employee);
        Intent intent = new Intent(this, ViewEmployeesActivity.class);
        startActivity(intent);
    }

    /**
     * Sends the user to the edit employee screen.
     */
    public void editEmployee(Integer empPos) {
        Intent intent = new Intent(this, EditEmployeeActivity.class);
        intent.putExtra("Place", String.valueOf(empPos));
        startActivity(intent);
    }

    /**
     * Sends the user to the home screen.
     * @param view
     */
    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String setAvailability(char c) {
        if (c == 'n')
            return "NA";

        else if (c == 'm')
            return "Morning";

        else if (c == 'a')
            return "Afternoon";

        else if (c == 'b')
            return "Both";

        return "Fail";
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ViewEmployeesActivity.class);
        startActivity(intent);
    }


}