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

    // Maps to track bid histories.
    private final Map<Item, Queue<Bid>> itemHistory   = new ConcurrentHashMap<>(100, 0.75f);
    private final Map<User, Queue<Item>>  userHistory = new ConcurrentHashMap<>(100, 0.75f);

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

        // check the maps have the correct keys
        checkMaps(bid, item);

        // check to see if the bid value is sufficient
        if(!bid.isAValidBid(item)) return;

        // check item history is not empty and that the last bid value is lower than the current
        if(itemHistory.get(item).size() == 0 || itemHistory.get(item).peek().value() < bid.value()) {
            itemHistory.get(item).add(bid);
            userHistory.get(bid.getUser()).add(item);

            bid.execute(item); // notify the user to register a bid on this item
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

        return itemHistory.get(item).peek();
    }

    /**
     * Get all the bids for an item.
     *
     * @param item
     * @return all the bids
     */
    @Override
    public List<Bid> getAllBids(final Item item) {

        return new LinkedList(itemHistory.get(item));
    }

    /**
     * Get all the items on which a user has bid.
     *
     * @param user
     * @return a list of items
     */
    @Override
    public List<Item> getAllItems(final User user) {

        return new LinkedList(userHistory.get(user));
    }
}
