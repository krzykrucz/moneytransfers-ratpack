package com.krzykrucz.transfers;


import com.google.common.reflect.ClassPath;
import com.krzykrucz.transfers.adapters.rest.MoneyTransfersRestAPI;
import com.krzykrucz.transfers.appconfig.MoneyTransfersApplication;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DontIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.apache.http.conn.ssl.AbstractVerifier.countDots;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "com.krzykrucz.transfers", importOptions = DontIncludeTests.class)
public class LayerArchitectureTest {

    private final static Set<String> ALL_ADAPTER_PACKAGES = getAdapterPackages();

    @ArchTest
    public static final ArchRule layersShouldHaveProperDependencies =
            layeredArchitecture()
                    .layer("Application").definedBy("..application..")
                    .layer("Domain").definedBy("..service..")
                    .layer("Adapters").definedBy("..adapters..")

                    .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters")

                    .ignoreDependency(MoneyTransfersApplication.class, MoneyTransfersRestAPI.class);

    @ArchTest
    public void adaptersShouldNotDependOnEachOther(JavaClasses classes) {
        ALL_ADAPTER_PACKAGES.forEach(adapterPackage -> {

            final String[] otherAdapterPackages = ALL_ADAPTER_PACKAGES.stream()
                    .filter(aPackage -> !aPackage.equals(adapterPackage))
                    .toArray(String[]::new);

            noClasses()
                    .that()
                    .resideInAPackage(adapterPackage)
                    .should()
                    .accessClassesThat()
                    .resideInAnyPackage(otherAdapterPackages)
                    .check(classes);

        });
    }

    private static Set<String> getAdapterPackages() {
        Set<ClassPath.ClassInfo> classInfos = null;
        try {
            classInfos = ClassPath.from(MoneyTransfersApplication.class.getClassLoader())
                    .getTopLevelClassesRecursive("com.krzykrucz.transfers.adapters");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classInfos.stream()
                .map(ClassPath.ClassInfo::getPackageName)
                .filter(aPackage -> !aPackage.equals("com.krzykrucz.transfers.adapters"))
                .filter(aPackage -> countDots(aPackage) < 5)
                .collect(Collectors.toSet());
    }

}
