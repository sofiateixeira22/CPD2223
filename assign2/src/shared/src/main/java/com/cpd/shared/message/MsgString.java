package com.cpd.shared.message;

import java.io.Serializable;

public record MsgString(Status status, String payload) implements Serializable { }
