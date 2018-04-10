%dw 1.0
%output application/java

%var toAddProps = (payload.properties pluck $$ default []) -- (flowVars.existingConfiguration.properties pluck $$ default [])
---

{
	properties : (flowVars.existingConfiguration.properties mapObject (value, key) -> {
	(key replace "." with "__") : payload.properties[key] when payload.properties[key]? otherwise value}
	unless flowVars.existingConfiguration.properties == null otherwise {}) 

++ {(toAddProps default [] map (prop) -> {
	(prop replace "." with "__") : payload.properties[prop]})}
	
}