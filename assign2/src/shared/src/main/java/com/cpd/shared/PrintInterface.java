package com.cpd.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrintInterface extends Remote {

    public String sayHello() throws RemoteException;
}
