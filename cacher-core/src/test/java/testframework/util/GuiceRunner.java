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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A Guice JUnit Test Runner
 * 
 * @author Dennis Crissman
 */
public class GuiceRunner extends BlockJUnit4ClassRunner {

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface UseModules{
		boolean resetContext() default false;
		Class<? extends Module>[] value();
	}

	private final boolean resetContext;
	private final Class<? extends Module>[] modules;
	private Injector injector = null;

	public GuiceRunner(Class<?> klass) throws InitializationError {
		super(klass);
		UseModules modules = klass.getAnnotation(UseModules.class);
		if(modules == null){
			throw new IllegalStateException("The UseModules annotation must be specified.");
		}

		this.resetContext = modules.resetContext();
		this.modules = modules.value();
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		createInjector().injectMembers(test);
		return test;
	}

	private Injector createInjector() throws Exception{
		if(resetContext){
			injector = null;
		}
		if(injector == null){
			injector = Guice.createInjector(instantiateModules());
		}
		return injector;
	}

	private Iterable<Module> instantiateModules() throws InstantiationException, IllegalAccessException{
		List<Module> instances = new ArrayList<Module>();
		for(Class<? extends Module> clazz : modules){
			instances.add(clazz.newInstance());
		}
		return instances;
	}

}
