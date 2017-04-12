package com.ecreditpal.maas.web.swagger;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.web.endpoint.filter.InjectLookupEventMessageFactory;
import com.ecreditpal.maas.web.endpoint.filter.ModelLogCommitFilter;
import com.ecreditpal.maas.web.endpoint.filter.ModelLogFilter;
import com.ecreditpal.maas.web.endpoint.filter.ModelLogResponseFilter;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/16.
 */
public class SpringWithSwagger extends ResourceConfig {

    public SpringWithSwagger() {
        final String myRestPackage = "com.ecreditpal.maas.web.endpoint";
        final String jacksonPackage = "org.codehaus.jackson.jaxrs";

        final String swaggerJaxrsJsonPackage = "com.wordnik.swagger.jaxrs.json";
        final String swaggerJaxrsListingPackage = "com.wordnik.swagger.jaxrs.listing";

        packages(swaggerJaxrsJsonPackage,
                swaggerJaxrsListingPackage,
                jacksonPackage,
                myRestPackage);

        register(MultiPartFeature.class);
//        register(JacksonJsonProvider.class);
        register(ModelLogFilter.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(InjectLookupEventMessageFactory.class)
                        .to(LookupEventMessage.class).in(RequestScoped.class);
            }
        });
        register(ModelLogResponseFilter.class);
        register(ModelLogCommitFilter.class);
    }


}
