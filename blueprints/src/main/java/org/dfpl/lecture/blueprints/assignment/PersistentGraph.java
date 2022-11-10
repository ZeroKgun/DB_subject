package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Graph;
import com.tinkerpop.blueprints.revised.Vertex;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class PersistentGraph implements Graph {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    PersistentGraph(String id, String pw, String name) throws SQLException {

        connection =
                DriverManager.getConnection("jdbc:mariadb://localhost:3306", id, pw);

        stmt = connection.createStatement();

        stmt.executeUpdate("CREATE OR REPLACE DATABASE " + name);
        stmt.executeUpdate("USE " + name);


        stmt.executeUpdate("CREATE OR REPLACE TABLE vertex(id INT UNIQUE, property JSON)");
        stmt.executeUpdate("CREATE OR REPLACE TABLE edge(id VARCHAR(40) UNIQUE, Vout INT, Vin INT, label VARHCAR(20), property JSON)");


    }

    @Override
    public Vertex addVertex(String id) throws IllegalArgumentException, SQLException {

        stmt.executeUpdate("INSERT INTO vertex VALUES(" + id + ", null)");

        Vertex newVertex = new PersistentVertex(this, id);

        return newVertex;
    }

    @Override
    public Vertex getVertex(String id) throws SQLException {

        rs = stmt.executeQuery("SELECT * FROM vertex WHERE id = " + id + ";");

        Vertex newVertex = null;

        while(rs.next()) {
            String newId = rs.getString(1);

            newVertex = new PersistentVertex(this, newId);
        }

        return newVertex;
    }

    @Override
    public void removeVertex(Vertex vertex) {

    }

    @Override
    public Collection<Vertex> getVertices() throws SQLException {
        ArrayList<Vertex> newVertices = new ArrayList<>();

        rs = stmt.executeQuery("SELECT * FROM vertex;");

        while(rs.next()) {
            String newId = rs.getString(1);
            Vertex newVertex = new PersistentVertex(this, newId);

            newVertices.add(newVertex);
        }

        return newVertices;
    }

    @Override
    public Collection<Vertex> getVertices(String key, Object value) throws SQLException {
        ArrayList<Vertex> newVertices = new ArrayList<>();

        rs = stmt.executeQuery("SELECT * FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "';");

        while(rs.next()) {
            String newId = rs.getString(1);
            Vertex newVertex = new PersistentVertex(this, newId);

            newVertices.add(newVertex);
        }

        return newVertices;
    }

    @Override
    public Edge addEdge(Vertex outVertex, Vertex inVertex, String label) throws IllegalArgumentException, NullPointerException, SQLException {
        if (label.contains("|")) {
            throw new IllegalArgumentException("label cannot contain '|'");
        }
        if (outVertex == null) {
            throw new NullPointerException("outVertex cannot be null");
        }
        if (inVertex == null) {
            throw new NullPointerException("inVertex cannot be null");
        }
        String edge_id = outVertex + "|" + label + "|" + inVertex;
        stmt.executeUpdate("INSERT INTO vertex VALUES(" +edge_id+","+ outVertex + ","+ inVertex + "," + label + ",null)");

        return (Edge) new PersistentEdge(this, outVertex.getId(), label, inVertex.getId());
    }

    @Override
    public Edge getEdge(Vertex outVertex, Vertex inVertex, String label) throws IllegalArgumentException, NullPointerException, SQLException  {
        String edge_id = outVertex + "|" + label + "|" + inVertex;
        ResultSet rs = stmt.executeQuery("SELECT * FROM edge WHERE id=" + edge_id);
        if (rs.next())
        {
            return (Edge) new PersistentEdge(this, outVertex.getId(), label, inVertex.getId());
        }
        else
            return null;
    }

    @Override
    public Edge getEdge(String id) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM edge WHERE id=" + id);
        if (rs.next())
        {
            return (Edge) new PersistentEdge(this, rs.getString("Vout"), rs.getString("label"), rs.getString("Vin"));
        }
        else
            return null;
    }

    @Override
    public void removeEdge(Edge edge) {

    }

    @Override
    public Collection<Edge> getEdges() throws SQLException {
        Collection<Edge> allEdges = new ArrayList<Edge>();
        ResultSet rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge");
        while (rs.next())
        {
            allEdges.add(new PersistentEdge(this, rs.getString("Vout"),rs.getString("label"), rs.getString("Vin")));
        }
        return allEdges;
    }

    @Override
    public Collection<Edge> getEdges(String key, Object value) throws SQLException {
        Collection<Edge> allEdges = new ArrayList<Edge>();
        ResultSet rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge WHERE json_value(property, '$."+key+"') = '"+value+"';");
        while (rs.next())
        {
            allEdges.add(new PersistentEdge(this, rs.getString("Vout"),rs.getString("label"), rs.getString("Vin")));
        }
        return allEdges;
    }

    @Override
    public void shutdown() {

    }
}
