package gregtech.api.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

	/**
	 * Workaround for Nomifactory 1.2.2.x worlds with Bansoukou-patched version string
	 * so that they are properly remapped on world load
	 */
	@Test
	public void parse_omits_text_after_numbers() {
		String version = "1.8.4.419-exa3";
		Version expected = new Version(1, 8, 4, 419);
		assertEquals("Failed to omit non-numeric trailing text", expected, Version.parse(version));
	}

	/**
	 * As a contingency against crashing, map non-numeric tokens to zeroes
	 */
	@Test public void parse_maps_text_to_zero() {
		String version = "lorem.ipsum.foo.bar";
		Version expected = new Version(0, 0, 0, 0);
		assertEquals("Failed to map non-numeric text to zero", expected, Version.parse(version));
	}

	/**
	 * If there's a preceding "v" which started getting added by the build script
	 * when I switched to using git tags for version numbering, we need to ignore it
	 * and consider just the number.
	 */
	@Test
	public void parse_ignores_v_before_number() {
		// Bug fix confirmation - formerly detected '0.18.1'
		String version = "v1.18.1";
		Version expected = new Version(1, 18, 1);
		assertEquals("Failed to detect version string with single preceding 'v'", expected, Version.parse(version));
	}

	/**
	 * Make sure that dev builds properly parse the version string too.
	 */
	@Test
	public void parse_handles_RFG_dirty_tags() {
		// RFG-generated version strings look like this when you have uncommitted changes
		String version = "v1.18.1.dirty";
		Version expected = new Version(1, 18, 1);
		assertEquals("Failed to parse simple dirty tag", expected, Version.parse(version));

		// RFG-generated version strings look like this when you have local commits plus uncommitted changes
		version = "v1.18.1-2-abcdef12.dirty";
		assertEquals("Failed to parse complex dirty tag", expected, Version.parse(version));
	}

	/**
	 * Ensures that 'v' handling doesn't break other rules.
	 */
	@Test
	public void parse_handles_other_v_conditions() {
		// two 'v' counts as text, v followed by non-numbers is still text, single v is not ignored between digits
		String version = "vv1.vfoo.1v8";
		Version expected = new Version(0, 0, 1);
		assertEquals("Parsing failed to handle normal logic with 'v' related conditions", expected, Version.parse(version));
	}
}