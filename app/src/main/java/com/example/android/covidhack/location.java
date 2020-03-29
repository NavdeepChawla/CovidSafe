package com.example.android.covidhack;

public class location {
    private double lattitude;
    private double longitude;
    private float accuracy;

    public location(double lattitude,double longitude,float accuracy){
        this.lattitude=lattitude;
        this.longitude=longitude;
        this.accuracy=accuracy;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
