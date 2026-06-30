package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "money-tree.runtime")
public class RuntimeProfileProperties {

    private String profile = "local";
    private boolean mockLlm = true;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isMockLlm() {
        return mockLlm;
    }

    public void setMockLlm(boolean mockLlm) {
        this.mockLlm = mockLlm;
    }
}
