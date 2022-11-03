package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Graph;
import com.tinkerpop.blueprints.revised.Vertex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class PersistentGraph implements Graph {
    Connection connection = null;
    Statement stmt = null;

    PersistentGraph(String id, String pw, String name) throws SQLException {

        connection =
                DriverManager.getConnection("jdbc:mariadb://localhost:3306", id, pw);

        stmt = connection.createStatement();

        stmt.executeUpdate("CREATE OR REPLACE DATABASE " + name);
        stmt.executeUpdate("USE " + name);


        stmt.executeUpdate("CREATE OR REPLACE TABLE vertex(id INT UNIQUE, property JSON)");
        stmt.executeUpdate("CREATE OR REPLACE TABLE edge(id INT UNIQUE, Vout INT, Vin INT, label VARHCAR(20), property JSON)");


    }

    @Override
    public Vertex addVertex(String id) throws IllegalArgumentException, SQLException {

        stmt.executeUpdate("INSERT INTO vertex VALUES(" + id + ", null)");

        return (Vertex)(new PersistentVertex(Integer.parseInt(id), null));
    }

    @Override
    public Vertex getVertex(String id) {
        return null;
    }

    @Override
    public void removeVertex(Vertex vertex) {

    }

    @Override
    public Collection<Vertex> getVertices() {
        return null;
    }

    @Override
    public Collection<Vertex> getVertices(String key, Object value) {
        return null;
    }

    @Override
    public Edge addEdge(Vertex outVertex, Vertex inVertex, String label) throws IllegalArgumentException, NullPointerException {
        return null;
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
