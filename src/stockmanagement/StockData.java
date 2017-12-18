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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
@ManagedBean
@SessionScoped
public class StockData {

	  private static final long serialVersionUID = 1L;
	    static final String API_KEY = "AF93E6L5I01EA39O";

	    private String table1Markup;
	    private String table2Markup;

	    private String selectedSymbol;
	    private List<SelectItem> availableSymbols;
	    private String selectedInterval;
	    private List<SelectItem> availableIntervals;

	    


	    @PostConstruct
	    public void init() {
	        //initially populate stock list
	        availableSymbols = new ArrayList<SelectItem>();
	        try 
	        {
		        Connection conn = Connector.getConnection();
		        String sql = "select * from stocks";
		        Statement statement=conn.createStatement();
		        ResultSet rs=statement.executeQuery(sql);
		        while(rs.next()) 
		        {
		        	availableSymbols.add(new SelectItem(rs.getString("stocksymbol"), rs.getString("stockname")));
		        }
	        }
	        catch(Exception e) 
	        {}
	        //initially populate intervals for stock api
	        availableIntervals = new ArrayList<SelectItem>();
	        availableIntervals.add(new SelectItem("1min", "1min"));
	        availableIntervals.add(new SelectItem("5min", "5min"));
	        availableIntervals.add(new SelectItem("15min", "15min"));
	        availableIntervals.add(new SelectItem("30min", "30min"));
	        availableIntervals.add(new SelectItem("60min", "60min"));
	    }

	    

	    public String getSelectedInterval() {
	        return selectedInterval;
	    }

	    public void setSelectedInterval(String selectedInterval) {
	        this.selectedInterval = selectedInterval;
	    }

	    public List<SelectItem> getAvailableIntervals() {
	        return availableIntervals;
	    }

	    public void setAvailableIntervals(List<SelectItem> availableIntervals) {
	        this.availableIntervals = availableIntervals;
	    }

	    public String getSelectedSymbol() {
	        return selectedSymbol;
	    }

	    public void setSelectedSymbol(String selectedSymbol) {
	        this.selectedSymbol = selectedSymbol;
	    }

	    public List<SelectItem> getAvailableSymbols() {
	        return availableSymbols;
	    }

	    public void setAvailableSymbols(List<SelectItem> availableSymbols) {
	        this.availableSymbols = availableSymbols;
	    }

	   

	    public String getTable1Markup() {
	        return table1Markup;
	    }

	    public void setTable1Markup(String table1Markup) {
	        this.table1Markup = table1Markup;
	    }

	    public String getTable2Markup() {
	        return table2Markup;
	    }

	    public void setTable2Markup(String table2Markup) {
	        this.table2Markup = table2Markup;
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

	    public String timeseries() throws MalformedURLException, IOException {

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
	                this.table2Markup = ""; // reset table 2 markup before repopulating
	                JsonObject dataJsonObj = mainJsonObj.getJsonObject(key);
	                this.table2Markup += "<table class='table table-hover'>";
	                this.table2Markup += "<thead><tr><th>Timestamp</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr></thead>";
	                this.table2Markup += "<tbody>";
	                int i = 0;
	          
	                for (String subKey : dataJsonObj.keySet()) 
	                {
	                	if(i<5)
	                	{
		                    JsonObject subJsonObj = dataJsonObj.getJsonObject(subKey);
		                    this.table2Markup += "<tr>";
		                    this.table2Markup += "<td>" + subKey + "</td>";
		                    this.table2Markup += "<td>" + subJsonObj.getString("1. open") + "</td>";
		                    this.table2Markup += "<td>" + subJsonObj.getString("2. high") + "</td>";
		                    this.table2Markup += "<td>" + subJsonObj.getString("3. low") + "</td>";
		                    this.table2Markup += "<td>" + subJsonObj.getString("4. close") + "</td>";
		                    this.table2Markup += "<td>" + subJsonObj.getString("5. volume") + "</td>";
		                    if (i == 0) 
		                    {
		                        String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		                        this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/faces/purchase.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close") + "'>Buy Stock</a></td>";
		                    }
		                    this.table2Markup += "</tr>";
		                    i++;
	                	}
	                }
	                
	                this.table2Markup += "</tbody></table>";
	            }
	        }
	        return "viewstock.xhtml";
	    }
	    
	    public String addw()
	    {
	    	String symbol = this.selectedSymbol;
	    	try 
	    	{
		    	Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("uid"));
		        Connection conn = Connector.getConnection();
		        
		        String sql="select * from watchlist where uid='"+uid+"' and stock_symbol='"+symbol+"'";
		        Statement statement=conn.createStatement();
		        ResultSet rs=statement.executeQuery(sql);
		        if(rs.next())
		        {
		        	statement.close();
		        	conn.close();
		        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Already there in the watchlist"));
		        }
		        else
		        {
		        	
		        	sql = "select stockname from stocks where stocksymbol='"+symbol+"'";
		        	rs=statement.executeQuery(sql);
		        	if(rs.next())
		        	{
		        		String stn=rs.getString("stockname");
			        	sql="insert into watchlist values('"+symbol+"','"+stn+"','"+uid+"')";
			        	statement.executeUpdate(sql);
			        	statement.close();
			        	conn.close();
			        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added to the Watchlist"));
		        	}
		        	
		        }
	    	}
	    	catch(Exception e) {}
	    	return "viewstock.xhtml";
	    	
	    }
}
