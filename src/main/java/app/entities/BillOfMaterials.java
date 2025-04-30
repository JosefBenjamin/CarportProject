package app.entities;

import java.util.List;

public class BillOfMaterials {
    private List<CompleteUnitMaterial> completeUnitMaterials;

    public BillOfMaterials(List<CompleteUnitMaterial> completeUnitMaterials) {
        this.completeUnitMaterials = completeUnitMaterials;
    }

    public List<CompleteUnitMaterial> getCompleteUnitMaterials() {
        return completeUnitMaterials;
    }

    public void setCompleteUnitMaterials(List<CompleteUnitMaterial> completeUnitMaterials) {
        this.completeUnitMaterials = completeUnitMaterials;
    }

    @Override
    public String toString() {
        return "BillOfMaterials{" +
                "completeUnitMaterials=" + completeUnitMaterials +
                '}';
    }
}

