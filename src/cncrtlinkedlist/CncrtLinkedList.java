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

    public CncrtLinkedList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
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
                return true;
            }
        } catch(Exception ex) {
            System.out.println("Error occured while inserting item : " + ex);
            return false;
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
                return true;
            } else {
                System.out.println("R Key : " + key + " does not exist");
                return false;
            }
        } catch (Exception ex) {
            System.out.println("Error in removing item : " + ex);
            return false;
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
        
        for(int i = 0; i< 30; i++) {
            MultipleThread remove = new MultipleThread("Remove", list);
            remove.start();
        }
        
        for(int i = 0; i< 10; i++) {
            MultipleThread search = new MultipleThread("Search", list);
            search.start();
        }
    }
    
}
