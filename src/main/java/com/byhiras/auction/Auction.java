package com.byhiras.auction;

import com.byhiras.auction.models.Item;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Auction class is used to store the items being auctioned
 * and provide accessor methods to those items.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
public class Auction {

    private final List<Item> items = new CopyOnWriteArrayList<>();

    /**
     * Add an item - note this is not thread safe
     * as the addition of items is a serial process.
     *
     * @param item the item we wish to add
     */
    public void add(Item item) {
        items.add(item);
    }

    /**
     * A simple O(N) scan of the items will be ok as the
     * list size will be relatively small. If the size does get too
     * large then a simple cache could be employed here.
     *
     * @param name the item we wish to find
     * @return the item
     */
    public Item find(String name) {
        for(Item i: items) {
            if(i.getName().equals(name)) return i;
        }
        return null;
    }
}
