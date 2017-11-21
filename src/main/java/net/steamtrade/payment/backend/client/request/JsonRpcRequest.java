package net.steamtrade.payment.backend.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class JsonRpcRequest {

    @JsonProperty("jsonrpc")
    protected String jsonrpc = "2.0";

    @JsonProperty("method")
    protected String method;

    @JsonProperty("params")
    protected List<String> params = new ArrayList<>();

    @JsonProperty("id")
    protected Integer id = 1;
}
