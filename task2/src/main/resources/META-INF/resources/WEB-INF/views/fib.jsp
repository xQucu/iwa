<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Page Title</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <h1>${message}</h1>

    <h2>Quad</h2>
    <form:form method="post" action="solveFib.html" modelAttribute="fib">
        <form:label path="n">A</form:label>
        <form:input path="n" />
        <button type="submit">Print fib</button>
    </form:form>

</body>
</html>


