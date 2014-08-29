/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fgsccrtlinkedlist;

import java.util.Random;

/**
 *
 * @author Rahul
 */
public class FineGrainedSyncThread extends Thread {
    private Thread t;
    private final String threadName;
    FgsCcrtLinkedList fgsList;
    private final Random rand = new Random();
    private int randomNumber;
    private boolean added, removed, searched;

    FineGrainedSyncThread(String name, FgsCcrtLinkedList list) {
        threadName = name;
        fgsList = list;
    }
    
    @Override
    public void run() {
        while(true) {
            randomNumber = rand.nextInt(10);
            try {
                switch(threadName) {
                    case "Insert" :
                        added = fgsList.Add(randomNumber);
//                        if(added) {
//                            System.out.println("Insert : Key " + randomNumber + " added");
//                        }
//                        else {
//                            System.out.println("Insert : Key " + randomNumber + " already exists");
//                        }
                        break;
                    case "Remove" :
                        removed = fgsList.Remove(randomNumber);
//                        if(removed) {
//                            System.out.println("Remove : Key " + randomNumber + " removed");
//                        }
//                        else {
//                            System.out.println("Remove : Key " + randomNumber + " does not exists");
//                        }
                        break;
                    case "Search" :
                        searched = fgsList.Search(randomNumber);
//                        if(searched) {
//                            System.out.println("Search : Key " + randomNumber + " found");
//                        }
//                        else {
//                            System.out.println("Search : Key " + randomNumber + " not found");
//                        }
                        break;
                }
            } catch (Exception ex) {
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
