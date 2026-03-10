package com.example.demo.models;

import java.math.BigInteger;

public class Fib {
    private int n;
    private String solution;

    public void handle() {

        try {
            if (n < 0) {
                setSolution("Negative number is not allowed");
            } else if (n == 0) {
                setSolution("The number is: 0");
            } else if (n == 1) {
                setSolution("The number is: 1");
            } else {
                BigInteger a = new BigInteger("0");
                BigInteger b = new BigInteger("1");
                BigInteger c = new BigInteger("0");

                for (int i = 2; i <= n; i++) {
                    c = a.add(b);
                    a = b;
                    b = c;
                }

                setSolution(String.format("The number is: %s", c.toString()));
            }
        } catch (Exception e) {
            setSolution("Invalid input");
        }
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
