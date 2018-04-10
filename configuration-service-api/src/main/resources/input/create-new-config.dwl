%dw 1.0
%output application/java
---
{

	application : payload.application,
	version : payload.version,
	environment : payload.environment,
	imports : payload.imports default [],
	properties : payload.properties mapObject (value, key) -> {
		(key replace "." with "__") : value
	} unless payload.properties == null otherwise {}
 }