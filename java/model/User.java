package com.example.tourbud5.model;

public class User {
    User(){ }
    User(UserBuilder.userBuilder builder){
        this.firstName=builder.getFirstName();

        this.description=builder.getDescription();
        this.password=builder.getPassword();
        this.phone=builder.getPhone();



    }



    private String firstName;
    private String description;
    private String password;
    private String phone;


    //needs public getter and setter as firestore requires public getters and setters to save to database


    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }




    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }





    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }



    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }



}
