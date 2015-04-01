package corning.test.flyway;

import org.flywaydb.core.Flyway;

/**
 * TestFlyway
 *
 */
public class TestFlyway {

	public static void main(String[] args) {

		testSqliteSql2SqliteDb();
		
		testMysqlSql2SqliteDb();

	}

	private static void testSqliteSql2SqliteDb() {
		String sqlType = "mysql";
		
		testMigrate("v1", sqlType);
		testMigrate("v2", sqlType);
		testMigrate("v3", sqlType);
		
	}

	private static void testMysqlSql2SqliteDb() {
		String sqlType = "sqlite";
		
		testMigrate("v1", sqlType);
		testMigrate("v2", sqlType);
		testMigrate("v3", sqlType);

	}

	protected static void testMigrate(String version, String sqlType) {
		Boolean result = false;
		try {
			Flyway flyway = new Flyway();
			
			// 设置数据库
			String resource = TestFlyway.class.getClassLoader().getResource("").getPath();
			String dbFile = resource + "database/" + sqlType + "/" + version + ".db";
			flyway.setDataSource("jdbc:sqlite:" + dbFile, null, null);
	
			// 设置sql脚本目录
			String locations = "db/migration/" + sqlType + "/" + version;
			flyway.setLocations(locations);
			
			// 成功数不大于0的说明出错了
			result = flyway.migrate() > 0;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println(String.format("脚本=%s,版本=%s,结果=%b", sqlType, version, result));
	}
	
}
