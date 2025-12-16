<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
    <title>Detalle de Usuario - CineArchive</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/fragments/header.jsp" />

    <div class="container">
        <div class="detail-container">
            <!-- Sección Hero (Principal) -->
            <div class="detail-hero">
                <!-- Avatar del Usuario -->
                <div style="width: 300px;">
                    <div style="width: 250px; height: 250px; border-radius: 50%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; font-size: 120px; color: white; margin: 0 auto; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                        ${usuario.nombre.substring(0,1).toUpperCase()}
                    </div>
                </div>

                <!-- Información del Usuario -->
                <div class="detail-info">
                    <h1>${usuario.nombre}</h1>

                    <div class="detail-meta">
                        <c:choose>
                            <c:when test="${usuario.rol == 'ADMINISTRADOR'}">
                                <span class="badge badge-admin">Administrador</span>
                            </c:when>
                            <c:when test="${usuario.rol == 'GESTOR_INVENTARIO'}">
                                <span class="badge badge-gestor">Gestor Inventario</span>
                            </c:when>
                            <c:when test="${usuario.rol == 'ANALISTA_DATOS'}">
                                <span class="badge badge-analista">Analista Datos</span>
                            </c:when>
                            <c:when test="${usuario.rol == 'CHUSMA'}">
                                <span class="badge badge-chusma">Chusma</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-user">Usuario Regular</span>
                            </c:otherwise>
                        </c:choose>

                        <span>•</span>

                        <c:choose>
                            <c:when test="${usuario.activo}">
                                <span class="status-badge active">Activo</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge inactive">Inactivo</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Información Principal -->
                    <div class="detail-synopsis">
                        <h3>📧 Información de Contacto</h3>
                        <p><strong>Email:</strong> ${usuario.email}</p>
                        <p><strong>ID:</strong> #${usuario.id}</p>
                        <c:if test="${not empty usuario.fechaNacimiento}">
                            <p><strong>Fecha de Nacimiento:</strong> ${usuario.fechaNacimiento}</p>
                        </c:if>
                    </div>

                    <!-- Botones de Acción -->
                    <div class="action-buttons">
                        <c:if test="${puedeModificar}">
                            <button class="btn-primary" onclick="window.location.href='${pageContext.request.contextPath}/admin/usuarios/editar/${usuario.id}'">
                                ✏️ Editar Usuario
                            </button>

                            <c:choose>
                                <c:when test="${usuario.activo}">
                                    <button class="btn-secondary" onclick="confirmarDesactivar(${usuario.id})">
                                        🚫 Desactivar
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn-primary" onclick="confirmarActivar(${usuario.id})" style="background-color: #28a745;">
                                        ✅ Activar
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <button class="btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/admin/usuarios'">
                            ← Volver al Listado
                        </button>
                    </div>
                </div>
            </div>

            <!-- Información Adicional -->
            <section class="admin-section" style="margin-top: 40px;">
                <h2>📊 Información Adicional</h2>

                <div class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
                    <div class="stat-card">
                        <div class="stat-icon">📅</div>
                        <div class="stat-content">
                            <h3>Fecha de Registro</h3>
                            <p class="stat-number" style="font-size: 18px;">${usuario.fechaRegistro}</p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">🎭</div>
                        <div class="stat-content">
                            <h3>Rol del Sistema</h3>
                            <p class="stat-number" style="font-size: 16px;">
                                <c:choose>
                                    <c:when test="${usuario.rol == 'ADMINISTRADOR'}">
                                        Administrador
                                    </c:when>
                                    <c:when test="${usuario.rol == 'GESTOR_INVENTARIO'}">
                                        Gestor de Inventario
                                    </c:when>
                                    <c:when test="${usuario.rol == 'ANALISTA_DATOS'}">
                                        Analista de Datos
                                    </c:when>
                                    <c:otherwise>
                                        Usuario Regular
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">
                            <c:choose>
                                <c:when test="${usuario.activo}">✅</c:when>
                                <c:otherwise>🚫</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="stat-content">
                            <h3>Estado Actual</h3>
                            <p class="stat-number" style="font-size: 18px;">
                                <c:choose>
                                    <c:when test="${usuario.activo}">Activo</c:when>
                                    <c:otherwise>Inactivo</c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Permisos del Rol -->
            <section class="admin-section" style="margin-top: 30px;">
                <h2>🔐 Permisos del Rol</h2>

                <div class="detail-synopsis">
                    <c:choose>
                        <c:when test="${usuario.rol == 'ADMINISTRADOR'}">
                            <p>✅ <strong>Gestión completa de usuarios</strong> - Crear, editar y eliminar usuarios</p>
                            <p>✅ <strong>Gestión de contenido</strong> - Agregar y modificar películas/series</p>
                            <p>✅ <strong>Configuración del sistema</strong> - Acceso a todas las configuraciones</p>
                            <p>✅ <strong>Reportes y análisis</strong> - Acceso completo a estadísticas</p>
                        </c:when>
                        <c:when test="${usuario.rol == 'GESTOR_INVENTARIO'}">
                            <p>✅ <strong>Gestión de contenido</strong> - Agregar, editar y eliminar películas/series</p>
                            <p>✅ <strong>Gestión de inventario</strong> - Control de disponibilidad</p>
                            <p>❌ No puede gestionar usuarios</p>
                            <p>❌ No puede acceder a configuración del sistema</p>
                        </c:when>
                        <c:when test="${usuario.rol == 'ANALISTA_DATOS'}">
                            <p>✅ <strong>Acceso a reportes</strong> - Visualización de estadísticas</p>
                            <p>✅ <strong>Análisis de datos</strong> - Generación de informes</p>
                            <p>❌ No puede modificar contenido</p>
                            <p>❌ No puede gestionar usuarios</p>
                        </c:when>
                        <c:otherwise>
                            <p>✅ <strong>Explorar catálogo</strong> - Ver películas y series disponibles</p>
                            <p>✅ <strong>Alquilar contenido</strong> - Realizar alquileres</p>
                            <p>✅ <strong>Gestionar listas</strong> - Crear y administrar listas personales</p>
                            <p>❌ Sin permisos administrativos</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <!-- Advertencias -->
            <c:if test="${usuario.rol == 'ADMINISTRADOR'}">
                <section class="admin-section" style="margin-top: 30px; background-color: rgba(255, 193, 7, 0.1); border-left: 4px solid #ffc107;">
                    <h2>⚠️ Advertencias</h2>
                    <div class="detail-synopsis">
                        <p><strong>Este usuario tiene permisos de ADMINISTRADOR.</strong></p>
                        <p>Puede realizar cualquier acción en el sistema, incluyendo eliminar usuarios y modificar configuraciones críticas.</p>
                        <c:if test="${usuario.id == usuarioLogueado.id}">
                            <p style="color: #dc3545;"><strong>⚠️ Este es tu propio usuario.</strong> No puedes desactivarte o eliminarte a ti mismo.</p>
                        </c:if>
                    </div>
                </section>
            </c:if>
        </div>
    </div>

    <!-- Formularios ocultos -->
    <form id="formDesactivar" method="post" action="${pageContext.request.contextPath}/admin/usuarios/desactivar/${usuario.id}" style="display: none;">
    </form>

    <form id="formActivar" method="post" action="${pageContext.request.contextPath}/admin/usuarios/activar/${usuario.id}" style="display: none;">
    </form>

    <!-- JavaScript -->
    <script>
        // ID del usuario logueado
        var usuarioLogueadoId = ${usuarioLogueado.id};
        var usuarioDetalleId = ${usuario.id};

        function confirmarDesactivar(id) {
            // Validar que no sea el propio usuario
            if (id === usuarioLogueadoId || usuarioDetalleId === usuarioLogueadoId) {
                alert('🚫 NO PUEDES DESACTIVAR TU PROPIA CUENTA\n\n' +
                      'Esto te dejaría sin acceso al sistema.\n\n' +
                      'Si realmente necesitas desactivar esta cuenta, pide a otro administrador que lo haga.');
                return;
            }

            if (confirm('¿Estás seguro de que deseas desactivar este usuario?\n\n' +
                       'El usuario no podrá iniciar sesión hasta que sea reactivado.')) {
                document.getElementById('formDesactivar').submit();
            }
        }

        function confirmarActivar(id) {
            if (confirm('¿Estás seguro de que deseas activar este usuario?\n\n' +
                       'El usuario podrá iniciar sesión nuevamente.')) {
                document.getElementById('formActivar').submit();
            }
        }
    </script>
</body>
</html>

