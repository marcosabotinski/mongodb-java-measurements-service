package com.sabotinski.mongodbexample.measurementsservice.api.models;

import java.time.LocalDateTime;

import com.google.gson.Gson;

public class Measurement {
    private String device;
    private LocalDateTime ts;
    private int temperature;
    private int angle;
    private int rpm;
    private String status;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + angle;
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + rpm;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + temperature;
        result = prime * result + ((ts == null) ? 0 : ts.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Measurement other = (Measurement) obj;
        if (angle != other.angle)
            return false;
        if (device == null) {
            if (other.device != null)
                return false;
        } else if (!device.equals(other.device))
            return false;
        if (rpm != other.rpm)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (temperature != other.temperature)
            return false;
        if (ts == null) {
            if (other.ts != null)
                return false;
        } else if (!ts.equals(other.ts))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Measurement() {
    }

    public Measurement(EmbeddedMeasurement m, String device) {
        this.setDevice(device);
        this.setAngle(m.getAngle());
        this.setRpm(m.getRpm());
        this.setStatus(m.getStatus());
        this.setTemperature(m.getTemperature());
        this.setTs(m.getTs());
    }
}