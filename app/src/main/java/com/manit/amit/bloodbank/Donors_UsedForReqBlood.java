package com.manit.amit.bloodbank;

/**
 * Created by LENOVO on 08-12-2017.
 */

public class Donors_UsedForReqBlood {

    public String address;
    public String age;
    public String blood;
    public String city;
    public String contact;
    public String fname;
    public String gender;
    public String image;
    public String thumb_image;
    public String lname;
    public String state;
    public String weight;
    public String full_name;


    public Donors_UsedForReqBlood(){
        //Empty Constructor
    }

    public Donors_UsedForReqBlood(String address, String age, String blood, String city, String contact,
                                  String fname, String gender, String image, String thumb_image, String lname, String state, String weight, String full_name) {
        this.address = address;
        this.age = age;
        this.blood = blood;
        this.city = city;
        this.contact = contact;
        this.fname = fname;
        this.gender = gender;
        this.image = image;
        this.thumb_image = thumb_image;
        this.lname = lname;
        this.state = state;
        this.weight = weight;
        this.full_name = full_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
