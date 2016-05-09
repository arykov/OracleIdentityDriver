package com.ryaltech.jdbc;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Transformer that modifies Oracle JDBC driver's acceptsURL and connect methods
 * to add support for connections through {@link OracleIdentityDriver}. This
 * transformer gets configured by the {@link Agent} at runtime This is done to
 * allow products that rely on JDBC driver name to determine DataBase
 * type(Business Object Crystal Reports) to identify Oracle as Oracle.
 * 
 * @author rykov
 *
 */
public class OjdbcClassTransformer implements ClassFileTransformer{
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if(className.equals("oracle/jdbc/driver/OracleDriver")){
			ClassPool cp = ClassPool.getDefault();
			CtClass ctClass = null;
			try{
				ctClass=cp.makeClass(new ByteArrayInputStream(classfileBuffer));
				
				for(CtMethod mthd:ctClass.getMethods()){					
					if(mthd.getName().equals("acceptsURL")){
						mthd.insertBefore("if(com.ryaltech.jdbc.OracleIdentityDriver.supportsURL($1))return true;");
					}
					if(mthd.getName().equals("connect")){
						mthd.insertBefore("if(com.ryaltech.jdbc.OracleIdentityDriver.supportsURL($1))return new com.ryaltech.jdbc.OracleIdentityDriver($0).connect($$);");
					}
				}
				return ctClass.toBytecode();
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}finally{
				try{
					ctClass.detach();
				}catch(Exception ex){}
			}					
			
		}else{
			return classfileBuffer;
		}
	}

	

}
