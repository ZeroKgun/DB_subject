package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Vertex;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PersistentVertex implements Vertex {
    private PersistentGraph g;
    private String id;

    public PersistentVertex(PersistentGraph g, String id) throws SQLException {
        this.g = g;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getProperty(String key) {
        return null;
    }

    @Override
    public Set<String> getPropertyKeys() throws SQLException {
        Set<String> keySet = new HashSet<>();
        ResultSet rs = g.stmt.executeQuery("SELECT JSON_KEYS(property) FROM vertex WHERE id = " + this.id +";");
        String a = rs.getString(1);
        String[] arr = a.split("\"");

        for(int i =0;i<arr.length;i++){
            if(i%2==1){
                keySet.add(arr[i]);
            }
        }

        return keySet;
    }

    @Override
    public void setProperty(String key, Object value) throws SQLException {
        //s : id / o : jsonobject
        g.stmt.executeUpdate("UPDATE vertex SET property = '" + value + "' where id = " + key);

    }

    @Override
    public Object removeProperty(String key) {
        return null;
    }

    @Override
    public Collection<Edge> getEdges(Direction direction, String... labels) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<Vertex> getVertices(Direction direction, String... labels) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<Vertex> getTwoHopVertices(Direction direction, String... labels) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<Vertex> getVertices(Direction direction, String key, Object value, String... labels) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void remove() {

    }
}
