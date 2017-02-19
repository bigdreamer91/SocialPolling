package edu.iastate.cs510.company2.models;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by David on 10/11/2016.
 */

public class User {
    public User(){

    }

    //modified the constructor to include the list preferred categories
    public User(String username, String email, String password, String salt, String name, String birthdate, String gender, String city, String country, ArrayList<String> categories) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.categories = categories;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return email.equals(user.email);// || username.equals(user.username);

    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    private String username;
    private String email;
    private String password;
    private String name;
    private String birthdate;
    private String gender;
    private String city;
    private String country;
    private String salt;
    private ArrayList<String> categories; // included this to keep a list of preferred categories

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getSalt() { return this.salt;}

    public void setSalt(String s) { this.salt = s;}

    public User(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getPreferredCategories(){return categories;}


    public boolean comparePasswords(String password) {
        return this.password.equals(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public  void setPreferredCategories(ArrayList<String> categories) {this.categories = categories;}

    public void setPassword(String password) {this.password = password;}
}