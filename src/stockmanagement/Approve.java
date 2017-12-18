package stockmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@SessionScoped()
public class Approve {

	private String toapprove;
	public String getToapprove() {
		if (getRequestParameter("uid") != null) {
			toapprove = getRequestParameter("uid");
        }
		return toapprove;
	}
	public void setToapprove(String toapprove) {
		this.toapprove = toapprove;
	}
	private String getRequestParameter(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }

	private String table2Markup;
	public String getTable2Markup() {
		populate();
		return table2Markup;
	}
	public void setTable2Markup(String table2Markup) {
		this.table2Markup = table2Markup;
	}

	private String license;
	private String commission;
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	
	public String sendr()
	{
		Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("uid"));
		 	try 
		 	{
		 		String sql="select approved from manager where uid='"+uid+"'";
		 		Connection conn = Connector.getConnection();
		 		PreparedStatement st =conn.prepareStatement(sql);
		 		ResultSet rs=st.executeQuery();
		 		if(rs.next())
		 		{
		 			if(rs.getString("approved").equals("1"))
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You are already Approved"));
					}
					else
					{
						sql="update manager set license=?,commission=? where uid=?";
				        st =conn.prepareStatement(sql);
						st.setString(1, license);
						st.setInt(2, Integer.parseInt(commission));
						st.setInt(3, uid);
						int i=st.executeUpdate();
						if(i>0)
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request sent to Admin"));
						}

					}
		 		}
		 		
		        
		 	}
		 	catch(Exception e) {
		 		System.out.println(e);
		 	}
						
		return "getapprove.xhtml";
	}
	
	private void populate()
	{
			try 
			{
				
		        Connection conn = Connector.getConnection();
		        String sql = "select * from manager where approved=0";
		        Statement statement=conn.createStatement();
		        this.table2Markup="";
    	    	this.table2Markup += "<table class='table table-hover'>";
    	    	this.table2Markup +="<tr><th>Manager Name</th><th>License</th><th>Commission</th><th>Action</th></tr>";
    	    	ResultSet rs=statement.executeQuery(sql);
    	    	while(rs.next())
    	    	{
    	    		String lic=rs.getString("license"); 
    	    		String comm=rs.getString("commission");
    	    		String uid1=rs.getString("uid");
    	    		ResultSet rs1=statement.executeQuery("select firstname from users where uid='"+uid1+"'");
    	    		if(rs1.next())
    	    		{
    	    			this.table2Markup += "<tr><td>" + rs1.getString("firstname") + "</td>";
    	    		}
    	    		this.table2Markup += "<td>" + lic + "</td>";
    	    		this.table2Markup += "<td>" + comm + "</td>";
    	    		String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
                    this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/faces/approve.xhtml?uid=" + uid1 + "'>Approve</a></td>";
                    this.table2Markup += "</tr>";
    	    	}
    	    	this.table2Markup += "</table>";
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		 	
	}
	
	public String approve()
	{
		try 
		{
			String sql="update manager set approved=1 where uid='"+toapprove+"'";
	 		Connection conn = Connector.getConnection();
	 		PreparedStatement st =conn.prepareStatement(sql);
	 		int i=st.executeUpdate();
			if(i>0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Approved"));
			}

		}
		catch(Exception e) {}
	return "approve.xhtml";	
	}
}
