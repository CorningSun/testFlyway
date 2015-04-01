package corning.test.migration;

import org.flywaydb.core.Flyway;

/**
 * TestFlyway
 *
 */
public class TestFlyway {
	
	public static void main(String[] args) {
		// Create the Flyway instance
		Flyway flyway = new Flyway();

		// Point it to the database
		flyway.setDataSource("jdbc:sqlite:H:\\Study\\Flyway\\flyway1.db", null, null);
		
		// Start the migration
		flyway.migrate();
		
//		MigrationInfoService info = flyway.info();
//		
//		for (MigrationInfo migrationInfo : info.all()) {
//			System.out.println(migrationInfo.getVersion());
//		}
		
	}
}
