package gov.usgs.cida.wqp.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static gov.usgs.cida.wqp.util.HttpConstants.*;

public enum EndPoint {
	RESULT  (ENDPOINT_RESULT, RESULT_SEARCH_ENPOINT, HEADER_RESULT),
	STATION (ENDPOINT_STATION, STATION_SEARCH_ENPOINT,  HEADER_SITE),
	SIMPLESTATION    (ENDPOINT_SIMPLE_STATION, SIMPLE_STATION_ENDPOINT, HEADER_SITE),
	BIOLOGICALRESULT (ENDPOINT_BIOLOGICAL_RESULT, BIOLOGICAL_RESULT_ENPOINT, HEADER_RESULT);
	
	private static Map<String, EndPoint> codeMap = new HashMap<String, EndPoint>();
	
	static {
		for (EndPoint endpoint : EndPoint.values()) {
			codeMap.put("/" + endpoint.URI.toUpperCase(), endpoint);
		}
	}
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	public final String COUNT_HEADER_NAME;
	public final String NAME;
	public final String URI;
	
	private EndPoint (String endpointName, String endpointUri, String headerName) {
		log.trace(getClass().getName());
		NAME = endpointName;
		URI  = endpointUri;
		COUNT_HEADER_NAME = headerName;
	}
	
	public static EndPoint getEnumByCode(String code) {
		if (code == null || code.isEmpty()) {
			return null;
		}
		try {
			String endpoint = code.substring(code.indexOf('/', 2), code.length()).toUpperCase();
			return codeMap.get(endpoint);
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}
}