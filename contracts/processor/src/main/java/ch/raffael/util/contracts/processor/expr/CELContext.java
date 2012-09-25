package ch.raffael.util.contracts.processor.expr;

import javassist.CtClass;

import ch.raffael.util.contracts.Ensure;
import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.Nullable;
import ch.raffael.util.contracts.processor.ProcessingEnvironment;
import ch.raffael.util.contracts.processor.model.ClassInfo;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CELContext {

    private final ProcessingEnvironment env;
    private final ProblemReporter problemReporter;

    private final ClassInfo classInfo;
    private final CtClass targetClass;
    private final PackageRef pakkage;
    private final CtClass contractsClass;

    @Ensure({ "!targetClass.isAnnotation()", "!targetClass.isPrimitive()" })
    public CELContext(ProcessingEnvironment env, ProblemReporter problemReporter, ClassInfo classInfo, CtClass targetClass, CtClass contractsClass) {
        this.env = env;
        this.problemReporter = problemReporter;
        this.classInfo = classInfo;
        this.targetClass = targetClass;
        pakkage = new PackageRef(targetClass.getPackageName());
        this.contractsClass = contractsClass;
    }

    @NotNull
    public ClassInfo getClassInfo() {
        return classInfo;
    }

    @NotNull
    public CtClass getTargetClass() {
        return targetClass;
    }

    @NotNull
    public PackageRef getPackage() {
        return pakkage;
    }

    @NotNull
    public CtClass getContractsClass() {
        return contractsClass;
    }

    @Nullable
    public CtClass findClass(String name) {
        return env.findClass(name);
    }

    public void reportProblem(Problem problem) {
        problemReporter.report(problem);
    }

}
