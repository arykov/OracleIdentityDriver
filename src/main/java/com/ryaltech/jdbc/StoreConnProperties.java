package com.ryaltech.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.beust.jcommander.JCommander;

/**
 * 
 * Utility to create property file with DB connection information for
 * {@link OracleIdentityDriver} use
 * 
 * @author rykov
 *
 */
public class StoreConnProperties {
	private static final String keyFileName = "sys.dat";
	private static final SecureRandom rng = new SecureRandom();
	private String propertiesFileLocation;
	private StandardPBEStringEncryptor encryptor;

	public StoreConnProperties(String propertiesFileLocation) {
		try {
			this.propertiesFileLocation = propertiesFileLocation;
			// consider encryption algorithm that is better than default
			encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(loadPassword(propertiesFileLocation));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Generate random Ascii String with codes from 32 to 126
	 * 
	 * @param length
	 * @return
	 */
	private final String generateCharArray(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append((char) (rng.nextInt(94) + 32));
		}
		return sb.toString();
	}

	/**
	 * Read file fully to a String
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private final String readFileFully(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		try {
			byte[] buffer = new byte[(int) f.length()];
			fis.read(buffer);
			return new String(buffer);
		} finally {
			try {
				fis.close();
			} catch (Exception ex) {
			}
		}

	}

	/**
	 * Save String to file
	 * 
	 * @param f
	 * @param data
	 * @throws Exception
	 */
	private final void writeToFile(File f, String data) throws Exception {
		FileOutputStream fos = new FileOutputStream(f);
		try {
			fos.write(data.getBytes());
		} finally {
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Load password from directory(or file parent directory) and generate that
	 * password and store it if needed
	 * 
	 * @param directory
	 * @return
	 * @throws Exception
	 */
	private final String loadPassword(String directory) throws Exception {
		File f = new File(directory);
		File dir = f.isDirectory() ? f : f.getParentFile();
		if (dir != null && dir.exists() && dir.isDirectory()) {

			File keyFile = new File(dir, keyFileName);
			if (!keyFile.exists()) {
				writeToFile(keyFile, generateCharArray(64));

				// replace with Files.setPosixFilePermissions once Java 6
				// support is no longer required
				try{
					Process p = Runtime.getRuntime().exec(
						"chmod 400 " + keyFile.getAbsolutePath());
					p.waitFor();
					if (p.exitValue() != 0) {
						throw new RuntimeException("Failed to make the file readable only by owner. Please, make sure it is done manually.");
					}
				}catch(RuntimeException rex){
					throw rex;
				}catch(Exception ex){
					throw new RuntimeException("Failed to make the file readable only by owner. Please, make sure it is done manually.", ex);
				}
			}
			return readFileFully(keyFile);
		}
		throw new RuntimeException(String.format("%s does not exist.", dir));
	}

	/**
	 * Save db connection information to the properties file
	 * 
	 * @param ci
	 * @throws Exception
	 */
	void saveJdbcConnInfo(final JdbcConnInfo ci) throws Exception {
		Properties props = new Properties();
		props.put("user", ci.user);
		props.put("url", ci.url);
		props.put("passEncrypted", encryptor.encrypt(ci.password));
		FileOutputStream fos = new FileOutputStream(propertiesFileLocation);
		try {
			props.store(fos, "");
		} finally {
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}

	}

	/**
	 * Load jdbc connection information
	 * 
	 * @return
	 * @throws Exception
	 */
	JdbcConnInfo loadJdbcConnInfo() {
		try {
			final Properties props = new Properties();
			FileInputStream fis = new FileInputStream(propertiesFileLocation);
			try {
				props.load(fis);
			} finally {
				try {
					fis.close();
				} catch (Exception ex) {
				}
			}
			return new JdbcConnInfo() {
				{
					user = props.getProperty("user");
					url = props.getProperty("url");
					password = encryptor.decrypt(props
							.getProperty("passEncrypted"));
				}
			};
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	public static void main(String[] args) throws Exception {
		Parameters params = new Parameters();
		JCommander jc = new JCommander(params);
		try {
			jc.parse(args);
		} catch (Exception ex) {
			// due to intelligible nature of jcommander parse messages
			System.out.println(ex.getMessage());
			params.help = true;
		}
		if (params.help) {
			jc.usage();
			System.out.println("For example: java -classpath OracleIdentityDriver.jar com.ryaltech.jdbc.ConnInfoStore -url jdbc:oracle:thin:@localhost:1521:XE -user SCOTT  -s /opt/data/connection.properties -password");
			System.out.println("You will be prompted for password");
			return;
		}
		new StoreConnProperties(params.stringpropertiesFileLocation)
				.saveJdbcConnInfo(params);
	}

}
