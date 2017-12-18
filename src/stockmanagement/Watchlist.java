package stockmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@SessionScoped()
public class Watchlist {

	static final String API_KEY = "AF93E6L5I01EA39O";
	private String selectedSymbol;
	private String count;
    private List<SelectItem> availableSymbols;
    private String selectedInterval;
    private List<SelectItem> availableIntervals;
    private String table2Markup="hey";
    private String table1Markup;
    private String table3Markup;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getTable1Markup() {
		return table1Markup;
	}
	public void setTable1Markup(String table1Markup) {
		this.table1Markup = table1Markup;
	}
	public String getTable3Markup() {
		return table3Markup;
	}
	public void setTable3Markup(String table3Markup) {
		this.table3Markup = table3Markup;
	}
	public String getTable2Markup() {
		try {
			timeseries();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table2Markup;
	}
	public void setTable2Markup(String table2Markup) {
		this.table2Markup = table2Markup;
	}
	public String getSelectedSymbol() {
		if (getRequestParameter("symbol") != null) {
			selectedSymbol = getRequestParameter("symbol");
        }
		return selectedSymbol;
	}
	private String getRequestParameter(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }
	public void setSelectedSymbol(String selectedSymbol) {
		this.selectedSymbol = selectedSymbol;
	}
	public List<SelectItem> getAvailableSymbols() {
		populatesymbols();
		return availableSymbols;
	}
	public void setAvailableSymbols(List<SelectItem> availableSymbols) {
		this.availableSymbols = availableSymbols;
	}
	public String getSelectedInterval() {
		return selectedInterval;
	}
	public void setSelectedInterval(String selectedInterval) {
		this.selectedInterval = selectedInterval;
	}
	public List<SelectItem> getAvailableIntervals() {
		populateintervals();
		return availableIntervals;
	}
	public void setAvailableIntervals(List<SelectItem> availableIntervals) {
		this.availableIntervals = availableIntervals;
	}
	
	private void populateintervals()
	{
		availableIntervals = new ArrayList<SelectItem>();
        availableIntervals.add(new SelectItem("1min", "1min"));
        availableIntervals.add(new SelectItem("5min", "5min"));
        availableIntervals.add(new SelectItem("15min", "15min"));
        availableIntervals.add(new SelectItem("30min", "30min"));
        availableIntervals.add(new SelectItem("60min", "60min"));
	}
	
	
	private void populatesymbols()
	{
		availableSymbols = new ArrayList<SelectItem>();
        try 
        {
        	Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
	        Connection conn = Connector.getConnection();
	        String sql = "select * from watchlist where uid='"+uid+"'";
	        Statement statement=conn.createStatement();
	        ResultSet rs=statement.executeQuery(sql);
	        while(rs.next()) 
	        {
	        	availableSymbols.add(new SelectItem(rs.getString("stock_symbol"), rs.getString("stock_name")));
	        }
        }
        catch(Exception e) 
        {}
	}
	
	public void installAllTrustingManager() {
        TrustManager[] trustAllCerts;
        trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Exception :" + e);
        }
        return;
    }

    private void timeseries() throws MalformedURLException, IOException {
    	
    	try 
    	{
    	      installAllTrustingManager();
    	      	System.out.println("inside timeseries");
    	        Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("uid"));
    	        Connection conn = Connector.getConnection();
    	        String sql = "select * from watchlist where uid='"+uid+"'";
    	        Statement statement=conn.createStatement();
    	        ResultSet rs=statement.executeQuery(sql);
    	        this.table2Markup="";
    	    	this.table2Markup += "<table class='table table-hover'>";
    	    	this.table2Markup +="<tr><th>Stock Name</th><th>Last refreshed</th><th>Price</th><th>Action</th></tr>";
    	        while(rs.next())
    	        {
    	        	String symbol = rs.getString("stock_symbol");
    	        	this.table2Markup += "<tr><td>" + rs.getString("stock_name") + "</td>";
    		        String interval = "1min";
    		        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=" + interval + "&apikey=" + API_KEY;
    		
    		        
    		        InputStream inputStream = new URL(url).openStream();
    		
    		        // convert the json string back to object
    		        JsonReader jsonReader = Json.createReader(inputStream);
    		        JsonObject mainJsonObj = jsonReader.readObject();
    		        for (String key : mainJsonObj.keySet()) {
    		            if (key.equals("Meta Data")) {
    		              
    		            } else {
    		                
    		                JsonObject dataJsonObj = mainJsonObj.getJsonObject(key);
    		                int i = 0;
    		          
    		                for (String subKey : dataJsonObj.keySet()) 
    		                {
    		                	if(i<1)
    		                	{
    			                    JsonObject subJsonObj = dataJsonObj.getJsonObject(subKey);
    			                    this.table2Markup += "<td>" + subKey + "</td>";
    			                    this.table2Markup += "<td>" + subJsonObj.getString("4. close") + "</td>";
    			                    String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    			                        this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/faces/details.xhtml?symbol=" + symbol + "'>View History</a></td>";
    			                    
    			                    this.table2Markup += "</tr>";
    			                    i++;
    		                	}
    		                }
    	              
    		            }
    		        }
    	        }
    	        this.table2Markup += "</table>";
    	        
    	}
    	
    	catch(Exception e) {}
	        
    }

    public String timeseries1() throws MalformedURLException, IOException {

        installAllTrustingManager();

        //System.out.println("selectedItem: " + this.selectedSymbol);
        //System.out.println("selectedInterval: " + this.selectedInterval);
        String symbol = this.selectedSymbol;
        String interval = this.selectedInterval;
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=" + interval + "&apikey=" + API_KEY;

        this.table1Markup += "URL::: <a href='" + url + "'>Data Link</a><br>";
        InputStream inputStream = new URL(url).openStream();

        // convert the json string back to object
        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject mainJsonObj = jsonReader.readObject();
        for (String key : mainJsonObj.keySet()) {
            if (key.equals("Meta Data")) {
                this.table1Markup = ""; // reset table 1 markup before repopulating
                JsonObject jsob = (JsonObject) mainJsonObj.get(key);
                this.table1Markup += "<style>#detail >tbody > tr > td{ text-align:center;}</style><b>Stock Details</b>:<br>";
                this.table1Markup += "<table class='table'>";
                this.table1Markup += "<tr><th>Information</th><th>Symbol</th><th>Last Refreshed</th><th>Interval</th><th>Output Size</th><th>Timezone</th></tr>";
                this.table1Markup += "<tr><td>" + jsob.getString("1. Information") + "</td>";
                this.table1Markup += "<td>" + jsob.getString("2. Symbol") + "</td>";
                this.table1Markup += "<td>" + jsob.getString("3. Last Refreshed") + "</td>";
                this.table1Markup += "<td>" + jsob.getString("4. Interval") + "</td>";
                this.table1Markup += "<td>" + jsob.getString("5. Output Size") + "</td>";
                this.table1Markup += "<td>" + jsob.getString("6. Time Zone") + "</td></tr>";
                this.table1Markup += "</table>";
            } else {
                this.table3Markup = ""; // reset table 2 markup before repopulating
                JsonObject dataJsonObj = mainJsonObj.getJsonObject(key);
                this.table3Markup += "<table class='table table-hover'>";
                this.table3Markup += "<thead><tr><th>Timestamp</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr></thead>";
                this.table3Markup += "<tbody>";
                int i = 0;
          
                for (String subKey : dataJsonObj.keySet()) 
                {
                	if(i<Integer.parseInt(count))
                	{
	                    JsonObject subJsonObj = dataJsonObj.getJsonObject(subKey);
	                    this.table3Markup += "<tr>";
	                    this.table3Markup += "<td>" + subKey + "</td>";
	                    this.table3Markup += "<td>" + subJsonObj.getString("1. open") + "</td>";
	                    this.table3Markup += "<td>" + subJsonObj.getString("2. high") + "</td>";
	                    this.table3Markup += "<td>" + subJsonObj.getString("3. low") + "</td>";
	                    this.table3Markup += "<td>" + subJsonObj.getString("4. close") + "</td>";
	                    this.table3Markup += "<td>" + subJsonObj.getString("5. volume") + "</td>";
	                    if (i == 0) 
	                    {
	                        String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	                        this.table3Markup += "<td><a class='btn btn-success' href='" + path + "/faces/purchase.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close") + "'>Buy Stock</a></td>";
	                    }
	                    this.table3Markup += "</tr>";
	                    i++;
                	}
                }
                
                this.table3Markup += "</tbody></table>";
            }
        }
        return "details.xhtml";
    }

}
