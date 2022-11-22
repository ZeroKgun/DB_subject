package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.revised.Edge;
import com.tinkerpop.blueprints.revised.Graph;
import com.tinkerpop.blueprints.revised.Vertex;

import java.sql.*;
import java.util.HashSet;
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


        stmt.executeUpdate("CREATE OR REPLACE TABLE vertex(id VARCHAR(40) UNIQUE , property JSON, INDEX(id))");
        stmt.executeUpdate("CREATE OR REPLACE TABLE edge(id VARCHAR(40) UNIQUE, Vout VARCHAR(20), Vin VARCHAR(20), label VARCHAR(20), property JSON, INDEX(id), INDEX(vout), INDEX(vin))");

    }

    @Override
    public Vertex addVertex(String id) throws IllegalArgumentException {
        if (id.contains("|")) {
            throw new IllegalArgumentException("id cannot contain '|'");
        }

        try {
            rs = stmt.executeQuery("SELECT COUNT(id) FROM vertex WHERE id = '" + id+"'");
            int cnt = 0;

            while(rs.next()) {
                if(1 == rs.getInt(1))
                    cnt++;
                if(cnt == 0) {
                    stmt.executeUpdate("INSERT INTO vertex VALUES('" + id + "', null)");
                }
                break;
            }

            Vertex newVertex = new PersistentVertex(this, id);

            return newVertex;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vertex getVertex(String id) {

        try {
            rs = stmt.executeQuery("SELECT id FROM vertex WHERE id = " + id + ";");
            Vertex newVertex = null;

            while(rs.next()) {
                //String newId = rs.getString(1);

                newVertex = new PersistentVertex(this, id);
            }

            return newVertex;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeVertex(Vertex vertex) {

    }

    @Override
    public Collection<Vertex> getVertices() {
        HashSet<Vertex> newVertices = new HashSet<>();

        try {
            rs = stmt.executeQuery("SELECT id FROM vertex;");
            while(rs.next()) {
                String newId = rs.getString(1);
                Vertex newVertex = new PersistentVertex(this, newId);

                newVertices.add(newVertex);
            }

            return newVertices;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Vertex> getVertices(String key, Object value) {
        HashSet<Vertex> newVertices = new HashSet<>();

        try {
            if (value instanceof String)
                rs = stmt.executeQuery("SELECT id FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = '" + value + "';");
            else
                rs = stmt.executeQuery("SELECT id FROM vertex WHERE JSON_VALUE(property, '$." + key + "') = " + value + ";");
            while(rs.next()) {
                String newId = rs.getString(1);
                Vertex newVertex = new PersistentVertex(this, newId);

                newVertices.add(newVertex);
            }

            return newVertices;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Edge addEdge(Vertex outVertex, Vertex inVertex, String label) throws IllegalArgumentException, NullPointerException {
        if (label.contains("|")) {
            throw new IllegalArgumentException("label cannot contain '|'");
        }
        if (outVertex == null) {
            throw new NullPointerException("outVertex cannot be null");
        }
        if (inVertex == null) {
            throw new NullPointerException("inVertex cannot be null");
        }

        String edge_id = outVertex.getId() + "|" + label + "|" + inVertex.getId();

        try {
            rs = stmt.executeQuery("SELECT COUNT(id) FROM edge WHERE id = '" + edge_id + "'");

            while(true) {
                if (!rs.next()) break;
                int cnt = 0;
                cnt = rs.getInt(1);
                if(cnt == 0) {
                        stmt.executeUpdate("INSERT INTO edge VALUES('" +edge_id+"','"+ outVertex.getId() + "','"+ inVertex.getId() + "','" + label + "',null)");
                }
                break;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Edge newEdge = new PersistentEdge(this, outVertex.getId(), label, inVertex.getId());

        return newEdge;
    }

    @Override
    public Edge getEdge(Vertex outVertex, Vertex inVertex, String label) throws IllegalArgumentException, NullPointerException {

        String edge_id = outVertex.getId() + "|" + label + "|" + inVertex.getId();

        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT id FROM edge WHERE id=" + edge_id);
            if (rs.next())
            {
                return (Edge) new PersistentEdge(this, outVertex.getId(), label, inVertex.getId());
            }
            else
                return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Edge getEdge(String id) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge WHERE id=" + id);
            if (rs.next())
            {
                return (Edge) new PersistentEdge(this, rs.getString("Vout"), rs.getString("label"), rs.getString("Vin"));
            }
            else
                return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeEdge(Edge edge) {

    }

    @Override
    public Collection<Edge> getEdges() {
        Collection<Edge> allEdges = new HashSet<Edge>();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge");
            while (rs.next())
            {
                Edge newEdge = new PersistentEdge(this, rs.getString("Vout"),rs.getString("label"), rs.getString("Vin"));
                allEdges.add(newEdge);
            }
            return allEdges;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Edge> getEdges(String key, Object value) {
        Collection<Edge> allEdges = new HashSet<Edge>();
        ResultSet rs;
        try {
            if (value instanceof String) {
                    rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge WHERE json_value(property, '$."+key+"') = '"+value+"';");
            }
            else {
                rs = stmt.executeQuery("SELECT Vout, label, Vin FROM edge WHERE json_value(property, '$."+key+"') = "+value+";");
            }
            while (true)
            {
                if (!rs.next()) break;
                allEdges.add(new PersistentEdge(this, rs.getString("Vout"),rs.getString("label"), rs.getString("Vin")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allEdges;
    }

    @Override
    public void shutdown() {

    }
}
