package stockmanagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@SessionScoped()
public class Purchase {

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
		 
		    public void createDbRecord(String symbol, double price, int qty, double amt) {
		        try {
		            //System.out.println("symbol: " + this.symbol + ", price: " + this.price + "\n");
		            //System.out.println("qty: " + this.qty + ", amt: " + this.amt + "\n");

		            Connection conn = Connector.getConnection();
		            Statement statement = conn.createStatement();
		            
		            //get userid
		            Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
		                    .getExternalContext()
		                    .getSessionMap().get("uid"));
		            
		            System.out.println(uid);
		            System.out.println("symbol:" + symbol);
		            System.out.println("price:" + price);
		            System.out.println("qty:" + qty);
		            System.out.println("amt:" + amt);
		            ResultSet rs= statement.executeQuery("select balance from balance where uid='"+uid+"'");
		            if(rs.next())
		            {
		            	Double bal=Double.parseDouble(rs.getString("balance"));
		            	if(bal>=amt)
		            	{
		            		rs=statement.executeQuery("select * from purchase where uid='"+uid+"' and stock_symbol='"+symbol+"'") ;
		            		if(rs.next())
		            		{
		            			Integer qty1=qty+Integer.parseInt(rs.getString("qty"));
		            			Double amt1=amt+Double.parseDouble(rs.getString("amt"));
		            			String activity="buy";
		            			String sql="update purchase set qty='"+qty1+"',amt='"+amt1+"',price='"+price+"' where uid='"+uid+"' and stock_symbol='"+symbol+"'";
		            			statement.executeUpdate(sql);
		            			statement.executeUpdate("INSERT INTO `activity` (`uid`, `stock_symbol`, `qty`, `price`, `amt`, `activity`) "
					                    + "VALUES ('" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"','"+activity +"')");
		            			Double newbal=bal-amt;
					            statement.executeUpdate("update balance set balance='"+newbal+"' where uid='"+uid+"'");
					            statement.close();
					            conn.close();
					            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully repurchased stock",""));
		            		}
		            		else
		            		{
		            			String activity="buy";
			            		statement.executeUpdate("INSERT INTO `purchase` (`id`, `uid`, `stock_symbol`, `qty`, `price`, `amt`) "
					                    + "VALUES (NULL,'" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"')");
			            		statement.executeUpdate("INSERT INTO `activity` (`uid`, `stock_symbol`, `qty`, `price`, `amt`, `activity`) "
					                    + "VALUES ('" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"','"+activity +"')");
					        
					            Double newbal=bal-amt;
					            statement.executeUpdate("update balance set balance='"+newbal+"' where uid='"+uid+"'");
					            statement.close();
					            conn.close();
					            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully purchased stock",""));
		            		}
		            	}
		            	else
		            	{
		            		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Not enough balance to purchase stock"));
		            	}
		            }
		            
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        return ;
		    }


	
	
}
