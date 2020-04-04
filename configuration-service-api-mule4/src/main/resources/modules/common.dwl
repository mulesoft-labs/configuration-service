%dw 2.0
/**
 * Function to map error
 */
fun mapError(error , correlationId, message, code) = 
 {
	"success": false,
	"correlationId": correlationId,
	"timestamp": now(),
	"errorDetails": [{
		"errorCode": code,
		"message": message
	}]
}

