package org.jfree.data.time.junit;


import junit.framework.TestCase;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import java.util.function.Supplier;

public abstract class IntermediateTestCase extends TestCase {
	public IntermediateTestCase(String name) {
		super(name);
	}

	protected void testGetFirstMillisecondExtracted(long arg1) {
		Locale saved = Locale.getDefault();
		Locale.setDefault(Locale.UK);
		TimeZone savedZone = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
		RegularTimePeriod d = createTimePeriod();
		assertEquals(arg1, d.getFirstMillisecond());
		Locale.setDefault(saved);
		TimeZone.setDefault(savedZone);
	}
	
	public abstract RegularTimePeriod createTimePeriod();
}