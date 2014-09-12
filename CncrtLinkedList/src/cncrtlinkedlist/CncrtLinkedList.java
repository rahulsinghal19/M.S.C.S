/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cncrtlinkedlist;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Rahul
 */

public class CncrtLinkedList {
    
    private final Node head;
    private final Node tail;
    private final Lock lock = new ReentrantLock();
    private int added, removed, length, operations;
    public final int limit = 100;
    public int performed = 0;

    public CncrtLinkedList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        added = 0;
        removed = 0;
        length = 0;
        operations = 0;
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
//                System.out.println("I Key : " + key + " already exists");
                return false;
            }
            else {
                Node tmp = new Node(key);
                tmp.next = curr;
                pred.next = tmp;
//                System.out.println("I Key : " + key + " inserted");
                added++;
                return true;
            }
        } finally {
            operations++;
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
//                System.out.println("R Key : " + key + " removed");
                removed++;
                return true;
            } else {
//                System.out.println("R Key : " + key + " does not exist");
                return false;
            }
        } finally {
            operations++;
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
//                System.out.println("S Key : " + key + " found");
                return true;
            }
            else {
//                System.out.println("S Key : " + key + " not found");
                return false;
            }
//            return curr.key == key;
        } catch (Exception ex) {
            System.out.println("Error while searching item : " + ex );
            return false;
        } finally {
            operations++;
        }
    }
    
    public void Print(CncrtLinkedList list) {
        
        Node curr = list.head;
        boolean repeated = false;
        System.out.println("Printing entire LinkedList... ");
        while(curr.next != list.tail) {
            if(curr.next.key == curr.next.next.key) {
                repeated = true;
                break;
            }
            System.out.print(curr.key + " -> ");
            curr = curr.next;
            length++;
        }
        if(repeated) {
            System.out.println("Inconsistant linked list, has repeated items!");
        }
        System.out.println("");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int totalThreads = Integer.parseInt(args[0]);
        int searchPercent = Integer.parseInt(args[1]);
        int insertPercent = Integer.parseInt(args[2]);
        int deletePercent = Integer.parseInt(args[3]);
        int searchThreads = ((searchPercent * totalThreads) / 100);
        int insertThreads = ((insertPercent * totalThreads) / 100);
        int deleteThreads = ((deletePercent * totalThreads) / 100);
        
        long start, end;
        
        CncrtLinkedList list = new CncrtLinkedList();
        start = System.currentTimeMillis();
        for(int i = 0; i< searchThreads; i++) {
            MultipleThread search = new MultipleThread("Search", list);
            search.start();
        }
        
        for(int i = 0; i< insertThreads; i++) {
            MultipleThread insert = new MultipleThread("Insert", list);
            insert.start();
        }
        
        for(int i = 0; i< deleteThreads; i++) {
            MultipleThread remove = new MultipleThread("Remove", list);
            remove.start();
        }

        while(true) {
            if(Thread.activeCount() == 1) {
                break;
            }
            else {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(CncrtLinkedList.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
        end = System.currentTimeMillis();
        list.Print(list);
        System.out.println("Added : " + list.added);
        System.out.println("Removed : " + list.removed);
        System.out.println("Estimated Remaining : " + (list.added - list.removed));
        System.out.println("Length of the Linked List : " + list.length);
        System.out.println("Number of operations performed : " + list.operations);
        System.out.println("Running time : " + ((end - start)/1000) + " seconds");
//        System.out.println("Done.. Main thread");
    }
}
