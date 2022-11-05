package tech.dakenviy.patcher.asm.tree;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;

public class LabelNodeContainer extends LabelNode {

    private Label label;

    public LabelNodeContainer() {
        super();
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public Label getLabel() {
        return this.label;
    }

    @Override
    public void resetLabel() {
        this.label = null;
    }
}
