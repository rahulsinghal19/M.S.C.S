/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package fgsccrtlinkedlist;

/**
 *
 * @author Rahul
 */
public class FgsCcrtLinkedList {
    private final Node head;
    private final Node tail;
    private int added, removed, length;
    
    public static int keySize = 100;
    public static int operations = 100000;
    public static int performed = 0;

    public FgsCcrtLinkedList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        added = 0;
        removed = 0;
        length = 0;
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
                    added++;
                    return true;
                }
            } finally {
                curr.lock.unlock();
            }
        } finally {
            FgsCcrtLinkedList.performed++;
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
                    removed++;
                    return true;
                }
                else {
                    System.out.println("R Key : " + key + " does not exist");
                    return false;
                }
            } finally {
                curr.lock.unlock();
            }
        } finally {
            FgsCcrtLinkedList.performed++;
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
        } finally {
            FgsCcrtLinkedList.performed++;
        }
    }
    
    public void Print(FgsCcrtLinkedList list) {
        
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
            int searchThreads = ((searchPercent * totalThreads) / 100);
            int insertThreads = ((insertPercent * totalThreads) / 100);
            int deleteThreads = ((deletePercent * totalThreads) / 100);

            FgsCcrtLinkedList.operations = Integer.parseInt(args[4]);
            FgsCcrtLinkedList.keySize = Integer.parseInt(args[5]);

            long start, end;
        
            FgsCcrtLinkedList list = new FgsCcrtLinkedList();
            start = System.currentTimeMillis();

            for(int i = 0; i< searchThreads; i++) {
                FineGrainedSyncThread search = new FineGrainedSyncThread("Search", list);
                search.start();
            }

            for(int i = 0; i< insertThreads; i++) {
                FineGrainedSyncThread insert = new FineGrainedSyncThread("Insert", list);
                insert.start();
            }

            for(int i = 0; i< deleteThreads; i++) {
                FineGrainedSyncThread remove = new FineGrainedSyncThread("Remove", list);
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
            System.out.println("Number of operations performed : " + FgsCcrtLinkedList.performed);
            System.out.println("Running time : " + ((end - start)/1000.0) + " seconds");
        }
    }
}
