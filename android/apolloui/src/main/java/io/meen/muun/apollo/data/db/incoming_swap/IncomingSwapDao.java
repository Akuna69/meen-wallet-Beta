package io.meen.apollo.data.db.incoming_swap;

import io.meen.apollo.data.db.base.HoustonUuidDao;
import io.meen.apollo.domain.model.IncomingSwap;
import io.meen.apollo.domain.model.Sha256Hash;

import androidx.annotation.NonNull;
import rx.Completable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IncomingSwapDao extends HoustonUuidDao<IncomingSwap> {

    /**
     * Constructor.
     */
    @Inject
    public IncomingSwapDao() {
        super("incoming_swaps");
    }

    @Override
    public Completable deleteAll() {
        return Completable.fromAction(delightDb.getIncomingSwapQueries()::deleteAll);
    }

    @Override
    protected void storeUnsafe(@NonNull final IncomingSwap element) {
        final Sha256Hash preimage = element.getPreimage();
        delightDb.getIncomingSwapQueries().insertIncomingSwap(
                element.getId(),
                element.houstonUuid,
                element.getPaymentHash().toString(),
                element.getSphinxPacketHex(),
                element.getCollectInSats(),
                element.getPaymentAmountInSats(),
                preimage != null ? preimage.toString() : null
        );
    }
}
