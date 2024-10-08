package edu.eci.arsw.highlandersim;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final CopyOnWriteArrayList<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    private static final Object lock = new Object();
    private static boolean paused = false;
    private static boolean isAlive = true;



    public Immortal(String name, CopyOnWriteArrayList<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }



    public void run() {
        while (this.getHealth() > 0 && isAlive) {
            synchronized (lock) {
                while (paused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (immortalsPopulation.size() > 1) {
                Immortal im;
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = (nextFighterIndex + 1) % immortalsPopulation.size();
                }

                im = immortalsPopulation.get(nextFighterIndex);
                this.fight(im);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fight(Immortal i2) {
        Immortal firstLock = this;
        Immortal secondLock = i2;


        if (System.identityHashCode(i2) < System.identityHashCode(this)) {
            firstLock = i2;
            secondLock = this;
        }
        synchronized (firstLock){
            synchronized (secondLock){
                if (i2.getHealth() > 0 && this.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                    if(i2.getHealth() <= 0){
                        immortalsPopulation.remove(i2);
                    }
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public static void pauseImmortals() {
        paused = true;
    }

    public static void resumeImmortals() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }

    public static void setDead(){ //cambiar nombre de metodo y atributo isAlive
        isAlive = false;
    }
    public static void setAlive(){//cambiar nombre de metodo y atributo isAlive
        isAlive = true;
    }



}
