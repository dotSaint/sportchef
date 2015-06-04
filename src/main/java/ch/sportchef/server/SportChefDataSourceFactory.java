package ch.sportchef.server;

import io.dropwizard.db.DataSourceFactory;

public class SportChefDataSourceFactory extends DataSourceFactory {

    private boolean migrateOnStart = false;
    private boolean dropAllBeforeMigration = false;

    public boolean isMigrateOnStart() {
        return migrateOnStart;
    }

    public boolean isDropAllBeforeMigration() {
        return dropAllBeforeMigration;
    }
}
