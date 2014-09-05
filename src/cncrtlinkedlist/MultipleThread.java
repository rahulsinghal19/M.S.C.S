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

    MultipleThread(String name, CncrtLinkedList list) {
        threadName = name;
        ccList = list;
    }
       
    @Override
    public void run() {
        while (true) {
            randomNumber = rand.nextInt(100);
            try {
                switch (threadName) {
                    case "Insert":
                        added = ccList.Add(randomNumber);
//                        if(added) {
//                            System.out.println("Insert : Key " + randomNumber + " added");
//                        }
//                        else {
//                            System.out.println("Insert : Key " + randomNumber + " already exists");
//                        }
                        break;
                    case "Remove":
                        removed = ccList.Remove(randomNumber);
//                        if(removed) {
//                            System.out.println("Remove : Key " + randomNumber + " removed");
//                        }
//                        else {
//                            System.out.println("Remove : Key " + randomNumber + " already exists");
//                        }
                        break;
                    case "Search":
                        searched = ccList.Search(randomNumber);
//                        if(searched) {
//                            System.out.println("Search : Key " + randomNumber + " found");
//                        }
//                        else {
//                            System.out.println("Search : Key " + randomNumber + " not found");
//                        }
                        break;
                }
            } catch(Exception ex) {
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