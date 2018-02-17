package org.mule.modules.caas.internal;

import org.mule.modules.caas.model.ApplicationConfiguration;

import java.util.*;


/**
 * This class is a temporary workaround until I find a more elegant way of making the configurations
 * available to the module and just loading them once.
 */
public class StaticConfigCache {

    private Map<String, List<ApplicationConfiguration>> staticConfigurationsCache;

    private static StaticConfigCache instance = new StaticConfigCache();

    private StaticConfigCache() {
        staticConfigurationsCache = new HashMap<>();
    }

    public static StaticConfigCache get() {
        return instance;
    }

    public synchronized void store(String serviceUrl, ApplicationConfiguration config) {
        List<ApplicationConfiguration> serviceCache = staticConfigurationsCache.getOrDefault(serviceUrl, new LinkedList<>());

        ApplicationConfiguration existing = serviceCache.stream()
                .filter(a -> sameCoordinates(a, config))
                .findAny().orElse(null);

        //if is brand new, add it.
        if (existing == null) {
            serviceCache.add(config);
        }

        //update the cache.
        staticConfigurationsCache.put(serviceUrl, serviceCache);
    }


    private boolean sameCoordinates(ApplicationConfiguration a , ApplicationConfiguration b) {
        return a.getName().equals(b.getName()) &&
                a.getVersion().equals(b.getVersion()) &&
                a.getEnvironment().equals(b.getEnvironment());
    }

    public synchronized Optional<ApplicationConfiguration> find(String serviceUrl, String app, String ver, String env) {

        List<ApplicationConfiguration> serviceCache = staticConfigurationsCache.getOrDefault(serviceUrl, new LinkedList<>());

        return serviceCache.stream()
                .filter(config -> {
                    return config.getName().equals(app) &&
                            config.getVersion().equals(ver) &&
                            config.getEnvironment().equals(env);
                }).findAny();
    }

}
