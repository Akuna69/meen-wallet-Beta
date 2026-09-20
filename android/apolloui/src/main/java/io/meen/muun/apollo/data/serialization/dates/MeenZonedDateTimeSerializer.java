package io.meen.apollo.data.serialization.dates;

import io.meen.common.dates.MeenZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MeenZonedDateTimeSerializer extends JsonSerializer<MeenZonedDateTime> {

    @Override
    public void serialize(MeenZonedDateTime value, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        final ApolloZonedDateTime apolloZonedDateTime = (ApolloZonedDateTime) value;

        gen.writeString(apolloZonedDateTime.toString());
    }
}
