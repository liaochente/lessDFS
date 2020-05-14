package com.liaochente.lessdfs.exception;

import com.liaochente.lessdfs.constant.LessStatus;

public class LessServerException extends RuntimeException {

    private LessStatus status;

    public LessServerException(LessStatus status) {
        super(null, null, false, false);
        this.status = status;
    }
}
