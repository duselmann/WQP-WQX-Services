package gov.usgs.cida.wqp.dao;

import java.util.List;
import java.util.Map;

public interface ICountDao extends IDao {
	
	static String QUERY_SELECT_ID = ".count";
	
	/** 
	 * This Dao will grab all of the record counts for the given type and parameters.
	 * @param nameSpace - the type of counts we are looking for.
	 * @param parameterMap - the map of query parameters from the http request
	 * @return - the list of counts.
	 */
	List<Map<String, Object>> getCounts(String nameSpace, Map<String, Object> parameterMap);
	
}