package com.byhiras.auction.models;

import com.byhiras.auction.util.Immutable;
import com.sun.istack.internal.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;

/**
 * The AuctionUser represents the individual bidding in the
 * auction.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
@Immutable
public class AuctionUser implements User {

    private final String name;

    public AuctionUser(String name) {
        this.name = name;
    }

    /**
     * If we receive a notification from the Bid that is has executed
     * then we register the details here. This would then store the information in
     * some form of database (but that's outside the scope here).
     *
     * @param bid the observable
     * @param item the item the user is bidding on
     */
    @Override
    public void update(Observable bid, Object item) {
        Logger.getLogger(getClass()).log(Level.INFO,
                name +
                " executed a " +
                 bid +
                " on " +
                item +
                " at " +
                new SimpleDateFormat("MM/dd/yyyy h:mm:ss:S a").format(new Date()));
    }
}
