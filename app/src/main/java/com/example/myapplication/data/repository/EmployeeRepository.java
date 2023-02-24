package com.example.myapplication.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.myapplication.data.database.EmployeeDatabaseHelper;
import com.example.myapplication.domain.models.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Code for storing list of employees in memory
 */
public class EmployeeRepository {
    private List<Employee> employeeList = new ArrayList<>();
    private EmployeeDatabaseHelper db;
    private static int employeeCount;

    private static final EmployeeRepository instance = new EmployeeRepository();

    private EmployeeRepository(){
        employeeCount = 0;
    }

    public static EmployeeRepository getInstance(){
        return instance;
    }

    public void setContext(Context context){
        db = new EmployeeDatabaseHelper(context);
    }

    public List<Employee> getEmployeeList(){
        return db.getEmployees();
    }

    public Employee getEmployeeByID(int id){
        Employee employee;
        int index;
        int left;
        int right;

        employeeList = getEmployeeList();

        left = 0;
        right = employeeList.size() - 1;

        while (left <= right){
            index = (left + right)/2;
            employee = employeeList.get(index);
            if (employee.getId() < id)
                left = index + 1;
            else if (employee.getId() > id)
                right = index - 1;
            else
                return employee;
        }

        return null;
    }

    public int getEmployeePosition(Employee employee){
        employeeList = getEmployeeList();
        if(employee != null) {
            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getId() == employee.getId())
                    return i;
            }
        }
        return -1;
    }

    public boolean addEmployee(Employee employee){
        int id;
        employeeList = getEmployeeList();
        employeeCount = employeeList.size();
        for (int i = 0; i < employeeCount; i++){
            if (employee.getName().matches(employeeList.get(i).getName())){
                if (employee.getEmail().matches(employeeList.get(i).getEmail())){
                    return false;
                }
            }
        }

        if (employeeCount <= 0)
            id = 1;
        else
            id = employeeList.get(employeeCount - 1).getId() + 1;

        employee.setId(id);
        db.addEmployee(employee);
        return true;
    }

    public Employee removeEmployee(Employee employee){
        db.removeEmployee(employee);
        return employee;
    }

    public Employee updateEmployee(Employee employee){
        db.updateEmployee(employee);
        return employee;
    }
}
