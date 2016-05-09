package com.ryaltech.jdbc;

import java.lang.instrument.Instrumentation;

/**
 * 
 * java agent that kicks off load time bytecode modification
 * 
 * @see <a
 *      href="https://docs.oracle.com/javase/7/docs/api/java/lang/instrument/package-summary.html">java.lang.instrument
 *      JavaDoc</a>
 * 
 * @author rykov
 *
 */
public class Agent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Agent called");

		try {
			inst.addTransformer(new OjdbcClassTransformer());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}

	}

}
