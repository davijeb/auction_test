package com.byhiras.auction;

import com.byhiras.auction.models.Item;
import com.byhiras.auction.models.User;
import com.byhiras.auction.models.Bid;

import java.util.List;

public interface BidTracker {
    void registerBid(Bid bid, Item item);

    Bid getCurrentWinningBid(Item item);

    List<Bid> getAllBids(Item item);

    List<Item> getAllItems(User user);

}
