package com.cpd.shared.message;

import java.io.Serializable;

public record MsgInfo(
        Status status,
        Stage stage,
        Long timeLeft)
            implements Serializable { }
