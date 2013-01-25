package cacher.fetcher;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import testframework.util.EasyMockHelper;
import testframework.util.EasyMockHelper.MethodWithParameters;

import cacher.fetcher.FetchMultipleOneAtATime;


public class TestFetchMultipleOneAtATime {

	private final FetchMultipleOneAtATime<Person> fetcher =
			EasyMockHelper.mockInstance(
					PersonFetcher.class,
					new MethodWithParameters("fetch", String.class));

	@Before
	public void setup(){
		reset(fetcher);
		replay(fetcher);
	}

	@After
	public void after(){
		verify(fetcher);
	}

	@Test
	public void test(){
		reset(fetcher);
		expect(fetcher.fetch(EasyMock.anyObject(String.class))).andReturn(new Person("Frank")).once();
		expect(fetcher.fetch(EasyMock.anyObject(String.class))).andReturn(new Person("Marge")).once();
		replay(fetcher);

		Map<String, Person> people = fetcher.fetch(Arrays.asList("1", "2"));

		assertEquals(2, people.size());
		assertEquals("Frank", people.get("1").name);
		assertEquals("Marge", people.get("2").name);
	}

	public static class Person {
		public String name;

		public Person(String name){
			this.name = name;
		}
	}

	public static class PersonFetcher extends FetchMultipleOneAtATime<Person>{

		@Override
		public Class<Person> getType(){
			return Person.class;
		}

		@Override
		public Person fetch(String key){
			throw new UnsupportedOperationException();
		}

	}

}
