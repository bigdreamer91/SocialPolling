package edu.iastate.cs510.company2.testsupport;

import java.util.ArrayList;

import edu.iastate.cs510.company2.models.User;

/**
 * Created by Guru on 10/28/2016.
 */

public class MockUser extends User
{
    private String clearPassword;

    public MockUser(String username, String email, String salted, String salt, String clear, String name, String birthday, String gender, String city, String country, ArrayList<String> categories){
        super(username, email, salted, salt, name, birthday, gender, city, country, categories);
        clearPassword = clear;

    }

    public String getClearPassword(){
        return clearPassword;
    }
}