package com.cpd.shared.message;

import java.io.Serializable;

public record MsgInfo(
        Status status,
        Stage stage,
        Integer round,
        Long timeLeft)
            implements Serializable { }
