package com.riskworx.limitorderbook;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.riskworx.limitorderbook.LimitOrderBook.Order;

public class LimitOrderBookTest {
    LimitOrderBook lob = new LimitOrderBook();

    //Storing the key in a map as an array to get the position expected
    private Long[] arrayKey(Integer arraySize, Map<Long,Order> orderBook){
        Long [] array = new Long[arraySize];
        Integer count = 0;
        for(Long bidList : orderBook.keySet()){
            array[count] = bidList;
            count++;
        }
        return array;
    }
    @Test
    public void testaddOrderHighestBidPrice()
    {
        //getting existing orders
        lob.existingOrderBook();
        lob.addOrder(100, 23, true);
        Long[] array = arrayKey(4, lob.getBidBookMap());
        Integer expected = 23;
        //highest price so it must be on the top of orderbook
        Integer actual = lob.getBidBookMap().get(array[0])._price;
        assertEquals(expected, actual);
    }

    @Test
    public void testaddOrderLowestBidPrice()
    {
        //getting existing orders
        lob.existingOrderBook();
        //adding new order
        lob.addOrder(100, 17, true);
        Long[] array = arrayKey(4, lob.getBidBookMap());
        Integer expected = 17;
        //lowest price so it must be on the bottom of orderbook
        Integer actual = lob.getBidBookMap().get(array[3])._price;
        assertEquals(expected, actual);
    }

    @Test
    public void testaddOrderBidSamePrice()
    {
        //getting existing orders
        lob.existingOrderBook();
        //adding new order
        lob.addOrder(20, 20, true);
        Long[] array = arrayKey(4, lob.getBidBookMap());
        Integer expected = 20;
        //testing on quantity because prices are the same to make sure order on the bottom
        Integer actual = lob.getBidBookMap().get(array[3])._quantity;
        assertEquals(expected, actual);
    }

    @Test
    public void testaddOrderHighestAskPrice()
    {
        lob.existingOrderBook();
        //adding new order
        lob.addOrder(100, 100, false);
        Long[] array = arrayKey(4, lob.getAskBookMap());
        Integer expected = 100;
        //highest price so it must be on the bottom of orderbook
        Integer actual = lob.getAskBookMap().get(array[3])._price;
        assertEquals(expected, actual);
    }

    @Test
    public void testaddOrderLowestAskPrice()
    {
        //getting existing orders
        lob.existingOrderBook();
        //adding new order
        lob.addOrder(100, 8, false);
        Long[] array = arrayKey(4, lob.getAskBookMap());
        Integer expected = 8;
        //lowest price so it must be on the top of orderbook
        Integer actual = lob.getAskBookMap().get(array[0])._price;

        assertEquals(expected, actual);
    }

    @Test
    public void testaddOrderAskSamePrice()
    {
        //getting existing orders
        lob.existingOrderBook();
        //adding new order
        lob.addOrder(20, 25, false);
        Long[] array = arrayKey(4, lob.getAskBookMap());
        Integer expected = 20;
        //testing on quantity because prices are the same to make sure order on the 2nd to last
        Integer actual = lob.getAskBookMap().get(array[2])._quantity;
        assertEquals(expected, actual);
    } 

    @Test
    public void testDeleteOrder()
    {
        //getting existing orders
        lob.existingOrderBook();
        Long[] array = arrayKey(3, lob.getAskBookMap());
        //adding new order
        lob.deleteOrder(array[0], false);
        //testing the order has been removed and the right one
        Integer expected = 2;
        Integer actual = lob.getAskBookMap().size();
        Integer expected1 = 27;
        Integer actual1 = lob.getAskBookMap().get(array[2])._price;
        Integer expected2 = 1000;
        Integer actual2 = lob.getAskBookMap().get(array[1])._quantity;
        assertEquals(expected, actual);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }
    @Test
    public void ModifyOrder()
    {
        //getting existing orders
        lob.existingOrderBook();
        //adding new order
        Long[] array = arrayKey(3, lob.getAskBookMap());
        lob.modifyOrder(array[0], 10, false);
        //testing the order has been removed and the right one
        Integer expected = 10;
        Integer actual = lob.getAskBookMap().get(array[0])._quantity;
        assertEquals(expected, actual);
    }
}

