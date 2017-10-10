package mx.nic.rdap.server.shiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.JdbcUtils;

/**
 * Based on https://mehmetceliksoy.wordpress.com/2015/06/28/shiro-jdbc-realm/
 * 
 * @author pcarana
 *
 */
public class CustomSecurityRealm extends JdbcRealm {

	/**
	 * The default query used to retrieve account data for the user.
	 */
	protected static final String DEFAULT_AUTHENTICATION_QUERY = "SELECT rus_pass FROM rdap_user WHERE rus_name = ?";

	/**
	 * The default query used to retrieve the roles that apply to a user.
	 */
	protected static final String DEFAULT_USER_ROLES_QUERY = "SELECT rur_name FROM rdap_user_role WHERE rus_name = ?";

	protected String authenticationQuery = DEFAULT_AUTHENTICATION_QUERY;

	protected String userRolesQuery = DEFAULT_USER_ROLES_QUERY;

	protected boolean permissionsLookupEnabled = false;	

	private static final Logger logger = Logger.getLogger(CustomSecurityRealm.class.getName());

	protected String dataSourceName;

	public CustomSecurityRealm() {
		super();
	}

//	FIXME Commented, is loaded from shiro.ini
//	public String getDataSourceName() {
//		return dataSourceName;
//	}
//
//	public void setDataSourceName(String dataSourceName) {
//		this.dataSourceName = dataSourceName;
//		this.dataSource = getDataSourceFromJDBC(dataSourceName);
//	}
//
//	private DataSource getDataSourceFromJDBC(String dataSourceName) {
//		try {
//			InitialContext ic = new InitialContext();
//			return (DataSource) ic.lookup(dataSourceName);
//		} catch (NamingException e) {
//			log.error("JDBCr while retrieving " + dataSourceName, e);
//			throw new AuthorizationException(e);
//		}
//	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		// Null username is invalid
		if (username == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}

		Connection conn = null;
		AuthenticationInfo info = null;
		try {
			conn = dataSource.getConnection();

			String password = getPasswordForUser(conn, username);
			if (password == null) {
				throw new UnknownAccountException("No account found for user [" + username + "]");
			}

			info = new SimpleAuthenticationInfo(username, password.toCharArray(), getName());
		} catch (SQLException e) {
			final String message = "There was a SQL error while authenticating user [" + username + "]";
			logger.log(Level.SEVERE, message, e);

			// Rethrow any SQL errors as an authentication exception
			throw new AuthenticationException(message, e);
		} finally {
			JdbcUtils.closeConnection(conn);
		}

		return info;
	}	

	private String getPasswordForUser(Connection conn, String username) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String password = null;
		try {
			ps = conn.prepareStatement(authenticationQuery);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();

			// Loop over results - although we are only expecting one result,
			// since usernames should be unique
			boolean foundResult = false;
			while (rs.next()) {
				// Check to ensure only one row is processed
				if (foundResult) {
					throw new AuthenticationException(
							"More than one user row found for user ["
									+ username + "]. Usernames must be unique.");
				}
				password = rs.getString(1);
				foundResult = true;
			}
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
		}
		return password;
	}

	protected Set<String> getRoleNamesForUser(Connection conn, String username) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Set<String> roleNames = new LinkedHashSet<String>();

		try {
			ps = conn.prepareStatement(userRolesQuery);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();
			while (rs.next()) {
				// Add the role to the list of names if it isn't null
				String roleName = rs.getString(1);
				if (roleName != null) {
					roleNames.add(roleName);
				}
			}
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
		}

		return roleNames;
	}

}