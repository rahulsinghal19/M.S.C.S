/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cncrtlinkedlist;

import java.util.Random;

/**
 *
 * @author Rahul
 */
public class MultipleThread extends Thread {
    private Thread t;
    private final String threadName;
    private int randomNumber;
    private final Random rand = new Random();
    private final CncrtLinkedList ccList;
    private boolean added, removed, searched;
    private final int runningTime = 40000;
    private int wait = 0;
//    private final int operations;
//    private static int performed = 0;

    MultipleThread(String name, CncrtLinkedList list) {
        threadName = name;
        ccList = list;
//        operations = list.limit;
    }
       
    @Override
    public void run() {
        while (wait < runningTime) {
//        while (ccList.performed != ccList.limit) {
            randomNumber = rand.nextInt(10);
            try {
                switch (threadName) {
                    case "Insert":
                        added = ccList.Add(randomNumber);
                        break;
                    case "Remove":
                        removed = ccList.Remove(randomNumber);
                        break;
                    case "Search":
                        searched = ccList.Search(randomNumber);
                        break;
                }
//                ccList.performed++;
                Thread.sleep(100);
                wait += 100;
            } catch(InterruptedException ex) {
                System.out.println("Run : Exception in " + threadName + " thread : " + ex);
            }
        }
    }
    
    @Override
    public void start() {
        if(t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}