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

package cacher.aop.impl.simple;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import cacher.aop.MethodInvocationAware;

/**
 * Common functionality for classes that utilize the {@link CacheKey} annotation.
 * 
 * @author Dennis Crissman
 */
public class CacheKeyInterpreter implements MethodInvocationAware {

	private MethodInvocation invocation;

	/*
	 * (non-Javadoc)
	 * @see cacher.aop.MethodInvocationAware#setMethodInvocation(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public void setMethodInvocation(MethodInvocation invocation) {
		this.invocation = invocation;
	}

	/**
	 * Finds the argument that uses {@link CacheKey}. If multiple or none are found, then a runtime exception
	 * will be thrown.
	 * @return argument position annotated with {@link CacheKey}.
	 * @throws MultipleKeyException
	 * @throws NoKeyException
	 */
	public int findKeyPosition(){
		Annotation[][] annotatedArguments = invocation.getMethod().getParameterAnnotations();
		Integer position = null;

		for(int x = 0; x < annotatedArguments.length; x++){
			Annotation[] argumentAnnotations = annotatedArguments[x];
			for(Annotation argAnnotation : argumentAnnotations){
				if(argAnnotation instanceof CacheKey){
					if(position == null){
						position = x;
					}
					else{
						throw new MultipleKeyException();
					}
				}
			}
		}

		if(position == null){
			throw new NoKeyException();
		}

		return position;
	}

	/**
	 * Attempts to convert the passed in keyObj to a {@link List}. Will throw
	 * a {@link ConversionException} if the object cannot be converted.
	 * @param keyObj - Object to be acted on
	 * @return List of Strings
	 * @throws ConversionException
	 */
	public List<String> convertToList(final Object keyObj){
		Collection<? extends Object> col = null;

		if(keyObj instanceof Collection){
			col = (Collection<?>) keyObj;
		}
		else if(keyObj.getClass().isArray()){
			col = Arrays.asList((Object[]) keyObj );
		}
		else{
			throw new ConversionException("Unable to convert " + keyObj.getClass() + " to a Collection");
		}

		List<String> keys = new ArrayList<String>();
		for(Object o : col){
			keys.add(o.toString());
		}

		return keys;
	}

	/**
	 * Thrown when multiple {@link CacheKey}s are given.
	 * 
	 * @author Dennis Crissman
	 */
	public static class MultipleKeyException extends RuntimeException{

		private static final long serialVersionUID = 1558927892689676842L;

		public MultipleKeyException(){
			super("Multiple CacheKey annotations were found, only one can be used as the key for the cache.");
		}

	}

	/**
	 * Thrown when no {@link CacheKey} is provided.
	 * 
	 * @author Dennis Crissman
	 */
	public static class NoKeyException extends RuntimeException{

		private static final long serialVersionUID = 300508928495068387L;

		public NoKeyException(){
			super("No CacheKey annotation was found. One and only one must exist.");
		}

	}

	/**
	 * Thrown when a parameter annotated by {@link CacheKey} cannot be converted properly.
	 * 
	 * @author Dennis Crissman
	 */
	public static class ConversionException extends RuntimeException{

		private static final long serialVersionUID = 2981117821498264832L;

		public ConversionException(String message){
			super(message);
		}

	}

}
