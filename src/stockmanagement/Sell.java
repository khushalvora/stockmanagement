package stockmanagement;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@SessionScoped()
public class Sell {

	static final String API_KEY = "AF93E6L5I01EA39O";
	private String selldata = "hi there";
	private String symbol;
    private double price;
    private int qty;
    private double amt;
    public String getSymbol() {
    	if (getRequestParameter("symbol") != null) {
            symbol = getRequestParameter("symbol");
        }
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getPrice() {
		if (getRequestParameter("price") != null) {
            price = Double.parseDouble(getRequestParameter("price"));
        }
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getAmt() {
		return amt;
	}
	public void setAmt(double amt) {
		this.amt = amt;
	}
	 private String getRequestParameter(String name) {
	        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
	    }

	public String getSelldata() {
		populateselldata();
		return selldata;
	}

	public void setSelldata(String selldata) {
		this.selldata = selldata;
	}
	private void populateselldata()
	{
		try 
        {
		 Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
	        Connection conn = Connector.getConnection();
	        String sql = "select * from purchase where uid='"+uid+"'";
	        Statement statement=conn.createStatement();
	        ResultSet rs=statement.executeQuery(sql);
	        this.selldata="";
        	this.selldata += "<table class='table table-hover'>";
        	this.selldata +="<tr><th>Stock Symbol</th><th>Quantity</th><th>Last Refresh Time</th><th>Price</th><th>Activity</th></tr>";
        	while(rs.next())
	        {
	        	this.selldata += "<tr><td>" + rs.getString("stock_symbol") + "</td>";
                this.selldata += "<td>" + rs.getString("qty") + "</td>";
                String symbol=rs.getString("stock_symbol");
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
			                    this.selldata += "<td>" + subKey + "</td>";
			                    this.selldata += "<td>" + subJsonObj.getString("4. close") + "</td>";
			                    String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			                        this.selldata += "<td><a class='btn btn-success' href='" + path + "/faces/sellstock.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close")  + "'>Sell</a></td>";
			                    
			                    this.selldata += "</tr>";
			                    i++;
		                	}
		                }
	              
		            }
		        }
                
	        }
	        this.selldata += "</table>";
        }
		catch(Exception e)
		{}
	}

	public void createDbRecord(String symbol, double price, int qty, double amt) {
		try 
		{
			Connection conn = Connector.getConnection();
            Statement statement = conn.createStatement();
            
            //get userid
            Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
            
            ResultSet rs= statement.executeQuery("select * from purchase where uid='"+uid+"' and stock_symbol='"+symbol+"'");
            if(rs.next())
            {
            	int qty1=Integer.parseInt(rs.getString("qty"));
            	if(qty1>=qty)
            	{
            		Integer qty2=Integer.parseInt(rs.getString("qty"))-qty;
            		Double amt2=Double.parseDouble(rs.getString("amt"))-amt;
            		String sql="update purchase set qty='"+qty2+"',amt='"+amt2+"',price='"+price+"' where uid='"+uid+"' and stock_symbol='"+symbol+"'";
        			statement.executeUpdate(sql);
        			String activity="sell";
        			statement.executeUpdate(sql);
        			statement.executeUpdate("INSERT INTO `activity` (`uid`, `stock_symbol`, `qty`, `price`, `amt`, `activity`) "
		                    + "VALUES ('" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"','"+activity +"')");
        			rs= statement.executeQuery("select balance from balance where uid='"+uid+"'");
        			if(rs.next())
        			{
        				Double bal=Double.parseDouble(rs.getString("balance"));
        				Double newbal=bal+amt;
    		            statement.executeUpdate("update balance set balance='"+newbal+"' where uid='"+uid+"'");
    		            statement.close();
    		            conn.close();
    		            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully sold stock",""));
        			}
        			
            	}
            	else
            	{
	            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("you do Not have enough quantity to sell stock"));	
            	}
            }
			
			
		}
		catch(Exception e){}
	}
}
