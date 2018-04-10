%dw 1.0
%output application/java
---
{
	application : flowVars.existingConfiguration.application,
	version : flowVars.existingConfiguration.version,
	environment : flowVars.existingConfiguration.environment,
	imports : payload.imports default [],
	properties : payload.properties mapObject (value, key) -> {
		(key replace "." with "__") : value
	} unless payload.properties == null otherwise {
	}
}