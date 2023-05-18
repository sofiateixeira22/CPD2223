package com.cpd.shared;

import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ControlInterface extends Remote {

    MsgString login(String user, String pass) throws RemoteException;
    MsgInfo ping(String token) throws RemoteException;
}
