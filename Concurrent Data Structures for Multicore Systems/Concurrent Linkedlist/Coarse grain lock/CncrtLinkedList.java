/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package cncrtlinkedlist;

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
    private int added, removed, length;
    
    public static int keySize = 100;
    public static int operations = 100000;
    public static int performed = 0;

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
            CncrtLinkedList.performed++;
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
            CncrtLinkedList.performed++;
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
        } catch (Exception ex) {
            System.out.println("Error while searching item : " + ex );
            return false;
        } finally {
            CncrtLinkedList.performed++;
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
        if(args.length != 6) {
            System.out.println("Usage: java <Class> total_threads search_thread_percentage insert_thread_percentage"
                    + "remove_thread_percentage number_of_operations key_space");
        } else {
            int totalThreads = Integer.parseInt(args[0]);
            int searchPercent = Integer.parseInt(args[1]);
            int insertPercent = Integer.parseInt(args[2]);
            int deletePercent = Integer.parseInt(args[3]);
            int searchThreads = (int) Math.ceil((searchPercent * totalThreads) / 100.0);
            int insertThreads = (int) Math.ceil((insertPercent * totalThreads) / 100.0);
            int deleteThreads = (int) Math.ceil((deletePercent * totalThreads) / 100.0);

            CncrtLinkedList.operations = Integer.parseInt(args[4]);
            CncrtLinkedList.keySize = Integer.parseInt(args[5]);

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

            System.out.println("Waiting for threads to complete the job..");

            while(true) {
                if(Thread.activeCount() == 1) {
                    break;
                }
            }
            
            end = System.currentTimeMillis();
            list.Print(list);
            
            System.out.println("Added : " + list.added);
            System.out.println("Removed : " + list.removed);
            System.out.println("Estimated Remaining : " + (list.added - list.removed));
            System.out.println("Length of the Linked List : " + list.length);
            System.out.println("Number of operations performed : " + CncrtLinkedList.performed);
            System.out.println("Running time : " + ((end - start)/1000.0) + " seconds");
        }
    }
}
