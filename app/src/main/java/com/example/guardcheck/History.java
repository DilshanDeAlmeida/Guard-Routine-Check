package com.example.guardcheck;

public class History {

    private String Date;
    private String Location;
    private String Remark;
    private String Guard;


    public History(String date, String location, String remark, String guard) {
        Date = date;
        Location = location;
        Remark = remark;
        Guard = guard;
    }


    public String getDate() {
        return Date;
    }

    public String getLocation() {
        return Location;
    }

    public String getRemark() {
        return Remark;
    }

    public String getGuard() {
        return Guard;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public void setGuard(String guard) {
        Guard = guard;
    }






}
