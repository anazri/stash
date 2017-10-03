package com.gaboratorium.stash.HelloWorld;

import com.gaboratorium.stash.HelloWorld.resources.GreeterResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        final GreeterResource greeterResource = new GreeterResource(configuration.getApplicationName());
        environment.jersey().register(greeterResource);
    }
}
