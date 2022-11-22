package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Vertex;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.HashSet;

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
            rs = g.stmt.executeQuery("SELECT property FROM vertex WHERE id =" +this.id +";");
            while(rs.next()) {
                properties = rs.getString("property");
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
            rs = g.stmt.executeQuery("SELECT property FROM vertex WHERE id = " + this.id + ";");
            while(rs.next()) {
                properties = rs.getString("property");
                r_properties = new JSONObject(properties);
            }
            return r_properties.keySet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        //s : id / o : jsonobject
        try {
            ResultSet rs;
            rs = g.stmt.executeQuery("SELECT property FROM vertex WHERE id =" + this.id);
            while (rs.next()) {
                if (rs.getString("property") == null) {
                    if (value instanceof String)
                        g.stmt.executeUpdate("UPDATE vertex SET property = JSON_OBJECT('" + key + "','" + value + "') WHERE id = " + this.id);
                    else
                        g.stmt.executeUpdate("UPDATE vertex SET property = JSON_OBJECT('" + key + "'," + value + ") WHERE id = " + this.id);
                } else {
                    if (value instanceof String)
                        g.stmt.executeUpdate("UPDATE vertex SET property = JSON_SET(property,'$." + key + "','" + value + "') WHERE id = " + this.id);
                    else
                        g.stmt.executeUpdate("UPDATE vertex SET property = JSON_SET(property,'$." + key + "'," + value + ") WHERE id = " + this.id);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object removeProperty(String key) {
        return null;
    }

    @Override
    public Collection<Edge> getEdges(Direction direction, String... labels) throws IllegalArgumentException {
        Collection<Edge> newEdges = new HashSet<Edge>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT vout,vin,label FROM edge WHERE Vout = '" + this.id + "'");
                while (rs.next()) {
                    Edge newEdge = new PersistentEdge(this.g, rs.getString("vout"), rs.getString("label"), rs.getString("vin"));
                    if(labels.length == 0) {
                        newEdges.add(newEdge);
                    }
                    else {
                        for(String label:labels) {
                            if (newEdge.getLabel().equals(label)) {
                                newEdges.add(newEdge);
                                break;
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
                rs = g.stmt.executeQuery("SELECT vout,vin,label FROM edge WHERE Vin = '" + this.id + "'");
                while (rs.next()) {
                    Edge newEdge = new PersistentEdge(this.g, rs.getString("vout"), rs.getString("label"), rs.getString("vin"));
                    if(labels.length == 0) {
                        newEdges.add(newEdge);
                    }
                    else {
                        for(String label:labels) {
                            if (newEdge.getLabel().equals(label)) {
                                newEdges.add(newEdge);
                                break;
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
        Collection<Vertex> newVertices = new HashSet<>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                rs = g.stmt.executeQuery("SELECT Vin, label FROM edge WHERE Vout = '" + this.id + "'");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString(1));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString(2).equals(label)) {
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
                rs = g.stmt.executeQuery("SELECT Vout,label FROM edge WHERE Vin = '" + this.id + "'");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString(1));
                    if(labels.length == 0) {
                        newVertices.add(newVertex);
                    }
                    else {
                        for(String label:labels) {
                            if (rs.getString(2).equals(label)) {
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
        Collection<Vertex> AllVertices = new ArrayList<Vertex>();
        for (Vertex vhop1 : getVertices(direction, labels)){
            for (Vertex vhop2 : vhop1.getVertices(direction, labels))
                for (Vertex vhop3 : vhop2.getVertices(direction, labels))
                    AllVertices.add(vhop3);
        }
        return AllVertices;
    }

    @Override
    public Collection<Vertex> getVertices(Direction direction, String key, Object value, String... labels) throws IllegalArgumentException {
        Collection<Vertex> newVertices = new HashSet<>();
        if(direction.equals(Direction.OUT)) {
            ResultSet rs = null;
            try {
                if (value instanceof String)
                    rs = g.stmt.executeQuery("SELECT vin, label FROM edge WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "' AND Vout = '"+ this.id +"';");
                else
                    rs = g.stmt.executeQuery("SELECT vin, label FROM edge WHERE JSON_VALUE(property, '$." + key + "') = " + value + " AND Vout = '"+ this.id +"';");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString("vin"));
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
                    rs = g.stmt.executeQuery("SELECT vout, label FROM edge WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "' AND Vin = '"+id+"';");
                else
                    rs = g.stmt.executeQuery("SELECT vout, label FROM edge WHERE JSON_VALUE(property, '$." + key + "') = " + value + " AND Vin = '"+id+"';");
                while (rs.next()) {
                    Vertex newVertex = new PersistentVertex(this.g, rs.getString("vout"));
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


    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (int) id.hashCode();
        return hash;
    }



}

