package edu.iastate.cs510.company2.models;

/**
 * Created by David on 10/7/2016.
 */

public interface IPoll {
    public static class Choice {
        public String choice;
        public int votes;
        public Choice(String choice, int votes){
            this.choice = choice;
            this.votes = votes;
        }
    }
}
