<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page session="false"%>
<html>
<head>
<title>Show Employees</title>
</head>
<body>

	<h3 style="color: red;">Show All Employees</h3>
	<ul>
		<c:if test="${employees !=null}"></c:if>
		<c:forEach var="listValue" items="${employees}">
			<li>${listValue}</li>
		</c:forEach>
		<c:if test="${error ==null }">
			<c:out value="${error}"></c:out>
		</c:if>
	</ul>
</body>
</html>