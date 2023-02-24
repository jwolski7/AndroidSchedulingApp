package com.example.myapplication.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Employee;

/**
 * Code for the add employee page.
 */
public class AddEmployeeActivity extends AppCompatActivity {

    EmployeeRepository employeeRepository;
    Button addEmployeeButton;
    @Override
    /**
     * Create the page to add employees.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        addEmployeeButton = findViewById(R.id.add_button);
        addEmployeeButton.setOnClickListener(this::addEmployee);

    }

    /**
     * Add employees to the employee repository on button click.
     * @param view
     */
    public void addEmployee(View view) {

        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText phone = findViewById(R.id.editTextPhone);
        EditText email = findViewById(R.id.editTextEmail);
        TextView displayMessage = findViewById(R.id.addMessage);

        // ensure all fields are filled
        if (firstName.getText().toString().matches("") || lastName.getText().toString().matches(""))
            displayMessage.setText("Require a first and last name.");
        else if (phone.getText().toString().matches(""))
            displayMessage.setText("Require a phone number");
        else if (email.getText().toString().matches(""))
            displayMessage.setText("Require email");
        else {
            Employee employee = new Employee(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), email.getText().toString());
            if(employeeRepository.addEmployee(employee)) {
                // immediately return to ViewEmployeesActivity when a new employee is added
                Intent intent = new Intent(this, ViewEmployeesActivity.class);
                startActivity(intent);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployeeActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Employee Error");
                builder.setMessage("Employee Already exists");
                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                builder.show();
            }
        }
    }
}