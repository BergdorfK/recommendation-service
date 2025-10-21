package com.starbank.recommendation_service.management;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Component
public class AppInfoContributor implements InfoContributor {
    private final String appName;
    private final String appVersion;

    public AppInfoContributor(ObjectProvider<BuildProperties> build) {
        BuildProperties bp = build.getIfAvailable();
        this.appName = (bp != null && bp.getName() != null) ? bp.getName() : "recommendation-service";
        this.appVersion = (bp != null && bp.getVersion() != null) ? bp.getVersion() : "dev";
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("name", appName);
        builder.withDetail("version", appVersion);
    }
}