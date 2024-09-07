/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread obj = new CountThread(0,99);
        obj.run();
        CountThread obj2 = new CountThread(99,199);
        obj2.run();
        CountThread obj3 = new CountThread(200,299);
        obj3.run();
    }
}
