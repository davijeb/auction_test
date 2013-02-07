package com.byhiras.auction.models;

import com.byhiras.auction.util.Immutable;

import java.util.Observable;

/**
 * The Bid class stores information regarding the bid
 * value a user is making on an auction item. It uses an
 * Observer pattern in order to notify the user when
 * a bid has been created.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
@Immutable
public class AuctionBid extends Observable implements Bid {

    private final int bidAmount;
    private final User user;

    /**
     * Create an AuctionBid object for a given value and user.
     * @param bidAmount how much is the bid worth
     * @param user the person bidding
     */
    public AuctionBid(final int bidAmount, final User user) {
        if(bidAmount < 0) throw new RuntimeException("Unable to create a bid with a negative amount: " +bidAmount);

        addObserver(user); // we want to record a bid on an item with the user

        this.user = user;
        this.bidAmount = bidAmount;
    }

    @Override
    public int value() {
        return bidAmount;
    }

    @Override
    public User getUser() {
        return user;
    }

    /**
     * A bid is only valid if its value is
     * greater than the items starting value.
     * @param item the item we want to check
     * @return true iff bid value is greater than item initial value
     */
    @Override
    public boolean isAValidBid(final Item item) {
        return bidAmount > item.getInitial();
    }

    /**
     * Inform the observers when we execute a bid on
     * an item.
     */
    @Override
    public void execute(final Item item) {
        setChanged();
        notifyObservers(item);
    }

    @Override
    public int compareTo(final Bid that) {
        return that.value() - this.value();
    }

    @Override
    public String toString() {
        return "Bid (" + bidAmount + ")";
    }
}
