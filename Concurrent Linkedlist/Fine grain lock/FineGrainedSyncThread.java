/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package fgsccrtlinkedlist;

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
        while(FgsCcrtLinkedList.performed <= FgsCcrtLinkedList.operations) {
            randomNumber = rand.nextInt(FgsCcrtLinkedList.keySize);
            switch(threadName) {
                case "Insert" :
                    added = fgsList.Add(randomNumber);
                    break;
                case "Remove" :
                    removed = fgsList.Remove(randomNumber);
                    break;
                case "Search" :
                    searched = fgsList.Search(randomNumber);
                    break;
            }
            if(FgsCcrtLinkedList.performed > FgsCcrtLinkedList.operations) {
                interrupt();
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
