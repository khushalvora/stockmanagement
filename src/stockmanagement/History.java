package stockmanagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped()
public class History {

	private String historydata = "hi there";
	private String historydata1 = " hi there";
	public String getHistorydata1() {
		populatehistorydata1();
		return historydata1;
	}

	public void setHistorydata1(String historydata1) {
		this.historydata1 = historydata1;
	}

	private String fulldata = "hi there";

	public String getFulldata() {
		populatefulldata();
		return fulldata;
	}

	public void setFulldata(String fulldata) {
		this.fulldata = fulldata;
	}

	public String getHistorydata() {
		populatehistorydata();
		return historydata;
	}

	public void setHistorydata(String historydata) {
		this.historydata = historydata;
	}
	
	private void populatehistorydata()
	{
		 try 
	        {
			 Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
	                    .getExternalContext()
	                    .getSessionMap().get("uid"));
		        Connection conn = Connector.getConnection();
		        String sql = "select * from activity where uid='"+uid+"'";
		        Statement statement=conn.createStatement();
		        ResultSet rs=statement.executeQuery(sql);
		        this.historydata="";
	        	this.historydata += "<table class='table table-hover'>";
	        	this.historydata +="<tr><th>Stock Symbol</th><th>Quantity</th><th>Price</th><th>Amount</th><th>Activity</th></tr>";
		        while(rs.next())
		        {
		        	this.historydata += "<tr><td>" + rs.getString("stock_symbol") + "</td>";
	                this.historydata += "<td>" + rs.getString("qty") + "</td>";
	                this.historydata += "<td>" + rs.getString("price") + "</td>";
	                this.historydata += "<td>" + rs.getString("amt") + "</td>";
	                this.historydata += "<td>" + rs.getString("activity") + "</td></tr>";
		        }
		        this.historydata += "</table>";
		        System.out.println(historydata);
	        }
		 	
		 	catch(Exception e) {}
		 	
	}
	
	private void populatefulldata()
	{
		try 
        {
		 Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
	        Connection conn = Connector.getConnection();
	        String sql = "select * from activity";
	        Statement statement=conn.createStatement();
	        ResultSet rs=statement.executeQuery(sql);
	        this.fulldata="";
        	this.fulldata += "<table class='table table-hover'>";
        	this.fulldata +="<tr><th>Stock Symbol</th><th>Quantity</th><th>Price</th><th>Amount</th><th>Activity</th></tr>";
	        while(rs.next())
	        {
	        	this.fulldata += "<tr><td>" + rs.getString("stock_symbol") + "</td>";
                this.fulldata += "<td>" + rs.getString("qty") + "</td>";
                this.fulldata += "<td>" + rs.getString("price") + "</td>";
                this.fulldata += "<td>" + rs.getString("amt") + "</td>";
                this.fulldata += "<td>" + rs.getString("activity") + "</td></tr>";
	        }
	        this.fulldata += "</table>";
	        System.out.println(historydata);
        }
	 	
	 	catch(Exception e) {}
		
	}
	
	private void populatehistorydata1()
	{
		try
		{
			historydata1="";
			Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
	                .getExternalContext()
	                .getSessionMap().get("uid"));
	        Connection conn = Connector.getConnection();
	        String sql = "select balance from balance where uid='"+uid+"'";
	        Statement statement=conn.createStatement();
	        ResultSet rs=statement.executeQuery(sql);
	        if(rs.next())
	        {
	        	String Balance = rs.getString("balance");
	        	historydata1+="Your Balance is "+Balance ;
	        }
	        
		}
		catch(Exception e)
		{}
	}
}
