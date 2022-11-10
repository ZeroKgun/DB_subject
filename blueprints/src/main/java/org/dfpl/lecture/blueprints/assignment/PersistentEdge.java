package org.dfpl.lecture.blueprints.assignment;



import com.tinkerpop.blueprints.revised.Direction;
import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Graph;
import com.tinkerpop.blueprints.revised.Vertex;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
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
        this.id = outVertex + label + inVertex;

    }
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        return null;
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
    public Object getProperty(String key) throws SQLException
    {
        ResultSet rs;
        String properties="";
        String result = "";
        JSONObject r_properties = null;
        rs = g.stmt.executeQuery("SELECT * FROM edge WHERE id =" +this.id +";");
        while(rs.next()) {
            properties = rs.getString(5);
            r_properties = new JSONObject(properties);
        }


        return r_properties.getString(key);

    }

    @Override
    public Set<String> getPropertyKeys() throws SQLException {
        ResultSet rs;
        String properties="";
        JSONObject r_properties = null;
        rs = g.stmt.executeQuery("SELECT * FROM edge WHERE id = " + this.id + ";");

        while(rs.next()) {
            properties = rs.getString(5);
            r_properties = new JSONObject(properties);
        }
        return r_properties.keySet();
    }

    @Override
    public void setProperty(String key, Object value) throws SQLException {
        ResultSet rs;
        rs = g.stmt.executeQuery("SELECT * FROM edge WHERE id ="+this.id);
        while(rs.next())
        {
            if(rs.getString(5) == null) {
                g.stmt.executeUpdate("UPDATE edge SET property = JSON_OBJECT('" + key + "','" + value + "') WHERE id = " + this.id);
            }
            else {
                g.stmt.executeUpdate("UPDATE edge SET property = JSON_SET(property,'$."+key+"','"+value+"') WHERE id = " + this.id);
            }
        }
    }

    @Override
    public Object removeProperty(String key) {
        return null;
    }
}
