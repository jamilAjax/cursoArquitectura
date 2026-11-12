package com.mercaplus.arquitectura;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.ArchTag;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * LAB 1 - Reglas de la arquitectura en capas que MercaPlus dice tener.
 * Ejecutar con:  mvn test -Parquitectura
 * Se espera que FALLEN sobre el codigo legado: cada falla es un hallazgo del diagnostico.
 */
@ArchTag("arquitectura")
@AnalyzeClasses(packages = "com.mercaplus", importOptions = ImportOption.DoNotIncludeTests.class)
class ReglasArquitecturaTest {

    @ArchTest
    static final ArchRule capas_respetadas = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Web").definedBy("..controller..")
            .layer("Aplicacion").definedBy("..service..")
            .layer("Datos").definedBy("..repository..")
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Aplicacion").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Datos").mayOnlyBeAccessedByLayers("Aplicacion");

    @ArchTest
    static final ArchRule el_modelo_no_depende_de_infraestructura = noClasses()
            .that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAnyPackage("..util..", "..controller..", "..service..")
            .because("las entidades de negocio no deben conocer pasarelas ni frameworks externos");

    @ArchTest
    static final ArchRule los_controladores_no_usan_jpa = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..")
            .because("la capa web no debe construir consultas a la base de datos");

    @ArchTest
    static final ArchRule la_web_no_expone_entidades = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..model..")
            .because("la API debe exponer DTO, no entidades JPA");
}
