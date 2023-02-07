//Reuse the existing classes in packages
//It will control the naming conflicts
package com.riskworx.limitorderbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class LimitOrderBook{
  
  //LinkedHashMap this will make delete, modify, sorting much easier and efficient through the object Order
  //Long is the "Key" which is represented as the ID which is linked to the Order details "Value".
  private final Map<Long, Order> _bidBookMap = new LinkedHashMap<>();
  private final Map<Long, Order> _askBookMap = new LinkedHashMap<>();

  //global variable for when the order book is needed
  //for efficiency only once all oreder methods are done then we sort the book
  public Map<Long, Order> getBidBookMap() {
    sortBidOrderBook();
    return _bidBookMap;
  }

  public Map<Long, Order> getAskBookMap() {
    sortAskOrderBook();
    return _askBookMap;
  }

  //used atomicLong as it contains methods such as getAndIncrement()
  //which maintains the sequence of order(FIFO) and allows for a unique trade number
  private final AtomicLong nextId = new AtomicLong();
  private final AtomicLong nextSeqId = new AtomicLong();
  
    //populate an existing order book that can be called if needed or book can be developed from scratch
    public void existingOrderBook()
    {
      addOrder(1000,22,true);
      addOrder(100,20,true);
      addOrder(100,20,true);
  
      //creating Ask order book
      addOrder(100,25,false);
      addOrder(1000,25,false);
      addOrder(1000,27,false);
    }
    
    //sorting a LinkedHashMap would be much easier, with a Comparator
    private void sortOrderBook(Map<Long, Order> orderBookMap, Comparator<Map.Entry<Long, Order>> comparator) {
      //calling the order book and storing it in the variable name "entries"
      List<Map.Entry<Long, Order>> entries = new ArrayList<>(orderBookMap.entrySet());
      Collections.sort(entries, comparator);
      //need to clear unorganised orders
      orderBookMap.clear();
      //repopulate the with ordganised orders
      for(Map.Entry<Long, Order> e : entries) {
        orderBookMap.put(e.getKey(), e.getValue());
      }
    }
  //function contructs the comparator method to be used in the Collections.sort(entries, comparator);
  //comparator is a Interface class that contains the methods compare() and equals()
    public void sortBidOrderBook() {
      Comparator<Map.Entry<Long, Order>> comparator = new Comparator<Map.Entry<Long, Order>>() {
        //overring the compare method to cater for the bid book
        @Override
        public int compare(Map.Entry<Long, Order> a, Map.Entry<Long, Order> b){
          Order o1 = a.getValue();
          Order o2 = b.getValue();
          //sort in descending order of price first
          if (o1._price > o2._price) {
              return -1;
          }
          else if (o1._price == o2._price) {
            // then FIFO, based on the seqId which tracks the time of the orders
            if (o1._seqId < o2._seqId) { 
              return -1;
            }
          }
          return 1;
        }
      };
      sortOrderBook(_bidBookMap, comparator);
    }
    //NB:
    public void sortAskOrderBook() {
      Comparator<Map.Entry<Long, Order>> comparator = new Comparator<Map.Entry<Long, Order>>() {
        @Override
        public int compare(Map.Entry<Long, Order> a, Map.Entry<Long, Order> b){
          Order o1 = a.getValue();
          Order o2 = b.getValue();
          //sort in ascending order of price first
          if (o1._price < o2._price) {
              return -1;
          }
          else if (o1._price == o2._price) {
            // then FIFO, based on the seqId which tracks the time of the orders
            if (o1._seqId < o2._seqId) { 
              return -1;
            }
          }
          return 1;
        }
      };
      sortOrderBook(_askBookMap, comparator);
    }
    
    //Add order method needs quantity 
    //and price and if it is a bid or ask order from the user
    public void addOrder(int qty, int price, boolean isBid) {
      //Price and Quantity check
      integerCheck(qty, "Quantity");
      integerCheck(price, "Price");
      //need to create a random trade number for order
      Long tradeId = nextId.getAndIncrement();

      Order order = new Order(tradeId, qty, price, isBid);
      order._seqId = nextSeqId.getAndIncrement();
      
      if (isBid){
        _bidBookMap.put(tradeId, order);
      }
      else{
        _askBookMap.put(tradeId, order);
      }
    }
   
    //Deletes order from order book expects a trade 
    //and if it is a bid or ask orderfrom user
    public void deleteOrder(Long tradeId, boolean isBid) {
      if (isBid) {
        _bidBookMap.remove(tradeId);
      }
      else {
        _askBookMap.remove(tradeId);
      }
    }
    
    //modifies order from order book expects a trade, the quatity changed to 
    //and if it is a bid or ask orderfrom user
    public void modifyOrder(Long tradeId, int qty, boolean isBid )
    {
      //Integer check
      integerCheck(qty,"Quantity");
      if (isBid) {
        Order order = _bidBookMap.get(tradeId);
        if (null != order) {
          order._quantity = qty;
          //need to update the trade as the order is modified and so is recorded at a later time
          order._seqId = nextSeqId.getAndIncrement();
        }
      }
      else {
        Order order = _askBookMap.get(tradeId);
        if (null != order) {
          order._quantity = qty;
          order._seqId = nextSeqId.getAndIncrement();
        }
      }
    }
    //making sure  quantity is greater than zero as Interger includes zero,
    //could add a upper limit to cap price and quantity allowed
    private void integerCheck(int number, String variableType) 
    {
      if (number <= 0) {
        throw new Error("The " + variableType + "must be greater than 0");
      }
    }
    
    //create a order object
    //Serializable is a maker interface with no fields or methods
    public class Order implements Serializable {
      Long _tradeId;
      //need this as a time reference Id FOR FIFO
      //could use the trade ID instead of this but makes it easier for other developers to follow
      Long _seqId;
      int _quantity;
      int _price;
      boolean _buyOrSell;
      
      //created a constructor to intialise the object
      Order(Long tradeId, int quantity, int price, Boolean buyOrSell){
        _tradeId = tradeId;
        _quantity = quantity;
        _price = price;
        _buyOrSell = buyOrSell;
      }
      //created a method to print out the orders in the final output
      //needed to override the interface class Serializable as it has no method
      @Override
      public String toString() {
        
        return "Order ID: " + _tradeId + ", " + _quantity + " Qty, " + _price + " Price ";
      }
    }
}
