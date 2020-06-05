package farsight.testing.constants;

import farsight.utils.PropertyLoader;

public final class ServiceNames {

	public static final String FIXED_RESPONSE_MOCK;
	public static final String SETUP_ASSERTION;
	public static final String ASSERTION_INVOKE_COUNT;
	public static final String REGISTER_EXCEPTION;
	public static final String JEXL_RESPONSE_MOCK;
	public static final String PIPELINE_CAPTURE_INTERCEPTOR;
	public static final String PIPELINE_CAPRURE_GETTER;
	public static final String TEARDOWN_FRAMEWORK;
	
	private ServiceNames() {
		//not meant to be instantiated
	}
	
	static {
		//initialize constants
		PropertyLoader loader = PropertyLoader.builder()
				.addClasspath("farsight/testing/servicenames.properties", true)
				.addFile(PropertyLoader.getSystemProperty("testing-servicenames", "testing-servicenames.properties"), false)
				.build();
		
		FIXED_RESPONSE_MOCK = loader.getString("FIXED_RESPONSE_MOCK");
		SETUP_ASSERTION = loader.getString("SETUP_ASSERTION");
		ASSERTION_INVOKE_COUNT = loader.getString("ASSERTION_INVOKE_COUNT");
		REGISTER_EXCEPTION = loader.getString("REGISTER_EXCEPTION");
		JEXL_RESPONSE_MOCK = loader.getString("JEXL_RESPONSE_MOCK");
		PIPELINE_CAPTURE_INTERCEPTOR = loader.getString("PIPELINE_CAPTURE_INTERCEPTOR");
		PIPELINE_CAPRURE_GETTER = loader.getString("PIPELINE_CAPRURE_GETTER");
		TEARDOWN_FRAMEWORK = loader.getString("TEARDOWN_FRAMEWORK");
	}

}
