package com.mercaplus.arquitectura;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.ArchTag;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * LAB 2 - Reglas del nuevo modulo hexagonal de pedidos (com.mercaplus.pedidos).
 * Deben quedar en VERDE al terminar el laboratorio:  mvn test -Parquitectura -Dtest=ReglasHexagonalesTest
 * Estructura esperada:
 *   com.mercaplus.pedidos.dominio          -> entidades y reglas puras (sin Spring, sin JPA)
 *   com.mercaplus.pedidos.aplicacion       -> casos de uso y puertos (interfaces)
 *   com.mercaplus.pedidos.infraestructura  -> adaptadores REST, JPA, pasarela de pagos
 *   com.mercaplus.pedidos.infraestructura.config -> composicion (Spring): conecta adaptadores y casos de uso
 */
@ArchTag("arquitectura")
@AnalyzeClasses(packages = "com.mercaplus.pedidos", importOptions = ImportOption.DoNotIncludeTests.class)
class ReglasHexagonalesTest {

    @ArchTest
    static final ArchRule regla_de_dependencia = onionArchitecture()
            .domainModels("..pedidos.dominio..")
            .applicationServices("..pedidos.aplicacion..")
            .adapter("entrada-rest", "..pedidos.infraestructura.rest..")
            .adapter("salida-persistencia", "..pedidos.infraestructura.persistencia..")
            .adapter("salida-pagos", "..pedidos.infraestructura.pagos..")
            .adapter("salida-promociones", "..pedidos.infraestructura.promociones..")
            .withOptionalLayers(true)
            // La configuracion de Spring es la raiz de composicion: es el unico lugar que puede
            // conocer a la vez los casos de uso y los adaptadores concretos para conectarlos.
            .ignoreDependency(resideInAPackage("..pedidos.infraestructura.config.."), DescribedPredicate.alwaysTrue());

    @ArchTest
    static final ArchRule dominio_sin_frameworks = noClasses()
            .that().resideInAPackage("..pedidos.dominio..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..")
            .allowEmptyShould(true)
            .because("el dominio debe poder probarse y evolucionar sin el framework");

    @ArchTest
    static final ArchRule aplicacion_sin_frameworks_de_persistencia = noClasses()
            .that().resideInAPackage("..pedidos.aplicacion..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.springframework.data..", "org.springframework.web..")
            .allowEmptyShould(true);
}
