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
            int newId = rs.getInt(1);

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
            int newId = rs.getInt(1);
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
            int newId = rs.getInt(1);
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

        String edgeID = outVertex.getId() + label + inVertex.getId();

        stmt.executeUpdate("INSERT INTO edge VALUES("+edgeID +","+ outVertex.getId() + ","+inVertex.getId()+","+", null)");

        Edge newEdge = new PersistentEdge(this, outVertex, label, inVertex); //생성자 논의

        return newEdge;
    }

    @Override
    public Edge getEdge(Vertex outVertex, Vertex inVertex, String label) {
        return null;
    }

    @Override
    public Edge getEdge(String id) {
        return null;
    }

    @Override
    public void removeEdge(Edge edge) {

    }

    @Override
    public Collection<Edge> getEdges() {
        return null;
    }

    @Override
    public Collection<Edge> getEdges(String key, Object value) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
