package com.gotcharoom.gdp.platform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SteamRequest extends PlatformCallbackRequest {
    private String openid_ns;
    private String openid_mode;
    private String openid_op_endpoint;
    private String openid_claimed_id;
    private String openid_identity;
    private String openid_return_to;
    private String openid_response_nonce;
    private String openid_assoc_handle;
    private String openid_signed;
    private String openid_sig;

    @Override
    public String getPlatformUserId() {
        return extractSteamId(this.openid_claimed_id);
    }

    private String extractSteamId(String claimedId) {
        if (claimedId == null) return null;

        int lastSlash = claimedId.lastIndexOf("/");
        if (lastSlash == -1 || lastSlash + 1 >= claimedId.length()) {
            return null;
        }

        return claimedId.substring(lastSlash + 1);
    }
}
