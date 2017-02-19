package edu.iastate.cs510.company2.models;

import java.util.Collection;

/**
 * Created by David on 10/7/2016.
 */

public class Poll implements IPoll {
    private String creator; //TODO: Change to User object
    private transient long created; //Transient means that it will be ignored by the marshaller
    private String question;
    private Collection<Choice> choices;
    private String category;
    private String image;
    private String link;

    public void setLink(String link) {
        this.link = link;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setQuestion(String question) {this.question=question;}
    public void setChoices(Collection<Choice> choices) {this.choices=choices;}
    public void setCreator(String creator) {this.creator=creator;}

    public Poll(){}

    public Poll(String user, String category, String question, Collection<Choice> choices){
        creator = user;
        created = System.currentTimeMillis();
        this.category = category;
        this.question = question;
        this.choices = choices;
    }

    public Poll(String user, String category, String question, Collection<Choice> choices, String image,String link){

        this.image=image;
        creator = user;
        created = System.currentTimeMillis();
        this.category = category;
        this.question = question;
        this.choices = choices;
        this.link = link;
        this.link=link;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreated() {
        return created;
    }

    public String getQuestion() {
        return question;
    }

    public Collection<Choice> getChoices() {
        return choices;
    }

    public String getCategory(){return category;}


    public String getImageString() {
        return image;
    }

    public String getLink() { return link; }

}
