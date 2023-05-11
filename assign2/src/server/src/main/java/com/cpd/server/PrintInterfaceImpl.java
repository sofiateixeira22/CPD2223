package com.cpd.server;

import com.cpd.shared.PrintInterface;
import java.rmi.RemoteException;

public class PrintInterfaceImpl implements PrintInterface{

    public String sayHello() throws RemoteException {  
        for(int i = 0; i < 10000000; i++) {
            System.out.print(".");
        }
        System.out.println("fim");
        return "Hello from server!";
    }
}
