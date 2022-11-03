package org.dfpl.lecture.blueprints.assignment;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Set;

public class PersistentEdge implements Edge {
    private String id;
    private Vertex outV;
    private String label;
    private Vertex inV;

    public PersistentEdge(String id, Vertex outV, String label, Vertex inV){
        this.id = id;
        this.outV = outV;
        this.label = label;
        this.inV = inV;
    }


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
