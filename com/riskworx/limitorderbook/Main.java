package com.riskworx.limitorderbook;

import com.riskworx.limitorderbook.LimitOrderBook.Order;

public class Main {

    public static void main(String[] args){
        new Main();
    }
    //Accessing the order books for bid and buy
    LimitOrderBook execute = new LimitOrderBook();
    
    private void printAllOrders() {
      System.out.println("----------------------");
      int count = 1;
      for (Order order: execute.getBidBookMap().values()) {
        System.out.println("Bid book order Queue " + count + " : " + order.toString());
        count++;
      }
      count = 1;
      for (Order order: execute.getAskBookMap().values()) {
        System.out.println("Ask book order Queue " + count + " : " + order.toString());
        count++;
      }
    }
    
    public Main()
    {
        //Previous orders
        execute.existingOrderBook();
        //printAllOrders();
        execute.addOrder(1,  20, true);
        execute.addOrder(300,  20, true);
        //printAllOrders();
        
        execute.modifyOrder(Long.valueOf(6), 200, true);
        execute.modifyOrder(Long.valueOf(4), 2000, false);
        //printAllOrders();
        
        execute.deleteOrder(Long.valueOf(2), true);
        printAllOrders();
    
    }
}


