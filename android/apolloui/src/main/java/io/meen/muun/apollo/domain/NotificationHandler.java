package io.meen.apollo.domain;

import io.meen.common.api.beam.notification.NotificationJson;
import io.meen.common.api.messages.MessageSpec;

import rx.Completable;
import rx.functions.Func2;

class NotificationHandler {

    public final MessageSpec spec;
    public final Func2<NotificationJson, Long, Completable> handler;

    /**
     * Constructor.
     */
    public NotificationHandler(MessageSpec spec,
                               Func2<NotificationJson, Long, Completable> handler) {
        this.spec = spec;
        this.handler = handler;
    }
}
