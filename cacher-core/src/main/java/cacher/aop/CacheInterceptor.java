package cacher.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.fetcher.FetchManager;
import cacher.fetcher.FetchMultiple;
import cacher.fetcher.FetchSingle;

/**
 * <p>Allows caching to be added to an existing method by simply adding the
 * {@link FetcherMethod} annotation to a method</p>
 * 
 * TODO: Complete documentation
 * 
 * <p><b>NOTE:</b> Bulk fetches require that the annotated method returns a Map&#60;String, Object&#62;</p>
 * 
 * @author Dennis Crissman
 * 
 * @see FetcherMethod
 */
public class CacheInterceptor implements MethodInterceptor{

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheInterceptor.class);

	private FetchManager fetchManager;

	public CacheInterceptor(){}

	@Inject
	public CacheInterceptor(FetchManager fetchManger){
		this.fetchManager = fetchManger;
	}

	@Inject
	public void setFetchManager(FetchManager fetchManager){
		this.fetchManager = fetchManager;
	}

	public FetchManager getFetchManager(){
		return fetchManager;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable { //NOSONAR
		if(getFetchManager() == null){
			throw new IllegalStateException("A FetchManager instance must set.");
		}

		Method method = invocation.getMethod();
		FetcherMethod cacheAnnotation = method.getAnnotation(FetcherMethod.class);
		if(cacheAnnotation == null){
			throw new IllegalArgumentException(
					"Unable to intercept a method without the FetcherMethod annotation: " + method.getName());
		}

		try{
			KeyGenerator keyGenerator = cacheAnnotation.keyGenerator().newInstance();

			if(cacheAnnotation.fetchBulk()){
				return getFetchManager().fetchMultiple(
						cacheAnnotation.prefix(),
						keyGenerator.generateKeys(invocation.getArguments()),
						new MultipleFetcher(invocation, cacheAnnotation.keyCleaner().newInstance()));
			}
			else{
				return getFetchManager().fetchSingle(
						cacheAnnotation.prefix(),
						keyGenerator.generateKey(invocation.getArguments()),
						new SingleFetcher(invocation));
			}
		}
		//TODO catch exceptions thrown from method invocation, as invoking again could be problematic.
		catch(Exception e){
			/*
			 * General catch all in case something unexpected happens, this will ensure that the
			 * FetcherMethod is still invoked.
			 */
			LOGGER.info("An unexpected exception was thrown while attempting to cache '" + method.getName()
					+ "'. Method will now be invoked without caching enabled.", e);
			return invocation.proceed();
		}
	}

	private class SingleFetcher implements FetchSingle<Object>{

		private final MethodInvocation invocation;

		public SingleFetcher(MethodInvocation invocation){
			this.invocation = invocation;
		}

		@Override
		public Object fetch(String key) {
			try {
				return invocation.proceed();
			} catch (Throwable e) { //NOSONAR
				String methodName = invocation.getMethod().toString();
				throw new RuntimeException("Unable to fetch key for method " + methodName + ": " + key, e);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class<Object> getType() {
			return (Class<Object>)invocation.getMethod().getReturnType();
		}

	}

	private class MultipleFetcher implements FetchMultiple<Object>{

		private final MethodInvocation invocation;
		private final KeyCleaner cleaner;

		public MultipleFetcher(MethodInvocation invocation, KeyCleaner cleaner){
			this.invocation = invocation;
			this.cleaner = cleaner;
		}

		@Override
		public Map<String, Object> fetch(List<String> keys) {
			try {
				cleaner.clean(invocation.getArguments(), keys);

				Object obj = invocation.proceed();

				if(obj instanceof Map){
					Map<String, Object> results = new HashMap<String, Object>();

					@SuppressWarnings("unchecked")
					Map<Object, Object> fetchedResults = (Map<Object, Object>) obj;
					for(Entry<Object, Object> entry : fetchedResults.entrySet()){
						results.put(entry.getKey().toString(), entry.getValue());
					}

					return results;
				}
				else{
					String methodName = invocation.getMethod().toString();
					throw new IllegalArgumentException("Wrapped method must return a Map<String, Object>: " + methodName);
				}

			} catch (Throwable e) { //NOSONAR
				String methodName = invocation.getMethod().toString();
				throw new RuntimeException("Unable to fetch keys for method " + methodName + ": " + keys, e);
			}
		}

		@Override
		public Class<Object> getType() {
			return Object.class;
		}

	}

}