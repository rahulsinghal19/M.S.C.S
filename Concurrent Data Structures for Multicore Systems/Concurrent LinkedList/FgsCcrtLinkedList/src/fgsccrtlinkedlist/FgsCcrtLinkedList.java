/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fgsccrtlinkedlist;

/**
 *
 * @author Rahul
 */
public class FgsCcrtLinkedList {
    private final Node head;
    private final Node tail;

    public FgsCcrtLinkedList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
    }
    
    public boolean Add(int key) {
//        head.lock.lock(); //Lock released?
        Node pred = head;
        pred.lock.lock();
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while(curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
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
            } finally {
                curr.lock.unlock();
            }
        } catch (Exception ex) {
            System.out.println("Error occured while inserting item : " + ex);
            return false;
        } finally {
            pred.lock.unlock();
        }
    }
    
    public boolean Remove(int key) {
        Node pred = head;
        pred.lock.lock();
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while(curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if(curr.key == key) {
                    pred.next = curr.next;
                    System.out.println("R Key : " + key + " removed");
                    return true;
                }
                else {
                    System.out.println("R Key : " + key + " does not exist");
                    return false;
                }
            } finally {
                curr.lock.unlock();
            }
        } catch (Exception ex) {
            System.out.println("Error in removing item : " + ex);
            return false;
        } finally {
        pred.lock.unlock();
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
        FgsCcrtLinkedList list = new FgsCcrtLinkedList();
        for(int i = 0; i< 5; i++) {
            FineGrainedSyncThread insert = new FineGrainedSyncThread("Insert", list);
            insert.start();
        }
        
        for(int i = 0; i< 30; i++) {
            FineGrainedSyncThread remove = new FineGrainedSyncThread("Remove", list);
            remove.start();
        }
        
        for(int i = 0; i< 10; i++) {
            FineGrainedSyncThread search = new FineGrainedSyncThread("Search", list);
            search.start();
        }
    }
    
}
