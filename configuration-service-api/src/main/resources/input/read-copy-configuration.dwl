%dw 1.0
%output application/java
---
{
	"application": flowVars.existingConfiguration.application,
	"version": flowVars.toVersion,
	"environment": flowVars.existingConfiguration.environment,
	"imports": flowVars.existingConfiguration.imports,
	"properties": flowVars.existingConfiguration.properties mapObject (value, key) -> {
		(key replace "." with "__") : value
	} unless flowVars.existingConfiguration.properties == null otherwise {
	}
}