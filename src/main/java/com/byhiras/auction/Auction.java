package com.byhiras.auction;

import com.byhiras.auction.models.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The Auction class is used to store the items being auctioned
 * and provide accessor methods to those items.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
public class Auction {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final List<Item> items = new ArrayList<>();

    /**
     * Add an item - thread safe by virtue of the
     * write lock.
     * @param item the item we wish to add
     */
    public void add(Item item) {

        lock.writeLock().lock();

        try {
            items.add(item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * A simple O(N) scan of the items will be ok as the
     * list size will be relatively small. If the size does get too
     * large then a simple cache could be employed here.
     * A lock (read in this case) is used here to guard against a
     * possible ConcurrentModificationException.
     *
     * @param name the item we wish to find
     * @return the item
     */
    public Item find(String name) {

        lock.readLock().lock();

        try {
            for(Item i: items) {
                if(i.getName().equals(name)) return i;
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }
}
