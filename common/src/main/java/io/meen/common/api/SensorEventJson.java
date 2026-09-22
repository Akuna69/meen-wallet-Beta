package io.meen.common.api;

import io.meen.common.dates.MeenZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorEventJson {

    public Long eventId;

    public MeenZonedDateTime eventTimestamp;

    public String eventType;

    public Map<String, Object> eventData;

    public SensorEventJson(
            Long eventId,
            MeenZonedDateTime eventTimestamp,
            String eventType,
            Map<String, Object> eventData
    ) {
        this.eventId = eventId;
        this.eventTimestamp = eventTimestamp;
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public SensorEventJson() {
    }
}
