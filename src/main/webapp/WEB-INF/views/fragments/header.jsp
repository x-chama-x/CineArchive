<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Header fragment reutilizable (solo EL básica) --%>
<header>
  <nav>
    <%-- Logo: CHUSMA va a /admin/usuarios, otros usuarios al catálogo --%>
    <c:choose>
      <c:when test="${sessionScope.usuarioLogueado.rol == 'CHUSMA'}">
        <a href="${pageContext.request.contextPath}/admin/usuarios" class="logo">CineArchive</a>
      </c:when>
      <c:otherwise>
        <a href="${not empty sessionScope.usuarioLogueado ? pageContext.request.contextPath.concat('/catalogo') : pageContext.request.contextPath.concat('/login')}" class="logo">CineArchive</a>
      </c:otherwise>
    </c:choose>

    <%-- Solo mostrar opciones de navegación si el usuario está logueado --%>
    <c:if test="${not empty sessionScope.usuarioLogueado}">
      <button class="menu-toggle">&#9776;</button>
      <div class="nav-links">

        <%-- Menú para CHUSMA: solo Ver Usuarios, Perfil y Salir --%>
        <c:if test="${sessionScope.usuarioLogueado.rol == 'CHUSMA'}">
          <a href="${pageContext.request.contextPath}/admin/usuarios" class="admin-link">👀 Ver Usuarios</a>
          <a href="${pageContext.request.contextPath}/perfil" class="user-profile">👤 Perfil</a>
          <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Salir</a>
        </c:if>

        <%-- Menú completo para otros roles --%>
        <c:if test="${sessionScope.usuarioLogueado.rol != 'CHUSMA'}">
          <a href="${pageContext.request.contextPath}/catalogo">Inicio</a>
          <a href="${pageContext.request.contextPath}/mi-lista">Mi Lista</a>
          <a href="${pageContext.request.contextPath}/para-ver">Para Ver</a>
          <a href="${pageContext.request.contextPath}/mis-alquileres">Alquileres</a>
          <a href="${pageContext.request.contextPath}/metodos-pago">💳 Métodos de Pago</a>

          <%-- Opciones específicas por rol --%>
          <c:if test="${sessionScope.usuarioLogueado.rol == 'ADMINISTRADOR'}">
            <a href="${pageContext.request.contextPath}/admin/usuarios" class="admin-link">👥 Panel Admin</a>
          </c:if>
          <c:if test="${sessionScope.usuarioLogueado.rol == 'GESTOR_INVENTARIO'}">
            <a href="${pageContext.request.contextPath}/inventario/panel" class="admin-link">📦 Inventario</a>
          </c:if>
          <c:if test="${sessionScope.usuarioLogueado.rol == 'ANALISTA_DATOS'}">
            <a href="${pageContext.request.contextPath}/reportes/panel" class="admin-link">📊 Reportes</a>
          </c:if>

          <a href="${pageContext.request.contextPath}/perfil" class="user-profile">👤 Perfil</a>
          <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Salir</a>
        </c:if>
      </div>
    </c:if>
  </nav>
</header>
