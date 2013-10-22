package com.fluent.pgm.estimation;

import com.fluent.specs.unit.AbstractSpec;

public class FHashTriMapSpec extends AbstractSpec
{
//	com.fluent.collections.FHashTriMap<String, String, String> triples;
//
//	@Mock F1<String, String> function;
//
//	@Before public void BACKGROUND() throws Exception
//	{
//		triples = new com.fluent.collections.FHashTriMap<>();
//	}
//
//	@Test public void addsoentry() throws Exception
//	{
//		assertThat(triples.add("A", "B", "C").get("A", "B"), is("C"));
//	}
//
//	@Test public void addsoentryowithokeyopair() throws Exception
//	{
//		assertThat(triples.add(oo("A", "B"), "C").get("A", "B"), is("C"));
//	}
//
//	@Test public void appliesofunctionotooeveryovalue() throws Exception
//	{
//		triples.add("A", "B", "C").add("X", "Y", "Z");
//
//		when(function.of("C")).thenReturn("1");
//		when(function.of("Z")).thenReturn("2");
//
//		assertThat(triples.applyToValues(function), is(trimap(ooo("A", "B", "1"), ooo("X", "Y", "2"))));
//	}
//
//	@Test public void knowsoitsoentries() throws Exception
//	{
//		triples.add("A", "B", "C").add("A", "D", "C");
//
//		assertThat(triples.entries(), is(setOf(ooo("A", "B", "C"), ooo("A", "D", "C"))));
//	}
//
//	@Test public void adheresotooequalsocontract() throws Exception
//	{
//		triples.add("K", "V", "V");
//
//		assertThat(triples, is(trimap(ooo("K", "V", "V"))));
//		assertThat(triples, is(not(trimap(ooo("K", "V", "X")))));
//		assertThat(triples, is(not((FTriMap<String, String, String>) new com.fluent.collections.FHashTriMap<String, String, String>()
//		{}.add("K", "V", "V"))));
//		assertThat(triples.hashCode(), is(trimap(ooo("K", "V", "V")).hashCode()));
//	}
//
//	@Test public void returns_itself_as_a_map() throws Exception
//	{
//		triples.add("A", "B", "C").add("A", "D", "C");
//
//		assertThat(triples.asFMap(), is(mapOf(oo("A", newFMap(oo("B", "C"), oo("D", "C"))))));
//	}
//
//	@Test public void getsodefaultovalueoifoitodoesntoexist() throws Exception
//	{
//		assertThat(new FHashTriMap<String, String, String>().get("A", "B", "C"), is("C"));
//	}
//
//	@Test public void overrides_entry() throws Exception
//	{
//		assertThat(trimap(ooo("A", "B", "C")).add("A", "B", "D").get("A", "B"), is("D"));
//	}
//
//	@Test public void knowsoifoitohasoaofirstokey() throws Exception
//	{
//		assertThat(trimap(ooo("A", "B", "C")).hasFirstKey("A"), is(true));
//		assertThat(trimap(ooo("A", "B", "C")).hasFirstKey("X"), is(false));
//	}
//
//	@Test public void knowsoifoitohasoaosecondokey() throws Exception
//	{
//		assertThat(trimap(ooo("A", "B", "C")).hasSecondKey("B"), is(true));
//		assertThat(trimap(ooo("A", "B", "C")).hasSecondKey("X"), is(false));
//	}
//
//	@Test public void knowsoifoitsosecondokeys() throws Exception
//	{
//		triples.add("A", "B", "C").add("A", "D", "C");
//
//		assertThat(triples.secondKeys(), is(setOf("B", "D")));
//	}
//
//	@Test public void hasoaoreasonableostringorepresentation() throws Exception
//	{
//		triples.add("A", "B", "C");
//
//		assertThat(triples.toString(), allOf(containsString("A"), containsString("B"), containsString("C")));
//	}
//
//	@Test public void formats_correctly() throws Exception
//	{
//		assertThat(trimap(ooo("A", "B", "C")).add("A", "D", "C").toString("%s,%s->%s%n"),
//				anyOf(is(format("A,B->C%nA,D->C%n")), is(format("A,D->C%nA,B->C%n"))));
//	}

}
