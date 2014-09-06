/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cncrtlinkedlist;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rahul
 */

public class CncrtLinkedList {
    
    private final Node head;
    private final Node tail;
    private final Lock lock = new ReentrantLock();
    private int added, removed, length;

    public CncrtLinkedList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        added = 0;
        removed = 0;
        length = 0;
    }
    
    public boolean Add(int key) {
        Node pred, curr;
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if(curr.key == key) {
                System.out.println("I Key : " + key + " already exists");
                return false;
            }
            else {
                Node tmp = new Node(key);
                tmp.next = curr;
                pred.next = tmp;
                System.out.println("I Key : " + key + " inserted");
                added++;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public boolean Remove(int key) {
        Node curr, pred;
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if(curr.key == key) {
                pred.next = curr.next;
                System.out.println("R Key : " + key + " removed");
                removed++;
                return true;
            } else {
                System.out.println("R Key : " + key + " does not exist");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public boolean Search(int key) {
        Node curr = head;
        try {
            while(curr.key < key) {
                curr = curr.next;
            }
            if(curr.key == key) {
                System.out.println("S Key : " + key + " found");
                return true;
            }
            else {
                System.out.println("S Key : " + key + " not found");
                return false;
            }
//            return curr.key == key;
        } catch (Exception ex) {
            System.out.println("Error while searching item : " + ex );
            return false;
        }
    }
    
    public void Print(CncrtLinkedList list) {
        
        Node curr = list.head;
        System.out.println("Printing entire LinkedList... ");
        while(curr.next != list.tail) {
            System.out.println(curr.key + " -> ");
            curr = curr.next;
            length++;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CncrtLinkedList list = new CncrtLinkedList();
        for(int i = 0; i< 5; i++) {
            MultipleThread insert = new MultipleThread("Insert", list);
            insert.start();
        }
        
        for(int i = 0; i< 3; i++) {
            MultipleThread remove = new MultipleThread("Remove", list);
            remove.start();
        }
        
        for(int i = 0; i< 1; i++) {
            MultipleThread search = new MultipleThread("Search", list);
            search.start();
        }
        while(true) {
            if(Thread.activeCount() == 1) {
                break;
            }
            else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CncrtLinkedList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        list.Print(list);
        System.out.println("Added : " + list.added);
        System.out.println("Removed : " + list.removed);
        System.out.println("Remaining : " + (list.added - list.removed));
        System.out.println("Length of the Linked List : " + list.length);
//        System.out.println("Done.. Main thread");
    }
}
