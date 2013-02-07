package com.byhiras.auction.models;

/**
 * The AuctionItem holds information on the item being sold such as
 * name, the starting offer price and the reserve price.
 *
 * @author: Jeremy Davies [jerdavies@gmail.com]
 */
public class AuctionItem implements Item {

    private final String name;
    private final int initial;
    private final int reserve;

    public AuctionItem(final String name, final int initial, final int reserve) {
        this.name = name;
        this.initial = initial;
        this.reserve = reserve;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getInitial() {
        return initial;
    }

    @Override
    public int getReserve() {
        return reserve;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuctionItem that = (AuctionItem) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name + " (initial: " + initial + ", reserve: " + reserve + ")";
    }
}
