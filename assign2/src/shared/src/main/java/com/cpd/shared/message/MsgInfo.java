package com.cpd.shared.message;

import java.io.Serializable;

public record MsgInfo(
        Status status,
        Stage stage,
        String roomID,
        Long timeLeft)
            implements Serializable { }
