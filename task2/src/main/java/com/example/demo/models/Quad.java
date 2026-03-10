package com.example.demo.models;

public class Quad {
    private Double a;
    private Double b;
    private Double c;
    private String solution;

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void handle() {
        if (a.equals(0.0)) {
            setSolution("a can not be zero");
            return;
        }

        Double delta = (b * b) - (4 * a * c);

        if (delta > 0) {
            Double x1 = (-b - Math.sqrt(delta)) / (2 * a);
            Double x2 = (-b + Math.sqrt(delta)) / (2 * a);
            setSolution(String.format("x1: %f, x2: %f", x1, x2));
        } else if (delta < 0) {
            setSolution("No real roots");
        } else {
            Double x = (-b) / (2 * a);
            setSolution(String.format("x: %f", x));
        }
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }
}
