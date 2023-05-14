package com.cpd.server;

import com.cpd.shared.PrintInterface;
import java.rmi.RemoteException;

public class PrintInterfaceImpl implements PrintInterface{

    public String sayHello() throws RemoteException {  
        return "Hello from server!";
    }
}
