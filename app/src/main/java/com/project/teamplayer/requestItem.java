package com.project.teamplayer;


//Request item class
public class requestItem  {
    private String message;
    private String email;
    private String activityName;



    public requestItem(String message, String email , String activityName){
        this.message=message;
        this.email = email;
        this.activityName = activityName;

    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String email) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String email) {
        this.activityName = activityName;
    }



}
