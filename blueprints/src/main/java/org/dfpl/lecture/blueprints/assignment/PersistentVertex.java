package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Vertex;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        ResultSet rs;
        String properties="";
        //Object result = "";
        JSONObject r_properties = null;
        try {
            rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id =" +this.id +";");
            while(rs.next()) {
                properties = rs.getString(2);
                r_properties = new JSONObject(properties);
            }
            //Object r_result = r_properties.get(key);
            if(r_properties.isNull(key)){
                return null;
            }
            else {
                if(r_properties.get(key) instanceof BigDecimal)
                    return Double.parseDouble(String.valueOf(r_properties.get(key)));
                else
                    return r_properties.get(key);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getPropertyKeys() {
        ResultSet rs;
        String properties="";
        JSONObject r_properties = null;
        try {
            rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id = " + this.id + ";");
            while(rs.next()) {
                properties = rs.getString(2);
                r_properties = new JSONObject(properties);
            }
            return r_properties.keySet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setProperty(String key, Object value) throws SQLException {
        //s : id / o : jsonobject
        ResultSet rs;
        rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE id ="+this.id);
        while(rs.next())
        {
            if(rs.getString(2) == null) {
                if(value instanceof String)
                    g.stmt.executeUpdate("UPDATE vertex SET property = JSON_OBJECT('" + key + "','" + value + "') WHERE id = " + this.id);
                else
                    g.stmt.executeUpdate("UPDATE vertex SET property = JSON_OBJECT('" + key + "'," + value + ") WHERE id = " + this.id);
            }
            else {
                if(value instanceof String)
                    g.stmt.executeUpdate("UPDATE vertex SET property = JSON_SET(property,'$."+key+"','"+value+"') WHERE id = " + this.id);
                else
                    g.stmt.executeUpdate("UPDATE vertex SET property = JSON_SET(property,'$."+key+"',"+value+") WHERE id = " + this.id);
            }
        }
    }

    @Override
    public Object removeProperty(String key) {
        return null;
    }

    @Override
    public Collection<Edge> getEdges(Direction direction, String... labels) throws IllegalArgumentException {
        Collection<Edge> newEdges = new ArrayList<>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT * FROM edge WHERE Vout = '" + this.id + "'");
                while (rs.next()) {
                    Edge newEdge = new PersistentEdge(this.g, rs.getString(2), rs.getString(4), rs.getString(3));
                    if(labels.length == 0) {
                        newEdges.add(newEdge);
                    }
                    else {
                        for(String label:labels) {
                            if (newEdge.getLabel().equals(label)) {
                                newEdges.add(newEdge);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else if (direction.equals(Direction.IN)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT * FROM edge WHERE Vin = '" + this.id + "'");
                while (rs.next()) {
                    Edge newEdge = new PersistentEdge(this.g, rs.getString(2), rs.getString(4), rs.getString(3));
                    if(labels.length == 0) {
                        newEdges.add(newEdge);
                    }
                    else {
                        for(String label:labels) {
                            if (newEdge.getLabel().equals(label)) {
                                newEdges.add(newEdge);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new IllegalArgumentException("Direction.BOTH is not allowed");
        }

        return newEdges;
    }

    @Override
    public Collection<Vertex> getVertices(Direction direction, String... labels) throws IllegalArgumentException {
        Collection<Vertex> newVertices = new ArrayList<>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT * FROM edge WHERE Vout = '" + this.id + "'");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString(3));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString(4).equals(label)) {
                                newVertices.add(newVertex);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        else if (direction.equals(Direction.IN)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT * FROM edge WHERE Vin = '" + this.id + "'");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString(2));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString(4).equals(label)) {
                                newVertices.add(newVertex);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            throw new IllegalArgumentException("Direction.BOTH is not allowed");
        }
        return newVertices;
    }

    @Override
    public Collection<Vertex> getTwoHopVertices(Direction direction, String... labels) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<Vertex> getVertices(Direction direction, String key, Object value, String... labels) throws IllegalArgumentException {
        Collection<Vertex> newVertices = new ArrayList<>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                if (value instanceof String)
                    rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "';");
                else
                    rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = " + value + ";");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString("id"));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString("label").equals(label)) {
                                newVertices.add(newVertex);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        else if (direction.equals(Direction.IN)) {
            ResultSet rs = null;
            try {
                if (value instanceof String)
                    rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "';");
                else
                    rs = g.stmt.executeQuery("SELECT * FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = " + value + ";");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString("id"));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString("label").equals(label)) {
                                newVertices.add(newVertex);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            throw new IllegalArgumentException("Direction.BOTH is not allowed");
        }
        return newVertices;
    }

    @Override
    public void remove() {

    }
    @Override
    public boolean equals(Object obj) {
        return id.equals(((PersistentVertex) obj).getId());
    }

}

