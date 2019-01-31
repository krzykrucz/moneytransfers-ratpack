package com.krzykrucz.transfers;


import com.krzykrucz.transfers.application.api.MoneyTransfersAPI;
import com.tngtech.archunit.core.importer.ImportOption.DontIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "com.krzykrucz.transfers", importOptions = DontIncludeTests.class)
public class LayerArchitectureTest {

    //    @ArchTest
    public static final ArchRule layersShouldHaveProperDependencies =
            layeredArchitecture()
                    .layer("Application").definedBy("..application..")
                    .layer("Domain").definedBy("..domain..")
                    .layer("Infrastructure").definedBy("..infrastructure..")

                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")

                    .ignoreDependency(MoneyTransfersApplication.class, MoneyTransfersAPI.class);

}
