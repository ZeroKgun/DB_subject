package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import org.json.JSONObject;

import java.util.Set;

public class PersistentVertex implements Vertex {

    private int m_id;
    private JSONObject m_property;

    public PersistentVertex(int id, JSONObject property) {
        m_id = id;
        m_property = property;
    }

    @Override
    public Iterable<Edge> getEdges(Direction direction, String... strings) {
        return null;
    }

    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... strings) {
        return null;
    }

    @Override
    public VertexQuery query() {
        return null;
    }

    @Override
    public Edge addEdge(String s, Vertex vertex) {
        return null;
    }

    @Override
    public <T> T getProperty(String s) {
        return null;
                //properties.get(s);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return null;
                //properties.keySet();
    }

    @Override
    public void setProperty(String s, Object o) {

    }

    @Override
    public <T> T removeProperty(String s) {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public Object getId() {
        return null;
    }
}
