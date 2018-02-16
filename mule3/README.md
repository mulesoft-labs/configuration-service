# Configuration as a Service Connector

Connector and API definition to apply configurations in mule from a Configuration Service.

Usage in a mule app:

```xml
    
    <configuration-service:config name="Configuration_Service" 
      applicationName="myApp" 
      configServerBaseUrl="http://localhost:8081/api/configuration" 
      environment="DEV" version="1.0"
      doc:name="Configuration Service: Configuration Service Connector"/>
    
    
    <!-- example of dynamic property loading -->
    <http:listener-config name="HTTP_Listener_Configuration" host="${http.host}" port="${http.port}" doc:name="HTTP Listener Configuration"/>
    <flow name="test-caas-connectorFlow">
        <http:listener config-ref="HTTP_Listener_Configuration" path="/" doc:name="HTTP"/>
        <set-payload value="Property is: ${a.b.c}" />
    </flow>
    
    
    <!-- example of dynamic dataweave transformation using caas -->
    <flow name="test-caas-connectorFlow1">
        <http:listener config-ref="HTTP_Listener_Configuration" path="/dw" doc:name="HTTP"/>
        <enricher target="#[flowVars['script']]" source="#[message.payloadAs(java.lang.String)]" doc:name="Message Enricher">
        	<processor-chain doc:name="Processor Chain">
        		<configuration-service:read-document config-ref="Configuration_Service" key="generate_default.dwl" doc:name="Configuration Service"/>
                <logger level="INFO" doc:name="Logger"/>
        	</processor-chain>
        </enricher>
        
        <set-payload value="#[dw(flowVars['script'], 'application/json')]" mimeType="application/json" doc:name="Set Payload"/>
    </flow>
    
```

All properties defined in the configuration server's repository for the application, profiles and labels will be available in the application in the form of placeholders.


## How To Build

This is a standard 'Anypoint Connector' project. And it is mavenized, thus it can be built using the standard toolset.

In short, this connector can be built using:

    $ mvn clean install -Ddevkit.studio.package.skip=false
   
