/*
 * Copyright 2013 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions and must accompany binary
 * distributions with such author attributions.
 */

package testframework.util;

import org.easymock.EasyMock;
import org.easymock.IMockBuilder;

/**
 * <p>Helper class for creating EasyMocks based on a concrete instances, rather than an interface.</p>
 * <p>This is useful when you want to only mock certain methods, but want others to function as normal.</p>
 * <p>Please Note: All methods provided will need to be implemented.</p>
 * 
 * @author dcrissman
 */
public final class EasyMockHelper {

	private EasyMockHelper(){}

	/**
	 * Creates a mock instance of type, where only the provided methods are mockable. All other methods
	 * will functional normally.
	 * @param type - T
	 * @param methodsToMock - Array of method names to mock.
	 * @return mocked instance of T
	 */
	public static <T> T mockInstance(Class<T> type, String... methodsToMock){
		IMockBuilder<T> builder = EasyMock.createMockBuilder(type);
		builder.addMockedMethods(methodsToMock);
		return builder.createMock();
	}

	/**
	 * Same as {@link #mockInstance(Class, String...)}, but catered to overloaded method signatures.
	 * @param type - T
	 * @param methodsToMock - Array of {@link MethodWithParameters}
	 * @return mocked instance of T
	 */
	public static <T> T mockInstance(Class<T> type, MethodWithParameters... methodsToMock){
		IMockBuilder<T> builder = EasyMock.createMockBuilder(type);
		for(MethodWithParameters method : methodsToMock){
			builder.addMockedMethod(method.getMethodName(), method.getParameters());
		}
		return builder.createMock();
	}

	public static class MethodWithParameters{

		private String methodName;
		private Class<?>[] parameters;

		public MethodWithParameters(){}

		public MethodWithParameters(String methodName, Class<?>... parameters){
			this();
			setMethodName(methodName);
			setParameters(parameters);
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public Class<?>[] getParameters() {
			return parameters;
		}

		public void setParameters(Class<?>... parameters) {
			this.parameters = parameters;
		}

	}

}
