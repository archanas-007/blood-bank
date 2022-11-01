package com.manit.amit.bloodbank;

/**
 * Created by sAgAr SiNGH on 11-Dec-17.
 */

public class Req_model {

    public String full_name;
    public String contact;
    public String thumb_image;

    public Req_model(){

    }

    public Req_model(String full_name, String contact, String thumb_image) {
        this.full_name = full_name;
        this.contact = contact;
        this.thumb_image = thumb_image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getThumb_image() {
        return thumb_image;
    }
}
