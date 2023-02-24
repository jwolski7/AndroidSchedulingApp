package com.example.myapplication.domain.models;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private Boolean open = false;
    private Boolean close = false;
    private char[] availability = {'n', 'n', 'n', 'n', 'n', 'n', 'n'};
    private String phoneNumber;
    private String email;


    public Employee(String firstName, String lastName, String phoneNumber, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getId(){ return id; }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){ return firstName + " " + lastName; }

    public void setName(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() { return phoneNumber; }
  
    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOpen(Boolean open){
        this.open = open;
    }

    public void setClose(Boolean close){ this.close = close; }

    public Boolean getOpen(){
        return open;
    }

    public Boolean getClose(){
        return close;
    }

    public void setAvailability(char[] availability){
        this.availability = availability;
    }

    public char[] getAvailability(){
        return availability;
    }
}
