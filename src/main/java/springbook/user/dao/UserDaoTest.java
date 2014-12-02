package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import org.hsqldb.Server;

import springbook.user.domain.User;

public class UserDaoTest {
	/**
	 * 검증용 main 코드
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Server hsqlServer = null;
		try{
		// DB생성
			hsqlServer = initHsqldb();
			
			ConnectionMaker connectionMaker = new DConnectionMaker();
			//ConnectionMaker connectionMaker = new NConnectionMaker();
			
			UserDao dao = new UserDao(connectionMaker);
			
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
