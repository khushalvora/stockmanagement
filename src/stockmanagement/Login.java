package stockmanagement;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;

import stockmanagement.Connector;
@ManagedBean()
@SessionScoped()
public class Login {

	private String username;
	private String password;
	private String role;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String mdfive(String test) {
        try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] messageDigest = md.digest(test.getBytes());
                BigInteger number = new BigInteger(1, messageDigest);
                String hashtext = number.toString(16);
                while (hashtext.length() < 32) {
                        hashtext = "0" + hashtext;
                }
                return hashtext;
        } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
        }

}


	public String login()
	{
		String s="login.xhtml";
		if(role.equals("user"))
		{
		
			try {
				Connection conn=Connector.getConnection();
				System.out.println("Connected Successfully");
				
				String sql = "SELECT uid,firstname,password,role from users where username = ?";
				PreparedStatement st = conn.prepareStatement(sql);
				st.setString(1, this.username);
				
				ResultSet rs = st.executeQuery();
	
				if (rs.next()) 
				{
					String uid = rs.getString("uid");
					String pass = rs.getString("password");
					String firstname=rs.getString("firstname");
					String role=rs.getString("role");
					if(pass.equals(mdfive(password)))
					{
						if(role.equals("user"))
						{
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("firstname",firstname);
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uid",uid);
							s="success.xhtml";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" You have Selected Wrong Role"));
							s="";
						}
						
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" Incorrect username and password"));
						s="";
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User doesn't exist"));
					s="";
				}
					
			}
			catch(Exception e)
			{
				
			}
		}
		else if(role.equals("manager"))
		{
			try {
				Connection conn=Connector.getConnection();
				System.out.println("Connected Successfully");
				
				String sql = "SELECT uid,firstname,password,role from users where username = ?";
				PreparedStatement st = conn.prepareStatement(sql);
				st.setString(1, this.username);
				
				ResultSet rs = st.executeQuery();
	
				if (rs.next()) 
				{
					String uid = rs.getString("uid");
					String pass = rs.getString("password");
					String firstname=rs.getString("firstname");
					String role=rs.getString("role");
					if(pass.equals(mdfive(password)))
					{
						if(role.equals("manager"))
						{
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("firstname",firstname);
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uid",uid);
							s="managerpanel.xhtml";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" You have selected Wrong role"));
							s="";
						}
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" Incorrect username and password"));
						s="";
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User doesn't exist"));
					s="";
				}
					
			}
			catch(Exception e)
			{
				
			}					
		}
		
		
		else
		{
		
			try {
				Connection conn=Connector.getConnection();
				System.out.println("Connected Successfully");
				
				String sql = "SELECT uid,firstname,password,role from users where username = ?";
				PreparedStatement st = conn.prepareStatement(sql);
				st.setString(1, this.username);
				
				ResultSet rs = st.executeQuery();
	
				if (rs.next()) 
				{
					String uid = rs.getString("uid");
					System.out.println(uid);
					String pass = rs.getString("password");
					String firstname=rs.getString("firstname");
					String role=rs.getString("role");
					if(pass.equals(mdfive(password)))
					{
						if(role.equals("admin"))
						{
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("firstname",firstname);
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uid",uid);
							s="admindashbord.xhtml";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" You have Selected Wrong Role"));
							s="";
						}
						
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" Incorrect username and password"));
						s="";
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User doesn't exist"));
					s="";
				}
					
			}
			catch(Exception e)
			{
				
			}
		}
		
		
		System.out.println(s);
		
		return s;
				
	}
	
	public String logout()
	{
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "login.xhtml";
	}
	
}
