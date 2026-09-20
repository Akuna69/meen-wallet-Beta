package io.meen.apollo.domain.action.operation;

import io.meen.apollo.data.db.contact.ContactDao;
import io.meen.apollo.domain.action.base.BaseAsyncAction1;
import io.meen.apollo.domain.model.Contact;
import io.meen.apollo.domain.model.OperationUri;
import io.meen.apollo.domain.model.PaymentRequest;

import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ResolveMeenUriAction extends BaseAsyncAction1<OperationUri, PaymentRequest> {

    private final ContactDao contactDao;

    /**
     * Resolves a Meen URI, fetching User and/or Contact as needed.
     */
    @Inject
    public ResolveMeenUriAction(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public Observable<PaymentRequest> action(OperationUri operationUri) {
        return Observable.fromCallable(() -> resolveMeenUri(operationUri));
    }

    private PaymentRequest resolveMeenUri(OperationUri uri) {
        switch (uri.getHost()) {
            case OperationUri.MUUN_HOST_CONTACT:
                final Contact contact = contactDao
                        .fetchByHid(uri.getContactHid())
                        .toBlocking()
                        .first();

                return PaymentRequest.toContact(contact);

            case OperationUri.MUUN_HOST_EXTERNAL:
                final String externalAddress = uri.getExternalAddress();
                return PaymentRequest.toAddress(externalAddress);

            default:
                throw new IllegalArgumentException("Invalid host: " + uri.getHost());
        }
    }
}
