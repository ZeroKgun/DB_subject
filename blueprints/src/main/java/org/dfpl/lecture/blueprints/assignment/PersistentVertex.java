package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;

import java.util.Set;

public class PersistentVertex implements Vertex {

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
    }

    @Override
    public Set<String> getPropertyKeys() {
        return null;
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
