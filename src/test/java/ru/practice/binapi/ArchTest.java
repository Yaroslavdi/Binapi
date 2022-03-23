package ru.practice.binapi;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("ru.practice.binapi");

        noClasses()
            .that()
                .resideInAnyPackage("ru.practice.binapi.service..")
            .or()
                .resideInAnyPackage("ru.practice.binapi.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..ru.practice.binapi.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
