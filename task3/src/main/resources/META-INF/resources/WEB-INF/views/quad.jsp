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
    <form:form method="post" action="solveQuad.html" modelAttribute="quad">
        <form:label path="a">A</form:label>
        <form:input path="a" />
        <br>
        <form:label path="b">B</form:label>
        <form:input path="b" />
        <br>
        <form:label path="c">C</form:label>
        <form:input path="c" />
        <br>
        <button type="submit">Solve Equation</button>
    </form:form>

</body>
</html>

