%dw 1.0
%output application/java
---

{

	application : payload.application,
	version : payload.version,
	environment : payload.environment,
	imports : payload.imports,
	documents : payload.documents default [], 
	properties : payload.properties mapObject (value, key) -> {
		(key replace '__' with '.') : value
	} unless payload.properties == null otherwise {}
} unless payload == null otherwise null