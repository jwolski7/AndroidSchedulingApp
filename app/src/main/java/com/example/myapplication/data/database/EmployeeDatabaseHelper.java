package com.example.myapplication.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.domain.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDatabaseHelper extends SQLiteOpenHelper{

    private static final String TABLE_NAME = "employee_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "employeeID";
    private static final String COL3 ="firstName";
    private static final String COL4 = "lastName";
    private static final String COL5 = "open";
    private static final String COL6 = "close";
    private static final String COL7 = "availability";
    private static final String COL8 = "phoneNumber";
    private static final String COL9 = "email";

    public EmployeeDatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableInit = "CREATE TABLE " + TABLE_NAME + "("+COL1+ " INTEGER PRIMARY KEY, " +
                COL2+" TEXT, "+ COL3+" TEXT, "+COL4+" TEXT, "+COL5+" TEXT, "+
                COL6+" TEXT, "+COL7+" TEXT, "+COL8+ " TEXT, "+COL9+" TEXT)";
        sqLiteDatabase.execSQL(tableInit);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //Add employee to database
    public boolean addEmployee(Employee employee){
        SQLiteDatabase db;
        ContentValues content;

        String availability = new String(employee.getAvailability());
        String open;
        String close;

        if (employee.getOpen()) {
            open = "true";
        }
        else {open = "false";}

        if (employee.getClose()) {
            close = "true";
        } else {close = "false";}

        db = this.getWritableDatabase();
        content = new ContentValues();
        content.put(COL2, employee.getId());
        content.put(COL3, employee.getFirstName());
        content.put(COL4, employee.getLastName());
        content.put(COL5, open);
        content.put(COL6, close);
        content.put(COL7, availability);
        content.put(COL8, employee.getPhoneNumber());
        content.put(COL9, employee.getEmail());

        long record = db.insert(TABLE_NAME, null, content);
        return record != -1;
    }

    public boolean updateEmployee(Employee employee) {
        SQLiteDatabase db;
        ContentValues content;
        String open;
        String close;

        int id = employee.getId();
        String avail = new String(employee.getAvailability());

        if (employee.getOpen()) {
            open = "true";
        }
        else{open="false";}

        if (employee.getClose()) {
            close = "true";
        } else { close = "false"; }

        db = this.getWritableDatabase();
        content = new ContentValues();
        content.put(COL2, employee.getId());
        content.put(COL3, employee.getFirstName());
        content.put(COL4, employee.getLastName());
        content.put(COL5, open);
        content.put(COL6, close);
        content.put(COL7, avail);
        content.put(COL8, employee.getPhoneNumber());
        content.put(COL9, employee.getEmail());

        db.update(TABLE_NAME, content, "employeeID = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    //convert database to array list for repository
    public List<Employee> getEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        result.moveToFirst();

        String id;
        String firstName;
        String lastName;
        String openString;
        String closeString;
        String phoneNumber;
        String email;
        String availabilityString;

        while (!result.isAfterLast()) {

            //Extract data from database
            char[] availability;
            id = result.getString(result.getColumnIndexOrThrow(COL2));
            firstName = result.getString(result.getColumnIndexOrThrow(COL3));
            lastName = result.getString((result.getColumnIndexOrThrow(COL4)));
            openString = result.getString((result.getColumnIndexOrThrow(COL5)));
            closeString = result.getString((result.getColumnIndexOrThrow(COL6)));
            boolean open, close;

            open = openString.matches("true");

            close = closeString.matches("true");

            phoneNumber = result.getString((result.getColumnIndexOrThrow(COL8)));
            email = result.getString((result.getColumnIndexOrThrow(COL9)));

            availabilityString = result.getString(result.getColumnIndexOrThrow(COL7));

            availability = availabilityString.toCharArray();

            //Create new employee and edit avail/open close
            Employee emp = new Employee(firstName, lastName, phoneNumber, email);
            emp.setId(Integer.parseInt(id));
            emp.setAvailability(availability);
            emp.setOpen(open);
            emp.setClose(close);

            employeeList.add(emp);

            result.moveToNext();
        }

        result.close();

        return employeeList;
    }

    //delete employee
    public Integer removeEmployee(Employee employee){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL2+" = ? ", new String[]{Integer.toString(employee.getId())});
    }

//    public Cursor getEmployee(Employee employee){
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT "+ COL1+" FROM "+TABLE_NAME+" WHERE "+COL1+" = "+ employee.getId();
//        return db.rawQuery(query, null);
//    }
}

