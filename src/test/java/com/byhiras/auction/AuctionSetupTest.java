 package com.byhiras.auction;

 import com.byhiras.auction.models.AuctionItem;
 import com.byhiras.auction.models.Item;
 import com.byhiras.auction.models.User;
 import com.byhiras.auction.models.AuctionBid;
 import com.byhiras.auction.models.Bid;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Spy;
 import org.mockito.runners.MockitoJUnitRunner;

 import java.util.Observable;

 import static org.hamcrest.MatcherAssert.assertThat;
 import static org.hamcrest.core.IsEqual.equalTo;
 import static org.mockito.Matchers.any;
 import static org.mockito.Matchers.anyObject;
 import static org.mockito.Mockito.*;

 @RunWith(MockitoJUnitRunner.class)
public class AuctionSetupTest {

    @Mock
    User user;

    @Mock
    Bid bid;

    @Mock
    BidTracker bidHistory;

    @Mock
    Auction auction;

    private final Item item = new AuctionItem("Item 1", 100, 200);

    @Spy
    @InjectMocks
    AuctionService auctionService = new AuctionService(auction, bidHistory);

    @Test
    public void testCreateAuctionItemAndNamesMatch() {

        final String name = "Item 1";
        assertThat(item.getName(), equalTo(name));

    }

    @Test(expected = RuntimeException.class)
    public void testCreatingABidWithANegativeValueThrowsARuntimeException()  {

        new AuctionBid(-100, user);

    }

    @Test(expected = RuntimeException.class)
    public void testBidWithNullBidIsNotRegisteredAndThrowsARuntimeException() {

        auctionService.bid(null, "Item 1");
        verify(bidHistory, times(0)).registerBid(any(Bid.class), any(Item.class));

    }

    @Test(expected = RuntimeException.class)
    public void testBidWithNullUserIsNotRegisteredAndThrowsARuntimeException() {

        auctionService.bid(bid, "Item 1");
        verify(bidHistory, times(0)).registerBid(any(Bid.class), any(Item.class));

    }

    @Test(expected = RuntimeException.class)
    public void testBidWithNullItemNameIsNotRegisteredAndThrowsARuntimeException() {

        auctionService.bid(bid, null);
        verify(bidHistory, times(0)).registerBid(any(Bid.class), any(Item.class));

    }

    @Test
    public void testASingleUserBidOnAnItemIsRegistered() {

        when(bid.getUser()).thenReturn(user);
        auctionService.bid(bid, "Item 1");
        verify(bidHistory, times(1)).registerBid(any(Bid.class), any(Item.class));

    }

    @Test
    public void testExecutingABidCausesTheUserToRegisterTheEvent() {

        Bid bid = spy(new AuctionBid(100, user));
        bid.execute(item);

        verify(user, times(1)).update(any(Observable.class),anyObject());

    }

     @Test
     public void testExecutingThreeBidsCausesTheUserToRegisterTheEventThreeTimes() {

         spy(new AuctionBid(100, user)).execute(item);
         spy(new AuctionBid(100, user)).execute(item);
         spy(new AuctionBid(100, user)).execute(item);

         verify(user, times(3)).update(any(Observable.class),anyObject());

     }


}
