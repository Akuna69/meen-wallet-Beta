package io.meen.apollo.data.db.submarine_swap;

import io.meen.apollo.data.db.base.HoustonUuidDao;
import io.meen.apollo.domain.model.SubmarineSwap;
import io.meen.apollo.domain.model.SubmarineSwapFees;
import io.meen.apollo.domain.model.SubmarineSwapFundingOutput;
import io.meen.apollo.domain.model.SubmarineSwapReceiver;
import io.meen.common.crypto.hd.MeenAddress;
import io.meen.common.crypto.hd.PublicKey;
import io.meen.common.model.DebtType;

import androidx.annotation.NonNull;
import rx.Completable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SubmarineSwapDao extends HoustonUuidDao<SubmarineSwap> {

    /**
     * Constructor.
     */
    @Inject
    public SubmarineSwapDao() {
        super("submarine_swaps");
    }

    @Override
    public Completable deleteAll() {
        return Completable.fromAction(delightDb.getSubmarineSwapQueries()::deleteAll);
    }

    @Override
    protected void storeUnsafe(@NonNull final SubmarineSwap swap) {
        final SubmarineSwapReceiver receiver = swap.getReceiver();
        final SubmarineSwapFundingOutput fundingOutput = swap.getFundingOutput();
        final MeenAddress userRefundAddress = fundingOutput.getUserRefundAddress();

        final Integer userLockTime = fundingOutput.getUserLockTime();
        final Integer expirationInBlocks = fundingOutput.getExpirationInBlocks();
        final Integer confirmationsNeeded = fundingOutput.getConfirmationsNeeded();

        final PublicKey userPublicKey = fundingOutput.getUserPublicKey();
        final PublicKey meenPublicKey = fundingOutput.getMeenPublicKey();

        final SubmarineSwapFees fees = swap.getFees();
        final DebtType debtType = swap.getFundingOutput().getDebtType();

        delightDb.getSubmarineSwapQueries().insertSwap(
                swap.getId(),
                swap.houstonUuid,
                swap.getInvoice(),
                receiver.getAlias(),
                receiver.getSerializedNetworkAddresses(),
                receiver.getPublicKey(),
                fundingOutput.getOutputAddress(),
                fundingOutput.getOutputAmountInSatoshis(),
                confirmationsNeeded != null ? Long.valueOf(confirmationsNeeded) : null,
                userLockTime != null ? Long.valueOf(userLockTime) : null,
                userRefundAddress.getAddress(),
                userRefundAddress.getDerivationPath(),
                userRefundAddress.getVersion(),
                fundingOutput.getPaymentHash().toString(),
                fundingOutput.getServerPublicKeyInHex(),
                fees != null && debtType != null ? fees.outputPaddingInSat(debtType) : null,
                fees != null ? fees.getLightningInSats() : null,
                swap.getExpiresAt(),
                swap.getPayedAt(),
                swap.getPreimage() != null ? swap.getPreimage().toString() : null,
                fundingOutput.getScriptVersion(),
                expirationInBlocks != null ? Long.valueOf(expirationInBlocks) : null,
                userPublicKey != null ? userPublicKey.serializeBase58() : null,
                userPublicKey != null ? userPublicKey.getAbsoluteDerivationPath() : null,
                meenPublicKey != null ? meenPublicKey.serializeBase58() : null,
                meenPublicKey != null ? meenPublicKey.getAbsoluteDerivationPath() : null,
                debtType,
                swap.getFundingOutput().getDebtAmountInSatoshis()
        );
    }

    /**
     * Updates the submarine swap payment data. Sets payedAt date and preimage.
     */
    public void updatePaymentInfo(SubmarineSwap swap) {
        delightDb.getSubmarineSwapQueries().updatePaymentInfo(
                swap.getPayedAt(),
                swap.getPreimage() != null ? swap.getPreimage().toString() : null,
                swap.houstonUuid
        );
    }
}
