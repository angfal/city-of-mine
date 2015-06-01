package edu.course.city.service.impl;

import edu.course.city.service.DatabaseService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("databaseService")
public class DatabaseServiceImpl implements DatabaseService {

    private static final String DATABASE_URL_PATTERN = "[\\w\\:]*//([\\w\\.]*):([\\d]*)/(.*)\\?.*";

    private String databaseHost;

    private String databasePort;

    private String databaseUserName;

    private String databasePassword;

    private String databaseName;

    @Autowired
    public DatabaseServiceImpl(BasicDataSource dataSource) {
        Pattern pattern = Pattern.compile(DATABASE_URL_PATTERN);
        Matcher matcher = pattern.matcher(dataSource.getUrl());
        if (matcher.matches()) {
            databaseHost = matcher.group(1);
            databasePort = matcher.group(2);
            databaseName = matcher.group(3);
        } else {
            throw new IllegalArgumentException(
                    String.format("DB url = '%s' isn't match pattern '%s'", dataSource.getUrl(), DATABASE_URL_PATTERN));
        }
        databaseUserName = dataSource.getUsername();
        databasePassword = dataSource.getPassword();
    }

    @Override
    public byte[] getDatabaseDump() throws IOException {
        Process process = Runtime.getRuntime().exec(
                String.format("mysqldump -h %s -P %s -u %s --password=%s -B %s", databaseHost, databasePort, databaseUserName, databasePassword, databaseName));

        byte[] databaseDump = IOUtils.toByteArray(process.getInputStream());
        if (databaseDump == null || databaseDump.length == 0) {
            throw new IllegalStateException(IOUtils.toString(process.getErrorStream()));
        }
        return databaseDump;
    }

    @Override
    public String getDatabaseHost() {
        return databaseHost;
    }

    @Override
    public String getDatabasePort() {
        return databasePort;
    }

    @Override
    public String getDatabaseUserName() {
        return databaseUserName;
    }

    @Override
    public String getDatabasePassword() {
        return databasePassword;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }
}
