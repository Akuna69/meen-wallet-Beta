package io.meen.common.crypto.hd;

import io.meen.common.api.MeenOutputJson;

import javax.validation.constraints.NotNull;

public class MeenOutput {

    @NotNull
    private final String txId;

    private final int index;

    private final long amount;

    public static MeenOutput fromJson(MeenOutputJson json) {
        return new MeenOutput(json.txId, json.index, json.amount);
    }

    /**
     * Constructor.
     */
    public MeenOutput(String txId, int index, long amount) {
        this.txId = txId;
        this.index = index;
        this.amount = amount;
    }

    public String getTxId() {
        return txId;
    }

    public int getIndex() {
        return index;
    }

    public long getAmount() {
        return amount;
    }

    public MeenOutputJson toJson() {
        return new MeenOutputJson(txId, index, amount);
    }
}
