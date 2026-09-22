package io.meen.common.api.messages;

public abstract class AbstractMessage implements Message {

    public String toLog() {
        return getSpec().messageType;
    }

}
