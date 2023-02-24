package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.EmployeeRepository;
import com.example.myapplication.domain.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class ViewEmployeesActivity extends AppCompatActivity {

    EmployeeRepository employeeRepository;

    @Override
    /**
     * Displays the employees that are in the repository and give the user
     * a few navigation options.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);

        employeeRepository = EmployeeRepository.getInstance();
        employeeRepository.setContext(this);

        ListView list = findViewById(R.id.viewList);

        ImageButton addEmployeeButton = findViewById(R.id.buttonAddEmployee);

        ImageButton homeButton = findViewById(R.id.buttonHome);

        ArrayList<String> names = new ArrayList<>();

        List<Employee> empList = employeeRepository.getEmployeeList();

        for (int nm = 0; nm < empList.size(); nm++) {
            names.add(empList.get(nm).getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        list.setAdapter(adapter);

        addEmployeeButton.setOnClickListener(this::addEmployee);

        homeButton.setOnClickListener(this::home);

        /**
         * Take the user to a new page to see all the information about an employee.
         */
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), EmployeeInfoActivity.class);
            intent.putExtra("Place", String.valueOf(i));
            startActivity(intent);
        });
    }

    /**
     * Take the user to the ad employee page.
     * @param view
     */
    public void addEmployee(View view){
        Intent intent = new Intent(this, AddEmployeeActivity.class);
        startActivity(intent);
    }

    /**
     * Take the user to the home page.
     * @param view
     */
    public void home(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Take the user to the home page.
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}