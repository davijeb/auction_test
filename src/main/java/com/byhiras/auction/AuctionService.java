package com.byhiras.auction;

import com.byhiras.auction.models.Bid;
import com.byhiras.auction.models.Item;

/**
 * The AuctionService acts as the entry point for all
 * auction related requests.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
public class AuctionService {

    private final Auction auction;
    private final BidTracker bidHistory;

    public AuctionService(Auction auction, BidTracker bidHistory) {
        this.auction = auction;
        this.bidHistory = bidHistory;
    }

    public void bid(Bid bid, String itemName) {
        if(bid == null)
            throw new RuntimeException("Unable to bid as the bid is null.");
        if(itemName == null)
            throw new RuntimeException("Unable to bid as the item name is null.");
        if(bid.getUser() == null)
            throw new RuntimeException("Unable to bid as the user is null.");

        bidHistory.registerBid(bid, auction.find(itemName));
    }

    public BidTracker getBidHistory() {
        return bidHistory;
    }

    public Item find(String itemName) {
        return auction.find(itemName);
    }
}
