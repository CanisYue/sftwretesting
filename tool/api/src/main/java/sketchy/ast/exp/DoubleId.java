package sketchy.ast.exp;

import sketchy.ast.visitor.Visitor;

import java.util.List;

/**
 * Double identifier expression.
 */
public class DoubleId extends IdExp<Double> {

    public DoubleId(List<String> ids) {
        this(false, ids);
    }

    public DoubleId(boolean exclude, List<String> ids) {
        super(ids);
    }

    @Override
    public Class<?> getIdType() {
        return Double.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
