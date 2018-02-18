package org.mule.modules.caas.internal;

import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.model.ApplicationConfiguration;

import java.util.*;


/**
 * This class is a temporary workaround until I find a more elegant way of making the configurations
 * available to the module and just loading them once.
 */
public class StaticConfigCache {

    private Map<String, List<ApplicationConfiguration>> staticConfigurationsCache;
    private Map<String, String> urlMappings;

    private static StaticConfigCache instance = new StaticConfigCache();

    private StaticConfigCache() {
        staticConfigurationsCache = new HashMap<>();
        urlMappings = new HashMap<>();
    }

    public static StaticConfigCache get() {
        return instance;
    }

    public synchronized void store(String configId, String serviceUrl, ApplicationConfiguration config) {
        List<ApplicationConfiguration> serviceCache = staticConfigurationsCache.getOrDefault(configId, new LinkedList<>());

        ApplicationConfiguration existing = serviceCache.stream()
                .filter(a -> sameCoordinates(a, config))
                .findAny().orElse(null);

        //if is brand new, add it.
        if (existing == null) {
            serviceCache.add(config);
        }

        //update the cache.
        staticConfigurationsCache.put(configId, serviceCache);
        urlMappings.put(configId, serviceUrl);
    }


    private boolean sameCoordinates(ApplicationConfiguration a , ApplicationConfiguration b) {
        return a.getName().equals(b.getName()) &&
                a.getVersion().equals(b.getVersion()) &&
                a.getEnvironment().equals(b.getEnvironment());
    }

    public synchronized Optional<ApplicationConfiguration> find(String configName) {
        List<ApplicationConfiguration> serviceCache = staticConfigurationsCache.getOrDefault(configName, new LinkedList<>());
        return serviceCache.stream().findAny();
    }

    public synchronized Optional<ApplicationConfiguration> findOne() throws ConfigurationServiceException {
         return staticConfigurationsCache.entrySet()
                 .stream().findAny()
                 .orElseThrow(() -> new ConfigurationServiceException("No configuration present"))
                 .getValue()
                 .stream()
                 .findAny();
    }

    public Optional<String> getServiceUrl(String configId) {
        String ret =  urlMappings.get(configId);

        if (ret == null && !urlMappings.isEmpty()) {
            ret = urlMappings.entrySet()
                    .stream().findAny()
                    .get().getValue();
        }

        return Optional.ofNullable(ret);
    }

}
