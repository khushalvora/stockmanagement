package stockmanagement;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import stockmanagement.Connector;
@ManagedBean()
public class Register {

	private String firstname,lastname,email,username,password,address,phonenumber,role,license,commission;


	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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
	
	public String mdfive(String test) 
	{
        try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] messageDigest = md.digest(test.getBytes());
                BigInteger number = new BigInteger(1, messageDigest);
                String hashtext = number.toString(16);
                while (hashtext.length() < 32) 
                {
                        hashtext = "0" + hashtext;
                }
                return hashtext;
        } 
        catch (NoSuchAlgorithmException e) 
        {
                throw new RuntimeException(e);
        }

}

	
	public String register() 
	{
		String s=null;
		
			
			int i=0;
			
			
			try {
				Connection conn=Connector.getConnection();
				
				String sql= "insert into users(firstname,lastname,address,phonenumber,email,username,password,role) values(?,?,?,?,?,?,?,?)";
				
				PreparedStatement st =conn.prepareStatement(sql);
				st.setString(1, this.firstname);
				st.setString(2, this.lastname);
				st.setString(3, this.address);
				st.setString(4, this.phonenumber);
				st.setString(5, this.email);
				st.setString(6, this.username);
				st.setString(7, mdfive(this.password));
				st.setString(8, this.role);
				
				i=st.executeUpdate();
				if(i>0)
				{
					sql = "select uid,role from users where username='"+username+"'";
					st=conn.prepareStatement(sql);
					ResultSet rs= st.executeQuery();
					if(rs.next())
					{
						String role=rs.getString("role");
						if(role.equals("manager"))
						{
							String uid=rs.getString("uid");
							sql="insert into balance(uid,balance) values(?,?)";
							st =conn.prepareStatement(sql);
							st.setInt(1, Integer.parseInt(uid));
							st.setInt(2, 0);
							i=st.executeUpdate();
							sql="insert into manager(uid,approved) values(?,?)";
							st =conn.prepareStatement(sql);
							st.setInt(1, Integer.parseInt(uid));
							st.setInt(2, 0);
							i=st.executeUpdate();
							
						}
						else if(role.equals("user"))
						{
							String uid=rs.getString("uid");
							sql="insert into balance(uid,balance) values(?,?)";
							st =conn.prepareStatement(sql);
							st.setInt(1, Integer.parseInt(uid));
							st.setInt(2, 100000);
							i=st.executeUpdate();
						}
					}
				}
				
				
			}
			
			catch(Exception e){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.toString()));
			}
			if(i>0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registered successfully.. Please login to continue"));
				s="login.xhtml";
				
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("user already exist"));
				s="";
			
			}
		
		
		return s;

		
	}
	
	
}
