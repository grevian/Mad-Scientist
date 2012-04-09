import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.newdawn.slick.util.Log;

public class ConfigReader {
	
	private static HashMap<String, Properties> cache = new HashMap<String, Properties>();
	
	public static Properties readConfig(String file) {
		
		if ( cache.containsKey(file) )
			return cache.get(file);
		
		Log.info("ConfigReader attempting to read file: " + file);
		try {
			FileInputStream in = new FileInputStream(file);
			Properties configFile = new Properties();
			configFile.load(in);
			in.close();
			cache.put(file, configFile);
			return configFile;
		} catch (Exception e) {
			return null;
		}
	}

}
