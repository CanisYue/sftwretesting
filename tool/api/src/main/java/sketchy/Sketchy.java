package sketchy;

import sketchy.ast.exp.AltExp;
import sketchy.ast.exp.AssignExp;
import sketchy.ast.exp.BAriExp;
import sketchy.ast.exp.LHSExp;
import sketchy.ast.exp.LongVal;
import sketchy.ast.exp.PreIncExp;
import sketchy.ast.exp.RefId;
import sketchy.ast.exp.ShiftExp;
import sketchy.ast.exp.BoolId;
import sketchy.ast.exp.BoolVal;
import sketchy.ast.exp.DoubleId;
import sketchy.ast.exp.DoubleVal;
import sketchy.ast.exp.Exp;
import sketchy.ast.exp.ImBoolVal;
import sketchy.ast.exp.ImDoubleVal;
import sketchy.ast.exp.ImIntVal;
import sketchy.ast.exp.IntArrVal;
import sketchy.ast.exp.IntId;
import sketchy.ast.exp.IntVal;
import sketchy.ast.exp.LogExp;
import sketchy.ast.exp.RefArrAccessExp;
import sketchy.ast.exp.RelExp;
import sketchy.ast.operator.AriOp;
import sketchy.ast.operator.AriOrShiftOp;
import sketchy.ast.operator.ShiftOp;
import sketchy.ast.operator.LogOp;
import sketchy.ast.operator.RelOp;
import sketchy.ast.stmt.AltStmt;
import sketchy.ast.stmt.BlockStmt;
import sketchy.ast.stmt.ExprStmt;
import sketchy.ast.stmt.IfStmt;
import sketchy.ast.stmt.Stmt;
import sketchy.ast.stmt.TryStmt;
import sketchy.ast.stmt.WhileStmt;
import sketchy.data.Data;
import sketchy.driver.SearchStrategy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Syntactic sugar to help write sketch programs.
 */
public final class Sketchy {

    /*---------------------- Operators -----------------------------*/

    /**
     * {@literal &&}.
     */
    public static LogOp AND = LogOp.AND;

    /**
     * {@literal ||}.
     */
    public static LogOp OR = LogOp.OR;

    /**
     * {@literal <}.
     */
    public static RelOp LT = RelOp.LT;

    /**
     * {@literal <=}.
     */
    public static RelOp LE = RelOp.LE;

    /**
     * {@literal >}.
     */
    public static RelOp GT = RelOp.GT;

    /**
     * {@literal >=}.
     */
    public static RelOp GE = RelOp.GE;

    /**
     * {@literal ==}.
     */
    public static RelOp EQ = RelOp.EQ;

    /**
     * {@literal !=}.
     */
    public static RelOp NE = RelOp.NE;

    /**
     * {@literal -}.
     */
    public static AriOp SUB = AriOp.SUB;

    /**
     * {@literal +}.
     */
    public static AriOp ADD = AriOp.ADD;

    /**
     * {@literal *}.
     */
    public static AriOp MUL = AriOp.MUL;

    /**
     * {@literal *}.
     */
    public static AriOp DIV = AriOp.DIV;

    /**
     * {@literal /}.
     */
    public static AriOp MOD = AriOp.MOD;

    /**
     * {@literal <<}.
     */
    public static ShiftOp SHIFTL = ShiftOp.SHIFTL;

    /**
     * {@literal >>}.
     */
    public static ShiftOp SHIFTR = ShiftOp.SHIFTR;

    /**
     * {@literal >>>}.
     */
    public static ShiftOp USHIFTR = ShiftOp.USHIFTR;

    /*-------------------- Arithmetic expressions ------------------*/

    public static BAriExp<Integer> arithmetic(String exp1, int exp2, AriOp... ops) {
        return arithmetic(asIntIdOrIntArrAccess(exp1), asInt(exp2), ops);
    }

    public static BAriExp<Integer> arithmetic(int exp1, String exp2, AriOp... ops) {
        return arithmetic(asInt(exp1), asIntIdOrIntArrAccess(exp2), ops);
    }

    public static BAriExp<Integer> arithmetic(String exp1, String exp2, AriOp... ops) {
        return arithmetic(asIntIdOrIntArrAccess(exp1), asIntIdOrIntArrAccess(exp2), ops);
    }

    public static BAriExp<Integer> arithmetic(String exp1, Exp<Integer> exp2, AriOp... ops) {
        return arithmetic(asIntIdOrIntArrAccess(exp1), exp2, ops);
    }

    public static BAriExp<Integer> arithmetic(Exp<Integer> exp1, String exp2, AriOp... ops) {
        return arithmetic(exp1, asIntIdOrIntArrAccess(exp2), ops);
    }

    public static BAriExp<Integer> arithmetic(int exp1, int exp2, AriOp... ops) {
        return arithmetic(asInt(exp1), asInt(exp2), ops);
    }

    public static BAriExp<Integer> arithmetic(int exp1, Exp<Integer> exp2, AriOp... ops) {
        return arithmetic(asInt(exp1), exp2, ops);
    }

    public static BAriExp<Integer> arithmetic(Exp<Integer> exp1, int exp2, AriOp... ops) {
        return arithmetic(exp1, asInt(exp2), ops);
    }

    public static <N extends Number> BAriExp<N> arithmetic(Exp<N> left, Exp<N> right, boolean excludeDivOrMod) {
        if (!excludeDivOrMod) {
            return arithmetic(left, right);
        }
        // use all operators except DIV or MOD
        AriOp[] ariOps = AriOp.values();
        AriOp[] ops = new AriOp[ariOps.length - 2];
        for (int i = 0; i < ariOps.length; i++) {
            if (ariOps[i] != AriOp.DIV && ariOps[i] != AriOp.MOD) {
                ops[i] = ariOps[i];
            }
        }
        return arithmetic(left, right, ops);
    }

    /**
     * Arithmetic expression.
     */
    public static <N extends Number> BAriExp<N> arithmetic(Exp<N> exp1, Exp<N> exp2, AriOp... ops) {
        if (ops.length == 0) {
            ops = AriOp.values();
        }
        return new BAriExp<>(exp1, exp2, Arrays.asList(ops));
    }

    /*----------------------- Shift expressions --------------------*/

    public static ShiftExp<Integer> shift(int left, String right, ShiftOp... ops) {
        return shift(asInt(left), right, ops);
    }

    public static ShiftExp<Integer> shift(String left, int right, ShiftOp... ops) {
        return shift(left, asInt(right), ops);
    }

    public static ShiftExp<Integer> shift(String left, String right, ShiftOp... ops) {
        return shift(left, asIntIdOrIntArrAccess(right), ops);
    }

    public static ShiftExp<Integer> shift(Exp<Integer> left, String right, ShiftOp... ops) {
        return shift(left, asIntIdOrIntArrAccess(right), ops);
    }

    public static ShiftExp<Integer> shift(String left, Exp<Integer> right, ShiftOp... ops) {
        return shift(asIntIdOrIntArrAccess(left), right, ops);
    }

    public static ShiftExp<Integer> shift(int left, int right, ShiftOp... ops) {
        return shift(asInt(left), asInt(right), ops);
    }

    public static ShiftExp<Integer> shift(int left, Exp<Integer> right, ShiftOp... ops) {
        return shift(asInt(left), right, ops);
    }

    public static ShiftExp<Integer> shift(Exp<Integer> left, int right, ShiftOp... ops) {
        return shift(left, asInt(right), ops);
    }

    /**
     * Bitwise and bit shift expression.
     */
    public static <N extends Number> ShiftExp<N> shift(Exp<N> left, Exp<Integer> right, ShiftOp... ops) {
        if (ops.length == 0) {
            ops = ShiftOp.values();
        }
        return new ShiftExp<>(left, right, Arrays.asList(ops));
    }

    /*--------------------- Logic expressions ----------------------*/

    public static LogExp logic(boolean exp1, boolean exp2, LogOp... ops) {
        return logic(asBool(exp1), asBool(exp2), ops);
    }

    public static LogExp logic(boolean exp1, Exp<Boolean> exp2, LogOp... ops) {
        return logic(asBool(exp1), exp2, ops);
    }

    public static LogExp logic(Exp<Boolean> exp1, boolean exp2, LogOp... ops) {
        return logic(exp1, asBool(exp2), ops);
    }

    /**
     * Logic expression.
     */
    public static LogExp logic(Exp<Boolean> exp1, Exp<Boolean> exp2, LogOp... ops) {
        if (ops.length == 0) {
            ops = LogOp.values();
        }
        return new LogExp(exp1, exp2, Arrays.asList(ops));
    }

    /*-------------------- Relational expressions ------------------*/

    public static RelExp<Integer> relation(String exp1, int exp2, RelOp... ops) {
        return relation(asIntIdOrIntArrAccess(exp1), asInt(exp2), ops);
    }

    public static RelExp<Integer> relation(int exp1, String exp2, RelOp... ops) {
        return relation(asInt(exp1), asIntIdOrIntArrAccess(exp2), ops);
    }

    public static RelExp<Integer> relation(String exp1, String exp2, RelOp... ops) {
        return relation(asIntIdOrIntArrAccess(exp1), asIntIdOrIntArrAccess(exp2), ops);
    }

    public static RelExp<Integer> relation(String exp1, Exp<Integer> exp2, RelOp... ops) {
        return relation(asIntIdOrIntArrAccess(exp1), exp2, ops);
    }

    public static RelExp<Integer> relation(Exp<Integer> exp1, String exp2, RelOp... ops) {
        return relation(exp1, asIntIdOrIntArrAccess(exp2), ops);
    }

    public static RelExp<Integer> relation(int exp1, int exp2, RelOp... ops) {
        return relation(asInt(exp1), asInt(exp2), ops);
    }

    public static RelExp<Integer> relation(int exp1, Exp<Integer> exp2, RelOp... ops) {
        return relation(asInt(exp1), exp2, ops);
    }

    public static RelExp<Integer> relation(Exp<Integer> exp1, int exp2, RelOp... ops) {
        return relation(exp1, asInt(exp2), ops);
    }

    public static RelExp<Double> relation(Exp<Double> exp1, double exp2, RelOp... ops) {
        return relation(exp1, asDouble(exp2), ops);
    }

    /**
     * Relational expression.
     */
    public static <N extends Number> RelExp<N> relation(Exp<N> exp1, Exp<N> exp2, RelOp... ops) {
        if (ops.length == 0) {
            ops = RelOp.values();
        }
        return new RelExp<>(exp1, exp2, Arrays.asList(ops));
    }

    /*------------------------ Variables ---------------------------*/

    /**
     * A boolean variable given a range of choices by variable name.
     */
    public static BoolId boolId(String... ids) {
        return new BoolId(Arrays.asList(ids));
    }

    /**
     * An double variable given a range of choices by variable name.
     */
    public static DoubleId doubleId(String... ids) {
        return new DoubleId(Arrays.asList(ids));
    }

    public static RefId<Long> longId(String... ids) {
        return refId(Long.class, ids);
    }

    /**
     * An int variable given a range of choices by variable name.
     */
    public static IntId intId(String... ids) {
        return intId(false, ids);
    }

    /**
     * An int variable given a range of choices by variable name.
     */
    public static IntId intId(boolean exclude, String... ids) {
        return new IntId(exclude, Arrays.asList(ids));
    }

    public static <T> RefId<T> refId(Class<T> type, String... ids) {
        return new RefId<>(type, Arrays.asList(ids));
    }

    public static <T> RefId<T> refId(Class<T> type, boolean exclude, String... ids) {
        return new RefId<>(type, exclude, Arrays.asList(ids));
    }

    /**
     * An int array variable given a range of choices by variable
     * name.
     */
    public static RefId<int[]> intArrId(String... ids) {
        return refId(int[].class, ids);
    }

    /**
     * A long array variable given a range of choices by variable
     * name.
     */
    public static RefId<long[]> longArrId(String... ids) {
        return refId(long[].class, ids);
    }

    /**
     * A double array variable given a range of choices by variable
     * name.
     */
    public static RefId<double[]> doubleArrId(String... ids) {
        return refId(double[].class, ids);
    }

    /**
     * An int array value, totally unbounded.
     */
    public static IntArrVal intArrVal() {
        if (Config.ss == SearchStrategy.RANDOM) {
            return new IntArrVal();
        } else {
            return new IntArrVal(
                    // pick non-negative choices as len
                    Config.ints.stream().filter(i -> i >= 0).collect(Collectors.toList()),
                    Config.ints);
        }
    }

    /**
     * An int array value with length between {@code lenLow}
     * (inclusive) and {@code lenHigh} (exclusive) and unbounded
     * elements, where {@literal 0 <= lenLow < lenHigh}.
     */
    public static IntArrVal intArrVal(int lenLow, int lenHigh) {
        return Config.ss == SearchStrategy.RANDOM ?
                new IntArrVal(lenLow, lenHigh) :
                new IntArrVal(lenLow, lenHigh, Config.ints);
    }

    /**
     * An int array value with length between {@code lenLow}
     * (inclusive) and {@code lenHigh} (exclusive) and element between
     * {@code elemLow} (inclusive) and {@code elemHigh} (exclusive),
     * where {@literal 0 <= lenLow < lenHigh} and
     * {@literal 0 <= elemLow < elemHigh}.
     */
    public static IntArrVal intArrVal(int lenLow, int lenHigh, int elemLow, int eleHigh) {
        return new IntArrVal(lenLow, lenHigh, elemLow, eleHigh);
    }

    /**
     * An int array access expression given a range of choices by
     * variable name.
     */
    public static RefArrAccessExp<Integer, int[]> intArrAccessExp(String... ids) {
        return intArrAccessExp(intArrId(ids));
    }

    /**
     * An int array access expression given an int array expression,
     * with indices auto inferred.
     */
    public static RefArrAccessExp<Integer, int[]> intArrAccessExp(Exp<int[]> id) {
        return refArrAccessExp(int.class, int[].class, id);
    }

    /**
     * An int array access expression given an int array variable name
     * and an index number.
     */
    public static RefArrAccessExp<Integer, int[]> intArrAccessExp(String id, int index) {
        return intArrAccessExp(id, asInt(index));
    }

    /**
     * An int array access expression given an int array variable name
     * and an index number expression.
     */
    public static RefArrAccessExp<Integer, int[]> intArrAccessExp(String id, Exp<Integer> index) {
        return intArrAccessExp(intArrId(id), index);
    }

    /**
     * An long array access expression given an long array variable name
     * and an index number expression.
     */
    public static RefArrAccessExp<Integer, int[]> intArrAccessExp(Exp<int[]> id, Exp<Integer> index) {
        return refArrAccessExp(int.class, int[].class, id, index);
    }

    /**
     * An int array access expression given the name, e.g.
     * {@literal a[0]}.
     */
    public static RefArrAccessExp<Integer, int[]> asIntArrAccess(String arrAccess) {
        // We need special handling if id is "<arr>[<var>]"
        int lBracketIdx = arrAccess.indexOf('[');
        if (lBracketIdx == -1) {
            throw new RuntimeException(arrAccess + " is NOT an array access!");
        }
        String arr = arrAccess.substring(0, lBracketIdx);
        int rBracketIdx = arrAccess.indexOf(']');
        String idx = arrAccess.substring(lBracketIdx + 1, rBracketIdx);
        if (idx.matches("\\d+")) {
            // index is a number
            return intArrAccessExp(arr, Integer.parseInt(idx));
        } else {
            // index is a variable
            return intArrAccessExp(arr, intId(idx));
        }
    }

    /**
     * An int variable or int array access expression given the name,
     * e.g. {@literal a[0]} or {@literal a[0]}.
     */
    public static Exp<Integer> asIntIdOrIntArrAccess(String str) {
        if (str.contains("[")) {
            return asIntArrAccess(str);
        } else {
            return intId(str);
        }
    }

    /**
     * A long array access expression given a range of choices by
     * variable name.
     */
    public static RefArrAccessExp<Long, long[]> longArrAccessExp(String... ids) {
        return longArrAccessExp(longArrId(ids));
    }

    /**
     * A long array access expression given an long array expression,
     * with indices auto inferred.
     */
    public static RefArrAccessExp<Long, long[]> longArrAccessExp(Exp<long[]> id) {
        return refArrAccessExp(long.class, long[].class, id);
    }

    /**
     * A long array access expression given a long array variable name
     * and an index number expression.
     */
    public static RefArrAccessExp<Long, long[]> longArrAccessExp(Exp<long[]> id, Exp<Integer> index) {
        return refArrAccessExp(Long.class, long[].class, id, index);
    }

    public static RefArrAccessExp<Double, double[]> doubleArrAccessExp() {
        return refArrAccessExp(double.class, double[].class);
    }

    public static RefArrAccessExp<Double, double[]> doubleArrAccessExp(String... ids) {
        return doubleArrAccessExp(doubleArrId(ids));
    }

    public static RefArrAccessExp<Double, double[]> doubleArrAccessExp(Exp<double[]> id) {
        return refArrAccessExp(double.class, double[].class, id);
    }

    public static RefArrAccessExp<Double, double[]> doubleArrAccessExp(Exp<double[]> id, Exp<Integer> index) {
        return refArrAccessExp(double.class, double[].class, id, index);
    }

    /**
     * A reference array access expression given a range of choices
     * by variable name.
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, String... ids) {
        return refArrAccessExp(cType, aType, refId(aType, ids));
    }

    /**
     * A reference array access expression given a reference array
     * expression, with indices auto inferred.
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, Exp<A> id) {
        return new RefArrAccessExp<>(id);
    }

    /**
     * A reference array access expression given a reference array
     * variable name and a fixed index number.
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, String id, int index) {
        return refArrAccessExp(cType, aType, id, asInt(index));
    }

    /**
     * A reference array access expression given an array variable
     * name and an index number expression.
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, String id, Exp<Integer> index) {
        return refArrAccessExp(cType, aType, refId(aType, id), index);
    }

    /**
     * A reference array access expression given a reference array
     * expression and a fixed index number .
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, Exp<A> id, int index) {
        return refArrAccessExp(cType, aType, id, asInt(index));
    }

    /**
     * A reference array access expression given a reference array
     * expression and an index number expression.
     */
    public static <E, A> RefArrAccessExp<E, A> refArrAccessExp(Class<E> cType, Class<A> aType, Exp<A> id, Exp<Integer> index) {
        return new RefArrAccessExp<>(id, index);
    }

    /*------------------------- Numbers ----------------------------*/

    /**
     * A free bool number.
     */
    public static BoolVal boolVal() {
        return new BoolVal();
    }

    /**
     * A free integer number.
     */
    public static IntVal intVal() {
        if (Config.ss == SearchStrategy.RANDOM) {
            return new IntVal();
        } else {
            return new IntVal(Config.ints);
        }
    }

    /**
     * An integer number between {@code low} (inclusive) and
     * {@code high} (exclusive), where {@literal low < high}.
     */
    public static IntVal intVal(int low, int high) {
        return new IntVal(low, high);
    }

    /**
     * A free long integer number.
     */
    public static LongVal longVal() {
        if (Config.ss == SearchStrategy.RANDOM) {
            return new LongVal();
        } else {
            return new LongVal(Config.longs);
        }
    }

    /**
     * A long integer number between {@code low} (inclusive) and
     * {@code high} (exclusive), where {@literal low < high}.
     */
    public static LongVal longVal(long low, long high) {
        return new LongVal(low, high);
    }

    /**
     * A free double number between -Double.MAX_VALUE and
     * Double.MAX_VALUE.
     */
    public static DoubleVal doubleVal() {
        if (Config.ss == SearchStrategy.RANDOM) {
            return new DoubleVal();
        } else {
            return new DoubleVal(Config.doubles);
        }
    }

    /**
     * An double number between {@code low} (inclusive) and
     * {@code high} (exclusive), where {@literal low < high}.
     */
    public static DoubleVal doubleVal(double low, double high) {
        return new DoubleVal(low, high);
    }

    /**
     * An immutable integer number node.
     */
    public static ImIntVal asInt(int val) {
        return new ImIntVal(val);
    }

    /**
     * An immutable bool number.
     */
    public static ImBoolVal asBool(boolean val) {
        return new ImBoolVal(val);
    }

    public static ImDoubleVal asDouble(double val) {
        return new ImDoubleVal(val);
    }

    /*--------------------------- Alt ------------------------------*/

    /**
     * Alternative statement.
     * <p>
     * Example usage:
     * {@code alt(ifStmt(...), whileStmt(...), assignStmt(...)).eval();}
     */
    public static Stmt alt(Stmt stmt1, Stmt stmt2, Stmt... stmts) {
        List<Stmt> list = new LinkedList<>();
        list.add(stmt1);
        list.add(stmt2);
        list.addAll(Arrays.asList(stmts));
        return new AltStmt(list);
    }

    @SafeVarargs
    public static Exp<Integer> alt(int exp1, Exp<Integer> exp2, Exp<Integer>... exps) {
        return alt(asInt(exp1), exp2, exps);
    }

    /**
     * Alternative expression.
     * <p>
     * Example usage:
     * {@code int x = alt(intVal(1, 2), intId(), intVal(3, 4)).eval();}
     */
    @SafeVarargs
    public static <T> Exp<T> alt(Exp<T> exp1, Exp<T> exp2, Exp<T>... exps) {
        List<Exp<T>> list = new LinkedList<>();
        list.add(exp1);
        list.add(exp2);
        list.addAll(Arrays.asList(exps));
        return new AltExp<>(list);
    }

    public static Exp<Integer> arithOrShift(Exp<Integer> left, Exp<Integer> right, boolean excludeDivOrMod) {
        if (!excludeDivOrMod) {
            return arithOrShift(left, right);
        }
        // use all operators except DIV or MOD
        AriOp[] ariOps = AriOp.values();
        ShiftOp[] shiftOps = ShiftOp.values();
        AriOrShiftOp[] ops = new AriOrShiftOp[ariOps.length - 2 + shiftOps.length];
        for (int i = 0; i < ariOps.length; i++) {
            if (ariOps[i] != AriOp.DIV && ariOps[i] != AriOp.MOD) {
                ops[i] = ariOps[i];
            }
        }
        System.arraycopy(shiftOps, 0, ops, ariOps.length - 2, shiftOps.length);
        return arithOrShift(left, right, ops);
    }

    public static Exp<Integer> arithOrShift(String left, int right, AriOrShiftOp... ops) {
        return arithOrShift(left, asInt(right), ops);
    }

    public static Exp<Integer> arithOrShift(int left, String right, AriOrShiftOp... ops) {
        return arithOrShift(asInt(left), right, ops);
    }

    public static Exp<Integer> arithOrShift(String left, Exp<Integer> right, AriOrShiftOp... ops) {
        return arithOrShift(asIntIdOrIntArrAccess(left), right, ops);
    }

    public static Exp<Integer> arithOrShift(Exp<Integer> left, String right, AriOrShiftOp... ops) {
        return arithOrShift(left, asIntIdOrIntArrAccess(right), ops);
    }

    public static Exp<Integer> arithOrShift(int left, Exp<Integer> right, AriOrShiftOp... ops) {
        return arithOrShift(asInt(left), right, ops);
    }

    public static Exp<Integer> arithOrShift(Exp<Integer> left, int right, AriOrShiftOp... ops) {
        return arithOrShift(left, asInt(right), ops);
    }

    /**
     * Arithmetic or shift expression, uniform each operator.
     */
    public static Exp<Integer> arithOrShift(Exp<Integer> left, Exp<Integer> right, AriOrShiftOp... ops) {
        if (ops.length == 0) {
            // use all operators
            AriOp[] ariOps = AriOp.values();
            ShiftOp[] shiftOps = ShiftOp.values();
            ops = new AriOrShiftOp[ariOps.length + shiftOps.length];
            System.arraycopy(ariOps, 0, ops, 0, ariOps.length);
            System.arraycopy(shiftOps, 0, ops, ariOps.length, shiftOps.length);
        }
        List<Exp<Integer>> exps = new LinkedList<>();
        for (AriOrShiftOp op : ops) {
            if (op instanceof AriOp) {
                exps.add(arithmetic(left, right, (AriOp) op));
            } else {
                // ShiftOp
                exps.add(shift(left, right, (ShiftOp) op));
            }
        }
        return new AltExp<>(exps);
    }

    /**
     * A free int variable or an int array access expression.
     */
    public static Exp<Integer> intIdOrIntArrAccessExp() {
        return alt(intId(), intArrAccessExp());
    }

    /**
     * A free long variable or a long array access expression.
     */
    public static Exp<Long> longIdOrLongArrAccessExp() {
        return alt(longId(), longArrAccessExp());
    }

    /**
     * A free double variable or a double array access expression.
     */
    public static Exp<Double> doubleIdOrArrAccess() {
        return alt(doubleId(),
                refArrAccessExp(double.class, double[].class));
    }

    /**
     * A int variable or an int array access expression given a range
     * of excluded choices.
     */
    public static Exp<Integer> intIdOrIntArrAccessExp(boolean exclude, String... ids) {
        return alt(new IntId(exclude, Arrays.asList(ids)), intArrAccessExp());
    }

    /*-------------------- Increment/decrement -------------------*/

    public static PreIncExp<Integer> preIncIntExp(String... ids) {
        return preIncExp(Integer.class, ids);
    }

    public static PreIncExp<Integer> preIncIntExp(boolean exclude, String... ids) {
        return preIncExp(Integer.class, exclude, ids);
    }

    public static <N extends Number> PreIncExp<N> preIncExp(Class<N> type, String... ids) {
        return preIncExp(refId(type, ids));
    }

    public static <N extends Number> PreIncExp<N> preIncExp(Class<N> type, boolean exclude, String... ids) {
        return preIncExp(refId(type, exclude, ids));
    }

    public static <N extends Number> PreIncExp<N> preIncExp(LHSExp<N> id) {
        return new PreIncExp<>(id);
    }

    /*----------------------- Assignment ---------------------------*/

    public static <T> AssignExp<T> assignExp(LHSExp<T> target, Exp<T> value) {
        return new AssignExp<>(target, value);
    }

    /*------------------------ Statements --------------------------*/

    public static <T extends Throwable> TryStmt<T> tryStmt(BlockStmt tryStmt, Class<T> exceptionType,
            BlockStmt catchBlock) {
        return new TryStmt<>(tryStmt, exceptionType, catchBlock, block());
    }

    public static <T extends Throwable> TryStmt<T> tryStmt(BlockStmt tryStmt, Class<T> exceptionType,
            BlockStmt catchBlock, BlockStmt finallyBlock) {
        return new TryStmt<>(tryStmt, exceptionType, catchBlock, finallyBlock);
    }

    public static BlockStmt block(Stmt... stmts) {
        return new BlockStmt(Arrays.asList(stmts));
    }

    public static IfStmt ifStmt(Exp<Boolean> condition, Stmt thenStmt) {
        return new IfStmt(condition, thenStmt);
    }

    public static IfStmt ifStmt(Exp<Boolean> condition, Stmt thenStmt, Stmt elseStmt) {
        return new IfStmt(condition, thenStmt, elseStmt);
    }

    public static WhileStmt whileStmt(Exp<Boolean> condition, Stmt body) {
        return new WhileStmt(condition, body);
    }

    public static <T> ExprStmt assignStmt(LHSExp<T> target, Exp<T> value) {
        return exprStmt(assignExp(target, value));
    }

    public static ExprStmt preIncIntStmt(boolean exclude, String... ids) {
        return exprStmt(preIncIntExp(exclude, ids));
    }

    public static ExprStmt preIncIntStmt(String... ids) {
        return exprStmt(preIncIntExp(ids));
    }

    public static <T> ExprStmt exprStmt(Exp<T> exp) {
        return new ExprStmt(exp);
    }
}
