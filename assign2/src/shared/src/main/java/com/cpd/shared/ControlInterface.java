package com.cpd.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ControlInterface extends Remote {

    public String login(String user, String pass) throws RemoteException;
}
