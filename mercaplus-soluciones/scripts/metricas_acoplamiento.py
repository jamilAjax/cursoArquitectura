#!/usr/bin/env python3
"""
Métricas de acoplamiento por paquete (Robert C. Martin): Ca, Ce e inestabilidad I = Ce / (Ca + Ce).

Cuenta las dependencias entre paquetes leyendo los `import` del código principal (src/main/java).
Ca = cuántos paquetes dependen de este · Ce = de cuántos paquetes depende este.

Uso (desde la raíz del repositorio):
  python3 scripts/metricas_acoplamiento.py legado     # paquetes de primer nivel del monolito
  python3 scripts/metricas_acoplamiento.py pedidos    # subpaquetes del módulo hexagonal
"""
import re, sys
from pathlib import Path

RAIZ = Path("src/main/java")
BASE = "com.mercaplus."

VISTAS = {
    # El monolito: un componente por paquete de primer nivel (se excluye el módulo nuevo)
    "legado": ["controller", "service", "repository", "model", "util"],
    # El módulo hexagonal; "promociones", "model" y "repository" cuentan como dependencias externas
    "pedidos": ["pedidos.dominio", "pedidos.aplicacion", "pedidos.infraestructura.pagos",
                "pedidos.infraestructura.promociones", "pedidos.infraestructura.rest",
                "pedidos.infraestructura.persistencia", "pedidos.infraestructura.config",
                "promociones", "model", "repository"],
}
MOSTRAR = {"legado": VISTAS["legado"], "pedidos": VISTAS["pedidos"][:7]}

def componente(paquete, comps):
    # el componente con el prefijo más largo que contiene al paquete
    candidatos = [c for c in comps if paquete == BASE + c or paquete.startswith(BASE + c + ".")]
    return max(candidatos, key=len) if candidatos else None

def main(vista):
    comps = VISTAS[vista]
    deps = {c: set() for c in comps}
    for f in RAIZ.rglob("*.java"):
        texto = f.read_text(encoding="utf-8")
        m = re.search(r"^package\s+([\w.]+);", texto, re.M)
        if not m: continue
        origen = componente(m.group(1), comps)
        if origen is None: continue
        for imp in re.findall(r"^import\s+(?:static\s+)?([\w.]+)(?:\.\*)?;", texto, re.M):
            paquete = imp.rsplit(".", 1)[0]
            destino = componente(paquete, comps) or componente(imp, comps)
            if destino and destino != origen:
                deps[origen].add(destino)
    print(f"| Paquete | Ca | Ce | I | Depende de |\n|---|---|---|---|---|")
    for c in MOSTRAR[vista]:
        ca = sum(1 for o in comps if c in deps[o])
        ce = len(deps[c])
        i = ce / (ca + ce) if ca + ce else 0
        nombre = c.replace("pedidos.infraestructura.", "infraestructura.")
        print(f"| {nombre} | {ca} | {ce} | {i:.2f} | {', '.join(sorted(deps[c])) or '—'} |")

if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "legado")
