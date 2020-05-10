package com.liaochente.lessdfs.util;

import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageHeader;


public class LessMessageUtils {

    private final static LessMessageUtils LESS_MESSAGE_UTILS = new LessMessageUtils();

    public final static ThreadLocal<LessMessage> LESS_MESSAGE_THREAD_LOCAL = new ThreadLocal<>();


    public final static LessMessageUtils create() {
        if (LESS_MESSAGE_THREAD_LOCAL.get() == null) {
            LessMessage lessMessage = new LessMessage();
            LESS_MESSAGE_THREAD_LOCAL.set(lessMessage);
        }
        return LESS_MESSAGE_UTILS;
    }

    public final static LessMessageUtils createHeader() {
        LessMessage lessMessage = LESS_MESSAGE_THREAD_LOCAL.get();
        if (lessMessage == null) {
            //todo exception;

        }

        LessMessageHeader header = new LessMessageHeader();
        lessMessage.setHeader(header);

        LESS_MESSAGE_THREAD_LOCAL.set(lessMessage);
        return LESS_MESSAGE_UTILS;
    }
}
