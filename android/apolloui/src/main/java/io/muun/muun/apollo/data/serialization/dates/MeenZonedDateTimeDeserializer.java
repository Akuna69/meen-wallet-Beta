package io.meen.apollo.data.serialization.dates;

import io.meen.common.dates.MeenZonedDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MeenZonedDateTimeDeserializer extends JsonDeserializer<MeenZonedDateTime> {

    @Override
    public MeenZonedDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {

        final ObjectCodec codec = parser.getCodec();
        final String serialized = codec.readValue(parser, String.class);
        return ApolloZonedDateTime.fromString(serialized);
    }
}
