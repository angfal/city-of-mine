package edu.course.city.service;

import java.io.IOException;

public interface DatabaseService {

    String getDatabaseHost();

    String getDatabasePort();

    String getDatabaseUserName();

    String getDatabasePassword();

    String getDatabaseName();

    byte[] getDatabaseDump() throws IOException;
}
