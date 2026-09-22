package io.muun.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeenAddressJson {

    @NotNull
    public Integer version;

    @NotNull
    public String derivationPath;

    @NotNull
    public String address;

    /**
     * Json constructor.
     */
    public MeenAddressJson() {
    }

    /**
     * Manual constructor.
     */
    public MeenAddressJson(Integer version, String derivationPath, String address) {
        this.version = version;
        this.derivationPath = derivationPath;
        this.address = address;
    }
}
