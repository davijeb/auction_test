package com.byhiras.auction;

import com.byhiras.auction.models.*;
import com.byhiras.auction.models.AuctionBid;
import com.byhiras.auction.models.Bid;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class AuctionTest {

    private User user;
    private Auction auction = new Auction();
    private BidTracker bidHistory = new BidTrackerImpl();

    Item ITEM1 = new AuctionItem("Item 1", 100, 200);
    Item ITEM2 = new AuctionItem("Item 2", 50, 75);
    Item ITEM3 = new AuctionItem("Item 3", 10, 15);
    Item ITEM4 = new AuctionItem("Item 4", 1000, 1500);
    Item ITEM5 = new AuctionItem("Item 5", 300, 400);

    AuctionService auctionService = new AuctionService(auction, bidHistory);

    @Before
    public void before() {

        auction.add(ITEM1);
        auction.add(ITEM2);
        auction.add(ITEM3);
        auction.add(ITEM4);
        auction.add(ITEM5);

        user = new AuctionUser("User 1");
    }

    @Test
    public void testBidOnItemWithValueLowerThanTheItemStartValueFails() {

        Item item = auctionService.find("Item 1");
        Bid bid = new AuctionBid(10, user);

        assertThat(bid.isAValidBid(item), equalTo(false));

    }

    @Test
    public void testBidOnItemWithTheSameValueAsTheItemStartValueFails() {

        Item item = auctionService.find("Item 1");
        Bid bid = new AuctionBid(100, user);

        assertThat(bid.isAValidBid(item), equalTo(false));

    }

    @Test
    public void testBidOnItemWithAHigherValueThanTheItemStartValueSucceeds() {

        Item item = auctionService.find("Item 1");
        Bid bid = new AuctionBid(999, user);

        assertThat(bid.isAValidBid(item), equalTo(true));

    }

    @Test
    public void testFindASingleItemHasCorrectNameAndInitialValueAndReserveValue() {

        Item item = auctionService.find("Item 1");

        assertThat(item.getName(), equalTo("Item 1"));
        assertThat(item.getInitial(), equalTo(100));
        assertThat(item.getReserve(), equalTo(200));
    }

    @Test
    public void testBiddingOnAnItemWithAValueLowerThanTheInitialItemValueFailsToRegisterABid() {

        Bid bid = new AuctionBid(10, user);
        auctionService.bid(bid, "Item 1");

        assertThat(auctionService.getBidHistory().getAllItems(user).isEmpty(), is(true));
    }

    @Test
    public void testBiddingOnAnItemWithAValueTheSameAsTheInitialItemValueFailsToRegisterABid() {

        Bid bid = new AuctionBid(100, user);
        auctionService.bid(bid, "Item 1");

        assertThat(auctionService.getBidHistory().getAllItems(user).isEmpty(), is(true));
    }

    @Test
    public void testBiddingOnAnItemWithAValueHigherTheInitialItemValueSucceedsInRegisteringABid() {

        Bid bid = new AuctionBid(101, user);
        auctionService.bid(bid, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getAllItems(user).isEmpty(), is(false));
        assertThat(auctionService.getBidHistory().getCurrentWinningBid(ITEM1), is(bid));
    }

    @Test
    public void testTwoBidsWithTheSecondBidHigherThanTheFirstSetsTheCorrectCurrentHighestBid() {

        Bid bid1 = new AuctionBid(101, user);
        Bid bid2 = new AuctionBid(102, user);
        auctionService.bid(bid1, ITEM1.getName());
        auctionService.bid(bid2, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getCurrentWinningBid(ITEM1), is(bid2));
    }

    @Test
    public void testTwoBidsWithEqualValueOnlyRegisterOnce() {

        Bid bid1 = new AuctionBid(101, user);
        Bid bid2 = new AuctionBid(101, user);
        auctionService.bid(bid1, ITEM1.getName());
        auctionService.bid(bid2, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getAllItems(user).size(), is(1));
        assertThat(auctionService.getBidHistory().getCurrentWinningBid(ITEM1), is(bid1));
    }

    @Test
    public void testRetrieveAllBidsForAnItem() {

        Bid bid1 = new AuctionBid(101, user);
        Bid bid2 = new AuctionBid(102, user);
        Bid bid3 = new AuctionBid(103, user);

        auctionService.bid(bid1, ITEM1.getName());
        auctionService.bid(bid2, ITEM1.getName());
        auctionService.bid(bid3, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getAllItems(user).size(), is(3));
    }

    @Test
    public void testRetrieveAllItemsAUserHasBidOn() {

        Bid bid1 = new AuctionBid(101, user);
        Bid bid2 = new AuctionBid(102, user);
        Bid bid3 = new AuctionBid(103, user);
        Bid bid4 = new AuctionBid(101, user); // won't register

        auctionService.bid(bid1, ITEM1.getName());
        auctionService.bid(bid2, ITEM2.getName());
        auctionService.bid(bid3, ITEM1.getName());
        auctionService.bid(bid4, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getAllBids(ITEM1).size(), is(2));
    }

    @Test
    public void testCurrentWinningBidGivenMultipleBidsOnAnItem() {

        Bid bid1 = new AuctionBid(101, user); // ok
        Bid bid2 = new AuctionBid(200, user); // ok
        Bid bid3 = new AuctionBid(50, user);  // no
        Bid bid4 = new AuctionBid(700, user); // expected
        Bid bid5 = new AuctionBid(200, user); // no
        Bid bid6 = new AuctionBid(1, user);   // no

        auctionService.bid(bid1, ITEM1.getName());
        auctionService.bid(bid2, ITEM1.getName());
        auctionService.bid(bid3, ITEM1.getName());
        auctionService.bid(bid4, ITEM1.getName());
        auctionService.bid(bid5, ITEM1.getName());
        auctionService.bid(bid6, ITEM1.getName());

        assertThat(auctionService.getBidHistory().getCurrentWinningBid(ITEM1), is(bid4));

        // there should only be three valid bids
        assertThat(auctionService.getBidHistory().getAllBids(ITEM1).size(), is(3));
    }


}
