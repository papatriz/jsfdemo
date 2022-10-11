package com.papatriz.jsfdemo.models;

public class CargoCycle {
    private Node loadNode;
    private Node unloadNode;
    private Cargo cargo;
    private boolean hasWeightError;
    private boolean hasCitiesError;

    public boolean isHasWeightError() {
        return hasWeightError;
    }

    public void setHasWeightError(boolean hasWeightError) {
        this.hasWeightError = hasWeightError;
    }

    public boolean isHasCitiesError() {
        return hasCitiesError;
    }

    public void setHasCitiesError(boolean hasCitiesError) {
        this.hasCitiesError = hasCitiesError;
    }

    public CargoCycle() {
        this.loadNode = new Node();
        this.unloadNode = new Node();
        this.cargo = new Cargo();

        this.loadNode.setType(EActionType.LOAD);
        this.loadNode.setCargo(this.cargo);
        this.unloadNode.setType(EActionType.UNLOAD);
        this.unloadNode.setCargo(this.cargo);
    }

    public Node getLoadNode() {
        return loadNode;
    }

    public void setLoadNode(Node loadNode) {
        this.loadNode = loadNode;
    }

    public Node getUnloadNode() {
        return unloadNode;
    }

    public void setUnloadNode(Node unloadNode) {
        this.unloadNode = unloadNode;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}