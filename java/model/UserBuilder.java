package com.example.tourbud5.model;

public class UserBuilder {
    public static class userBuilder{

        //user builder needs to be a seperate class otherwise it will get saved into the database with the user class

        public userBuilder(){}



        private String firstName;
        private String description;
        private String password;
        private String phone;


        public String getDescription() {
            return description;
        }

        public String getPassword() {
            return password;
        }

        public String getPhone() {
            return phone;
        }

        public String getFirstName() {
            return firstName;
        }






        public userBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }






        public userBuilder setDescription(String description) {
            this.description = description;
            return this;
        }






        public userBuilder setPassword(String password) {
            this.password = password;
            return this;
        }



        public userBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build(){
            return new User(this); }


    }
}
