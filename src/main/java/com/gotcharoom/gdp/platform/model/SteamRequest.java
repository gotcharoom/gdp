package com.gotcharoom.gdp.platform.model;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamRequest extends PlatformCallbackRequest {
    private String openidNs;
    private String openidMode;
    private String openidOpEndpoint;
    private String openidClaimedId;
    private String openidIdentity;
    private String openidReturnOo;
    private String openidResponseNonce;
    private String openidAssocHandle;
    private String openidSigned;
    private String openidSig;

    @Override
    public String getPlatformUserId() {
        return extractSteamId(this.openidClaimedId);
    }

    private String extractSteamId(String claimedId) {
        if (claimedId == null) return null;

        int lastSlash = claimedId.lastIndexOf("/");
        if (lastSlash == -1 || lastSlash + 1 >= claimedId.length()) {
            return null;
        }

        return claimedId.substring(lastSlash + 1);
    }

    public static SteamRequest fromParams(Map<String, String> params) {
        return SteamRequest.builder()
                .openidNs(params.get("openid.ns"))
                .openidMode(params.get("openid.mode"))
                .openidOpEndpoint(params.get("openid.op_endpoint"))
                .openidClaimedId(params.get("openid.claimed_id"))
                .openidIdentity(params.get("openid.identity"))
                .openidReturnOo(params.get("openid.return_to"))
                .openidResponseNonce(params.get("openid.response_nonce"))
                .openidAssocHandle(params.get("openid.assoc_handle"))
                .openidSigned(params.get("openid.signed"))
                .openidSig(params.get("openid.sig"))
                .build();
    }
}
