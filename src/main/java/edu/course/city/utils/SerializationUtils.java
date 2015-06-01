package edu.course.city.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;


public class SerializationUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T object) {
        return (T) deserialize(serialize(object));
    }

    public static byte[] serialize(Serializable object) {
        if (object == null) {
            throw new IllegalArgumentException("The 'object' must not be null");
        }

        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream(512);
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(bytesOutputStream);
            objectOutputStream.writeObject(object);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to serialize", ex);
        } finally {
            IOUtils.closeQuietly(objectOutputStream);
        }
        return bytesOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The 'objectData' must not be null");
        }

        ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(objectData);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(bytesInputStream);
            return objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            throw new RuntimeException("Failed to deserialize", ex);
        } finally {
            IOUtils.closeQuietly(objectInputStream);
        }
    }
}