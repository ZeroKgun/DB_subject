package org.dfpl.lecture.blueprints.assignment;


import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Vertex;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

public class PersistentEdge implements Edge {

    private PersistentGraph g;
    private String id;
    private String outVertex;
    private String label;
    private String inVertex;

    public PersistentEdge(PersistentGraph g, String outVertex, String label, String inVertex)
    {
        this.g = g;
        this.outVertex = outVertex;
        this.label = label;
        this.inVertex = inVertex;
        this.id = outVertex + "|" + label + "|" +  inVertex;

    }
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        if (direction.equals(Direction.OUT)) {
            ResultSet rs;
            try {
                rs = g.stmt.executeQuery("SELECT id FROM vertex WHERE id = " + outVertex + ";");
                Vertex newVertex = null;

                while(rs.next()) {
                    String newId = rs.getString(1);

                    newVertex = new PersistentVertex(g, newId);
                }
                return newVertex;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (direction.equals(Direction.IN)) {
            ResultSet rs;
            try {
                rs = g.stmt.executeQuery("SELECT id FROM vertex WHERE id = " + inVertex + ";");
                Vertex newVertex = null;

                while(rs.next()) {
                    String newId = rs.getString(1);

                    newVertex = new PersistentVertex(g, newId);
                }

                return newVertex;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Direction.BOTH is not allowed");
        }
    }

    @Override
    public String getLabel() {

        return this.label;
    }

    @Override
    public void remove() {

    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getProperty(String key)
    {
        ResultSet rs;
        String properties="";
        //Object result = "";
        JSONObject r_properties = null;
        try {
            rs = g.stmt.executeQuery("SELECT property FROM edge WHERE id = '" +this.id +"'");
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
    public Set<String> getPropertyKeys(){
        ResultSet rs;
        String properties="";
        JSONObject r_properties = null;
        try {
            rs = g.stmt.executeQuery("SELECT property FROM edge WHERE id = '" + this.id + "'");
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
    public void setProperty(String key, Object value)  {
        try {
            ResultSet rs;
            rs = g.stmt.executeQuery("SELECT property FROM edge WHERE id = '"+this.id +"'");
            while (rs.next()) {
                if (rs.getString("property") == null) {
                    if (value instanceof String)
                        g.stmt.executeUpdate("UPDATE edge SET property = JSON_OBJECT('" + key + "','" + value + "') WHERE id = '" + this.id + "'");
                    else
                        g.stmt.executeUpdate("UPDATE edge SET property = JSON_OBJECT('" + key + "'," + value + ") WHERE id = '" + this.id + "'");
                } else {
                    if (value instanceof String)
                        g.stmt.executeUpdate("UPDATE edge SET property = JSON_SET(property,'$." + key + "','" + value + "') WHERE id = '" + this.id + "'");
                    else
                        g.stmt.executeUpdate("UPDATE edge SET property = JSON_SET(property,'$." + key + "'," + value + ") WHERE id = '" + this.id + "'");
                }
                break;
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
    public boolean equals(Object obj) {
        return (this.getId().equals(((Edge)obj).getId()));
    }

    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (int) id.hashCode();
        return hash;
    }

}
