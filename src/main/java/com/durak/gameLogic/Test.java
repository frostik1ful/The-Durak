package com.durak.gameLogic;

import java.io.DataInputStream;
import java.io.IOException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) throws IOException {
        task1();
        //task2();
    }
    static void task1() throws IOException {









    }








    static void task2(){

    }
    static void factorial(){
        int n = 100;

        BigInteger answer = BigInteger.ONE;
        int a=0;

        while (n>0){
            answer = answer.multiply(BigInteger.valueOf(n));
            n--;
        }
        String s = answer.toString();

        for (int i = 0; i < s.length(); i++) {
            a+= (int) s.charAt(i);
        }
        System.out.println(a);
    }
    static void buble(){
        int array[] = new int[10];
        Random random = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(100);
        }
        System.out.println(Arrays.toString(array));

        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1){
                int a = array[i];
                int b = array[i+1];
                if (b < a){
                    int temp = a;
                    a = b;

                    b = temp;
                    array[i] = a;
                    array[i+1] = b;
                    if (i > 0){
                        i-=2;
                    }
                    else {
                        i--;
                    }

                }
                //System.out.println(Arrays.toString(array));
            }
        }
        System.out.println(Arrays.toString(array));
    }

}
