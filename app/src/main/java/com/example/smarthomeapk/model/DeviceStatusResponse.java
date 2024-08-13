package com.example.smarthomeapk.model;

public class DeviceStatusResponse {
    private int id;
    private String name;
    private String status;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {

        return name;
    }
}
