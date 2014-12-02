package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import org.hsqldb.Server;

import springbook.user.domain.User;

public class UserDao {
	private ConnectionMaker connectionMaker;
	
	public UserDao() {
		connectionMaker = new DConnectionMaker();	// !!!!!!!!!
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"insert into users(id, name, password) values (?, ?, ?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException{
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
	//public abstract Connection getConnection()  throws ClassNotFoundException, SQLException;
	
	/**
	 * 검증용 main 코드
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Server hsqlServer = null;
		try{
		// DB생성
		hsqlServer = initHsqldb();
		
		UserDao dao = new UserDao();
		
		User user = new User();
		user.setId("whiteship");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + " 조회 성공");
		}finally{
			if(null != hsqlServer)
				hsqlServer.stop();
		}
	}
	
	public static Server initHsqldb() throws ClassNotFoundException, SQLException{
        // stub to get in/out of embedded db
        Server hsqlServer = null;
        Connection connection = null;
        
        hsqlServer = new Server();
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, "test");
        hsqlServer.setDatabasePath(0, "file:target/test");
        hsqlServer.start();
        

        Class.forName("org.hsqldb.jdbcDriver");
        try{
        connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test", "sa", ""); // can through sql exception
        connection.prepareStatement(
        		"create table users(" +
				"	id varchar(10) not null " +
				"	,name varchar(20) not null " +
				"	,password varchar(10) not null " +
				"	,primary key (id) " +
				")").execute();
        }catch(SQLSyntaxErrorException e){
        	System.out.println("중복호출로 인한 오류 " + e);
        }
        
        return hsqlServer;
	}
}

class NUserDao extends UserDao {
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		// N사 DB connection 생성코드
		Class.forName("org.hsqldb.jdbcDriver");
		return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test", "sa", "");
	}
}

class DUserDao extends UserDao {
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		// N사 DB connection 생성코드
		return null;
	}
}
