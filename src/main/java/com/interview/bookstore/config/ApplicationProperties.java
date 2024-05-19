package com.interview.bookstore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific to Bookstore.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Api api = new Api();

    public Api getApi() {
        return api;
    }

    public static class Api {

        private final NyTimes nytimes = new NyTimes();

        public NyTimes getNytimes() {
            return nytimes;
        }

        public static class NyTimes {

            private String key;
            private String baseurl;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getBaseurl() {
                return baseurl;
            }

            public void setBaseurl(String baseurl) {
                this.baseurl = baseurl;
            }
        }
    }
}
