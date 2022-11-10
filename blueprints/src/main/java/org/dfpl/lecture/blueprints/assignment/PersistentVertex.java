package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Vertex;
import org.json.JSONObject;

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
<<<<<<< HEAD
=======

>>>>>>> bdcf7445c255283818d10719d91eb365fb83685c
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getProperty(String key) throws SQLException {
        ResultSet rs;
        String properties="";
        String result = "";
        JSONObject r_properties = null;
        rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id =" +this.id +";");
        while(rs.next()) {
            properties = rs.getString(2);
            r_properties = new JSONObject(properties);
        }
        //Object r_result = r_properties.get(key);
        if(r_properties.isNull(key)){
            return null;
        }
        else
            return String.valueOf(r_properties.get(key));
    }

    @Override
    public Set<String> getPropertyKeys() throws SQLException {
        ResultSet rs;
        String properties="";
        JSONObject r_properties = null;
        rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id = " + this.id + ";");

        while(rs.next()) {
            properties = rs.getString(2);
            r_properties = new JSONObject(properties);
        }
        return r_properties.keySet();

    }

    @Override
    public void setProperty(String key, Object value) throws SQLException {
        //s : id / o : jsonobject
        ResultSet rs;
        rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id ="+this.id);
        while(rs.next())
        {
            if(rs.getString(2) == null) {
                g.stmt.executeUpdate("UPDATE vertex SET property = JSON_OBJECT('" + key + "','" + value + "') WHERE id = " + this.id);
            }
            else {
                g.stmt.executeUpdate("UPDATE vertex SET property = JSON_SET(property,'$."+key+"','"+value+"') WHERE id = " + this.id);
            }
        }
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
