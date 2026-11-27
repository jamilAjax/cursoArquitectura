package com.crediandes.arquitectura;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * Fitness functions del reto. Corren en cada build y NO se pueden desactivar ni debilitar.
 * Puedes AGREGAR reglas (y suma en la rubrica).
 */
@AnalyzeClasses(packages = "com.crediandes", importOptions = ImportOption.DoNotIncludeTests.class)
class ReglasArquitecturaTest {

    @ArchTest
    static final ArchRule regla_de_dependencia = onionArchitecture()
            .domainModels("..solicitudes.dominio..")
            .applicationServices("..solicitudes.aplicacion..")
            .adapter("rest", "..solicitudes.infraestructura.rest..")
            .adapter("persistencia", "..solicitudes.infraestructura.persistencia..")
            .adapter("buro", "..solicitudes.infraestructura.buro..")
            .adapter("listas", "..solicitudes.infraestructura.listas..")
            .withOptionalLayers(true)
            // infraestructura.config es la raiz de composicion: ahi se arman los casos de uso con sus
            // adaptadores (@Configuration/@Bean), porque la capa de aplicacion no puede usar Spring.
            // Es el unico paquete autorizado a conocer todas las capas.
            .ignoreDependency(resideInAPackage("..solicitudes.infraestructura.config.."), DescribedPredicate.alwaysTrue());

    @ArchTest
    static final ArchRule dominio_sin_frameworks = noClasses()
            .that().resideInAPackage("..solicitudes.dominio..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..", "jakarta.validation..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule aplicacion_sin_spring = noClasses()
            .that().resideInAPackage("..solicitudes.aplicacion..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..")
            .allowEmptyShould(true);
}
