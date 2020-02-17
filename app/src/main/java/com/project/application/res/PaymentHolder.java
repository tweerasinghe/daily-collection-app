package com.project.application.res;

public class PaymentHolder {
    private String ticketID, vehicleNo, date, time,balance;

    public PaymentHolder(String ticketID, String vehicleNo, String date, String time, String balance) {
        this.ticketID = ticketID;
        this.vehicleNo = vehicleNo;
        this.date = date;
        this.time = time;
        this.balance = balance;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
