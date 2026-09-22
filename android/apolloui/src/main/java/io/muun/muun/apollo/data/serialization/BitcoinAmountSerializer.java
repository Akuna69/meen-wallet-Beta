package io.meen.apollo.data.serialization;

import io.meen.apollo.domain.model.BitcoinAmount;
import io.meen.common.api.BitcoinAmountJson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BitcoinAmountSerializer extends JsonSerializer<BitcoinAmount> {

    @Override
    public void serialize(BitcoinAmount value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeObject(new BitcoinAmountJson(
                value.inSatoshis,
                value.inInputCurrency,
                value.inPrimaryCurrency
        ));
    }
}
