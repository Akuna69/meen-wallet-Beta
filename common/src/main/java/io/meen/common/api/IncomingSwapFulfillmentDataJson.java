package io.meen.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingSwapFulfillmentDataJson {

    public String fulfillmentTxHex;

    public String meenSignatureHex;

    public String outputPath;

    public int outputVersion;


    /**
     * JSON constructor.
     */
    public IncomingSwapFulfillmentDataJson() {
    }

    /**
     * Houston constructor.
     */
    public IncomingSwapFulfillmentDataJson(final String fulfillmentTxHex,
                                           final String meenSignatureHex,
                                           final String outputPath,
                                           final int outputVersion) {
        this.fulfillmentTxHex = fulfillmentTxHex;
        this.meenSignatureHex = meenSignatureHex;
        this.outputPath = outputPath;
        this.outputVersion = outputVersion;
    }
}
