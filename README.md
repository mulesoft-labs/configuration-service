# SpringCloudConfig Anypoint Connector

Module to integrate with Spring Cloud Config server and load configurations as Property Placeholders.

Usage in a mule app:

```xml

<spring-cloud-config:config name="Spring_Cloud_Config" applicationName="foo" profiles="a,b,c" 
	label="abc" configServerBaseUrl="http://localhost:8888/"/>
    
```

All properties defined in the configuration server's repository for the application, profiles and labels will be available in the application in the form of placeholders.