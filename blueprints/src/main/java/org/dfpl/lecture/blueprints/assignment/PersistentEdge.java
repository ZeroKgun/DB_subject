package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Set;

public class PersistentEdge implements Edge {
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getLabel() {
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
