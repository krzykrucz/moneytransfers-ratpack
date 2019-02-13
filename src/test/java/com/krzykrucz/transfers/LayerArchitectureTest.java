package com.krzykrucz.transfers;


import com.google.common.reflect.ClassPath;
import com.krzykrucz.transfers.application.api.MoneyTransfersAPI;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DontIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "com.krzykrucz.transfers", importOptions = DontIncludeTests.class)
public class LayerArchitectureTest {

    private final static Set<String> ALL_ADAPTER_PACKAGES = getAdapterPackages();

    @ArchTest
    public static final ArchRule layersShouldHaveProperDependencies =
            layeredArchitecture()
                    .layer("Application").definedBy("..application..")
                    .layer("Domain").definedBy("..domain..")
                    .layer("Infrastructure").definedBy("..infrastructure..")

                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")

                    .ignoreDependency(MoneyTransfersApplication.class, MoneyTransfersAPI.class);

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
                    .getTopLevelClassesRecursive("com.krzykrucz.transfers.infrastructure");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classInfos.stream()
                .map(ClassPath.ClassInfo::getPackageName)
                .filter(aPackage -> !aPackage.equals("com.krzykrucz.transfers.infrastructure"))
                .collect(Collectors.toSet());
    }

}
