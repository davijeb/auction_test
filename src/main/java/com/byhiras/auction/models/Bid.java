package com.byhiras.auction.models;

public interface Bid extends Comparable<Bid> {
    int value();
    User getUser();
    boolean isAValidBid(Item item);
    void execute(Item item);
}
