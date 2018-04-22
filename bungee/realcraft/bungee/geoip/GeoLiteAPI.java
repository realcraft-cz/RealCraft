package realcraft.bungee.geoip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import net.md_5.bungee.BungeeCord;
import realcraft.bungee.RealCraftBungee;

public class GeoLiteAPI {

    private static final String GEOIP_URL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
    private static LookupService lookupService;

    /**
     * Download (if absent) the GeoIpLite data file and then try to load it.
     *
     * @return True if the data is available, false otherwise.
     */
    public static boolean isDataAvailable(RealCraftBungee plugin){
        if (lookupService != null) {
            return true;
        }
        final File data = new File(plugin.getDataFolder(), "GeoIP.dat");
        if (data.exists()) {
            try {
                lookupService = new LookupService(data);
                return true;
            } catch (IOException e) {
                // TODO ljacqu 20151123: Log the exception instead of just swallowing it
                return false;
            }
        }
        // Ok, let's try to download the data file!
        BungeeCord.getInstance().getScheduler().runAsync(plugin,new Runnable(){
            @Override
            public void run() {
                try {
                    URL downloadUrl = new URL(GEOIP_URL);
                    URLConnection conn = downloadUrl.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    if (conn.getURL().toString().endsWith(".gz")) {
                        input = new GZIPInputStream(input);
                    }
                    OutputStream output = new FileOutputStream(data);
                    byte[] buffer = new byte[2048];
                    int length = input.read(buffer);
                    while (length >= 0) {
                        output.write(buffer, 0, length);
                        length = input.read(buffer);
                    }
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return false;
    }

    /**
     * Get the country code of the given IP address.
     *
     * @param ip Ip address
     *
     * @return String
     */
    public static String getCountryCode(RealCraftBungee plugin,String ip){
        if(isDataAvailable(plugin)){
            return lookupService.getCountry(ip).getCode();
        }
        return "--";
    }

    /**
     * Get the country name of the given IP address.
     *
     * @param ip Ip address
     *
     * @return String
     */
    public static String getCountryName(RealCraftBungee plugin,String ip){
        if(isDataAvailable(plugin)){
            return lookupService.getCountry(ip).getName();
        }
        return "N/A";
    }

    public static boolean isCountryBlocked(InetSocketAddress inetAddress){
		String address = inetAddress.getAddress().getHostAddress().replace("/","");
		if(address.length() > 0){
			String country = GeoLiteAPI.getCountryCode(RealCraftBungee.getInstance(),address);
			if(country.equalsIgnoreCase("CZ") || country.equalsIgnoreCase("SK")) return false;
		}
		return true;
	}

}