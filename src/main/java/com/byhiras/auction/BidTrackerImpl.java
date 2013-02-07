package com.byhiras.auction;

import com.byhiras.auction.models.Item;
import com.byhiras.auction.models.User;
import com.byhiras.auction.models.Bid;
import com.byhiras.auction.util.ThreadSafe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The BidTrackerImpl holds onto all the bid information
 * for users and items.
 *
 * It is possible there could be multiple bids on the same item
 * so to prevent a possible race condition a ReadWriteLock is used
 * to ensure consistent access to the shared mutable state Maps.
 *
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
@ThreadSafe
public class BidTrackerImpl implements BidTracker {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // maps to track bid histories
    private final Map<Item, Queue<Bid>> itemHistory = new ConcurrentHashMap<>();
    private final Map<User, Queue<Item>>  userHistory = new ConcurrentHashMap<>();

    /**
     * Attempt to register a bid on an item. If the bid
     * is valid and is greater than the previous bid then
     * this is added to the storage maps.
     *
     * @param bid the bid we are registering
     * @param item the item we are bidding on
     */
    @Override
    public void registerBid(final Bid bid, final Item item) {

        lock.writeLock().lock();

        try
        {
            // check the maps have the correct keys
            checkMaps(bid, item);

            // check to see if the bid value is sufficient
            if(!bid.isAValidBid(item)) return;

            if(itemHistory.get(item).size() == 0 || itemHistory.get(item).peek().value() < bid.value()) {
                itemHistory.get(item).add(bid);
                userHistory.get(bid.getUser()).add(item);

                bid.execute(item); // notify the user to register a bid on this item
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Ensure the maps have the keys in-place.
     * @param bid the current bid
     * @param item the item the bid is against
     */
    private void checkMaps(final Bid bid, final Item item) {
        if (itemHistory.get(item) == null) itemHistory.put(item, new PriorityQueue<Bid>());
        if (userHistory.get(bid.getUser()) == null) userHistory.put(bid.getUser(), new LinkedList<Item>());
    }

    /**
     * Get the current winning bid for an item.
     *
     * @param item
     * @return the winning bid
     */
    @Override
    public Bid getCurrentWinningBid(final Item item) {

        lock.readLock().lock();

        try {
            return itemHistory.get(item).peek();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get all the bids for an item.
     *
     * @param item
     * @return all the bids
     */
    @Override
    public List<Bid> getAllBids(final Item item) {

        lock.readLock().lock();


        try {
            return new LinkedList<Bid>(itemHistory.get(item));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get all the items on which a user has bid.
     *
     * @param user
     * @return
     */
    @Override
    public List<Item> getAllItems(final User user) {

        lock.readLock().lock();

        try {
            return new LinkedList(userHistory.get(user));
        } finally {
            lock.readLock().unlock();
        }
    }
}
