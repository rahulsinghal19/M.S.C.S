/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package fgsccrtlinkedlist;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Rahul
 */
public class Node {
    int key;
    Node next;
    Lock lock;
    
    Node(int keyValue) {
        key = keyValue;
        lock = new ReentrantLock();
        next = null;
    }
}
